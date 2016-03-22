package com.hhtv.eventqa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.activity.MainActivity;
import com.hhtv.eventqa.adapter.EventQuestionAdapter;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.customView.FixedRecyclerView;
import com.hhtv.eventqa.helper.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa.helper.ultis.DateTimeUltis;
import com.hhtv.eventqa.helper.ultis.DeviceUltis;
import com.hhtv.eventqa.helper.ultis.UserUltis;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Result;
import com.hhtv.eventqa.model.question.Vote;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 1/3/16.
 */
public class EventQuestionFragment2 extends BaseFragment implements IOnAdapterInteractListener {
    public static EventQuestionFragment2 newInstance(int eventId, int userId) {
        EventQuestionFragment2 f = new EventQuestionFragment2();
        f.userId = userId;
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    FixedRecyclerView mRecyclerView;
    @Bind(R.id.event_question_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeView;
    @Bind(R.id.event_question_loading_layout)
    LinearLayout mLoadingLayout;
    @Bind(R.id.event_question_notfound_layout)
    LinearLayout mNotfoundLayout;


    EventQuestionAdapter mAdapter = null;
    GridLayoutManager gridLayoutManager;

    public EventQuestionFragment2() {
    }

    @Nullable
    @Override
    @DebugLog
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question2, container, false);

        ButterKnife.bind(this, v);
        firstLoad = true;
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getRealContext(), 1) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new EventQuestionAdapter(new ArrayList<Result>(), this);
        /*mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(200);*/


        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).reloadContent(false);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (addedItems.size() > 0){
                        Log.d("MYTAG","ONTOP");
                        mAdapter.insertNewItem(addedItems, new EventQuestionAdapter.IOnUpdateItemsComplete() {
                            @Override
                            public void onComplete() {
                                //mRecyclerView.scrollVerticallyTo(0);
                            }
                        });
                        addedItems.clear();
                    }
                }*/
                int firstPos = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0) {
                    mSwipeView.setEnabled(false);
                } else {
                    mSwipeView.setEnabled(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && ((MainActivity) getActivity()).isMFabShown()) {
                    ((MainActivity) getActivity()).hideFab(true);
                } else if (dy < 0 && !((MainActivity) getActivity()).isMFabShown()) {
                    ((MainActivity) getActivity()).hideFab(false);
                }
            }
        });
        processLoadQuestion(eventId, userId, true);
        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (firstLoad = false)
            firstLoad = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firstLoad = false)
            firstLoad = true;
    }

    @Override
    public void scroll(int position) {
        int firstVisible = gridLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = gridLayoutManager.findLastVisibleItemPosition();
        int visibleItem = lastVisible - firstVisible;
        //int scrollto = (firstVisible - position < 0)? 0 : firstVisible - position ;
        if (firstVisible < visibleItem) {
            gridLayoutManager.scrollToPosition(0);
        }
        Log.d("MYTAG", "f: " + firstVisible + " l: " + lastVisible + "p: " + position);
    }

    @OnClick(R.id.event_question_notfound_layout)
    void onNotfoundClicked() {
        processLoadQuestion(eventId, userId, true);
    }

    void changeViewOrder(boolean listView, boolean loading, boolean notfound) {
        mRecyclerView.setVisibility(listView ? View.VISIBLE : View.GONE);
        mLoadingLayout.setVisibility(loading ? View.VISIBLE : View.GONE);
        mNotfoundLayout.setVisibility(notfound ? View.VISIBLE : View.GONE);
    }

    public List<Result> addedItems = new ArrayList<>();

    boolean isScroll2Top;
    public String updateData(final Response<Vote> response) {
        if (response.body().getNew_questions().size() > 0){
            isScroll2Top = true;
        }else{
            isScroll2Top = false;
        }
        addedItems.addAll(response.body().getNew_questions());
        mAdapter.insertNewItem(response.body().getNew_questions(), new EventQuestionAdapter.IOnUpdateItemsComplete() {
            @Override
            public void onComplete() {
                mAdapter.removeItems(response.body().getRemoved_questions(), new EventQuestionAdapter.IOnUpdateItemsComplete() {
                    @Override
                    public void onComplete() {
                        mAdapter.upDateItemChanged(response.body().getChanged_questions());
                        if (isScroll2Top && gridLayoutManager.findFirstVisibleItemPosition() < 3) {
                            gridLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
                            isScroll2Top  = false;
                        }
                    }
                });

            }
        });
        return "new: " + response.body().getNew_questions().size()
                + " change: " + response.body().getChanged_questions().size()
                + " remove: " + response.body().getRemoved_questions().size()
                + " url: " + response.raw().request().url();
    }

    @DebugLog
    public void instantInsert(String body) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String now = new DateTime(DateTimeZone.UTC).toString(dtf);
        final Result result = new Result(-1, "", body, now, -1, UserUltis.getUserName(getRealContext()), 0, 0, false, 1);
        /*mAdapter.insert(mAdapter.getmModel(), result, 0);
        gridLayoutManager.scrollToPosition(0);*/
        if (mAdapter != null && mAdapter.getItemCount() == 0) {

        }
        mRecyclerView.setAdapter(mAdapter);
        mSwipeView.setRefreshing(true);
        mAdapter.insertNewItem(new ArrayList<Result>() {{
            add(result);
        }}, new EventQuestionAdapter.IOnUpdateItemsComplete() {
            @Override
            public void onComplete() {
                mSwipeView.setRefreshing(false);

                //mRecyclerView.scrollVerticallyTo(0);
                //scroll(0);
            }
        });
        //mAdapter.insert(mAdapter.getmModel(), result, 0);
    }

    @Override
    public void processVote(int id, int pos, final boolean up) {
        /*if (UserUltis.getUserId(getRealContext()) == -1) {
            Toast.makeText(getRealContext(), "Signin before vote !", Toast.LENGTH_SHORT).show();
            return;
        }*/

        /*if (question.getIsVoted()){

            return;
        }*/

        try {
            mAdapter.mModel.get(pos).setIsVoted(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*final int questionId = question.getId();*/
        mSwipeView.setRefreshing(true);
        ApiEndpoint api = ApiService.build();
        Log.d("MYTAG", "vote, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));
        Call<Vote> call = api.vote(eventId, id, UserUltis.getUserId(getRealContext()), up, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mSwipeView.setRefreshing(false);
                Toast.makeText(getRealContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                if (mAdapter == null) {
                    mAdapter = new EventQuestionAdapter(new ArrayList<Result>(), EventQuestionFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "vote: " + updateData(response));
                ((MainActivity) getActivity()).reloadContent(false);
            }

            @Override
            public void onFailure(Throwable t) {
                mSwipeView.setRefreshing(false);
                //Toast.makeText(getRealContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(getRealContext(), getResources().getString(R.string.you_already_vote_this_question),
                Toast.LENGTH_SHORT).show();
    }

    boolean isAdapterEmpty = true;

    public void processUpdateQuestion(final boolean loading) {
        if (loading) {
            mSwipeView.setRefreshing(true);
        }
        if (isAdapterEmpty || mAdapter.getItemCount() == 0) {
            firstLoad = true;
            processLoadQuestion(eventId, userId, false);
            return;
        }
        ApiEndpoint api = ApiService.build();
        //Log.d("MYTAG","EQF update, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));

        Call<Vote> call = api.updateQuestionsList(DeviceUltis.getDeviceId(getRealContext()), eventId,
                UserUltis.getUserId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                if (loading) {
                    mSwipeView.setRefreshing(false);
                }
                changeViewOrder(true, false, false);
                mSwipeView.setRefreshing(false);
                Log.d("MYTAG2", "mAdapter: " + (mAdapter == null));
                if (mAdapter == null) {
                    mAdapter = new EventQuestionAdapter(new ArrayList<Result>(), EventQuestionFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("TIMER", "EQF update: " + updateData(response));
                if (mAdapter.getItemCount() == 0) {
                    changeViewOrder(false, false, true);
                } else {
                    changeViewOrder(true, false, false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mSwipeView.setRefreshing(false);
                changeViewOrder(false, false, true);
                //Toast.makeText(getRealContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    int eventId = -1, userId = -1;
    boolean firstLoad = true;
    ArrayList<Result> mModels;

    @DebugLog
    public void processLoadQuestion(final int eventId, final int userid, boolean loading) {
        Log.d("MYTAG2", "processLoadQuestion " + loading);
        /*if (firstLoad) {
            firstLoad = false;
        } else {
            return;
        }*/
        this.eventId = eventId;
        this.userId = userid;
        DateTimeUltis.setLastCheck(getRealContext());

        if (loading) {
            changeViewOrder(false, true, false);
        }
        ApiEndpoint api = ApiService.build();
        Call<Question> call = api.getAllQuestions(eventId, userid, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                Log.d("MYTAG", "EQF url: " + response.raw().request().url());
                mSwipeView.setRefreshing(false);
                changeViewOrder(true, false, false);
                if (response.body().getResults().size() == 0) {
                    isAdapterEmpty = true;
                    changeViewOrder(false, false, true);
                } else {
                    changeViewOrder(true, false, false);
                    isAdapterEmpty = false;
                    mModels = new ArrayList<>();
                    mModels.addAll(response.body().getResults());
                    mAdapter = new EventQuestionAdapter(response.body().getResults(), EventQuestionFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mSwipeView.setRefreshing(false);
                Log.d("MYTAG", "EQF on fail ! " + t.getMessage());
                changeViewOrder(false, false, true);
            }
        });
    }
}
