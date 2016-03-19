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
import android.widget.TextView;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.activity.MainActivity;
import com.hhtv.eventqa.adapter.SimpleQuestionAdapter;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa.helper.ultis.DateTimeUltis;
import com.hhtv.eventqa.helper.ultis.DeviceUltis;
import com.hhtv.eventqa.helper.ultis.UserUltis;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Result;
import com.hhtv.eventqa.model.question.Vote;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 1/3/16.
 */
public class EventQuestionFragment extends BaseFragment implements IOnAdapterInteractListener {
    public static EventQuestionFragment newInstance(int eventId, int userId) {
        EventQuestionFragment f = new EventQuestionFragment();
        f.userId = userId;
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    UltimateRecyclerView mRecyclerView;


    SimpleQuestionAdapter mAdapter = null;
    GridLayoutManager gridLayoutManager;
    public EventQuestionFragment() {
    }

    @Nullable
    @Override
    @DebugLog
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question, container, false);

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
        mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), this);
        mRecyclerView.setEmptyView(getResources().getIdentifier("loading_view", "layout",
                getRealContext().getPackageName()));
        mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(200);

        mRecyclerView.enableDefaultSwipeRefresh(true);
        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstLoad = true;
                mRecyclerView.setRefreshing(true);
                processUpdateQuestion();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (addedItems.size() > 0){
                        Log.d("MYTAG","ONTOP");
                        mAdapter.insertNewItem(addedItems, new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                            @Override
                            public void onComplete() {
                                //mRecyclerView.scrollVerticallyTo(0);
                            }
                        });
                        addedItems.clear();
                    }
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
        if (firstVisible < visibleItem){
            gridLayoutManager.scrollToPosition(0);
        }
        Log.d("MYTAG", "f: " + firstVisible + " l: " + lastVisible + "p: " + position);
    }

    public List<Result> addedItems = new ArrayList<>();
    public String updateData(final Response<Vote> response) {
        if (response.body().getChanged_questions().size() > 0) {
            if (mAdapter != null) {
                mAdapter.upDateItemChanged(response.body().getChanged_questions());
            }
        }
        if (response.body().getNew_questions().size() > 0) {
            if (mAdapter != null) {
                addedItems.addAll(response.body().getNew_questions());
                /*if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0){
                    mAdapter.insertNewItem(response.body().getNew_questions(), new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                        @Override
                        public void onComplete() {
                            mRecyclerView.scrollVerticallyTo(0);
                        }
                    });
                    addedItems.clear();
                }*/

                mAdapter.insertNewItem(response.body().getNew_questions(), new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                    @Override
                    public void onComplete() {
                        //mRecyclerView.scrollVerticallyTo(0);
                        scroll(response.body().getNew_questions().size());
                    }
                });
            }
        }
        if (response.body().getRemoved_questions().size() > 0) {
            if (mAdapter != null) {
                mAdapter.removeItems(response.body().getRemoved_questions());
            }
        }
        return "new: " + response.body().getNew_questions().size()
                + " change: " + response.body().getChanged_questions().size()
                + " remove: " + response.body().getRemoved_questions().size()
                + " url: " + response.raw().request().url();
    }

    @DebugLog
    public void instantInsert(String body){
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String now = new DateTime(DateTimeZone.UTC).toString(dtf);
        final Result result = new Result(-1, "", body, now, -1, "", 0, 0, false, 1);
        /*mAdapter.insert(mAdapter.getmModel(), result, 0);
        gridLayoutManager.scrollToPosition(0);*/
        if (mAdapter != null && mAdapter.getItemCount() == 0){
            mRecyclerView.hideEmptyView();
        }
        mAdapter.insertNewItem(new ArrayList<Result>() {{
            add(result);
        }}, new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
            @Override
            public void onComplete() {
                //mRecyclerView.scrollVerticallyTo(0);
                //scroll(0);
            }
        });
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
        mAdapter.getmModel().get(pos).setIsVoted(true);
        /*final int questionId = question.getId();*/
        mRecyclerView.setRefreshing(true);
        ApiEndpoint api = ApiService.build();
        Log.d("MYTAG","vote, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));
        Call<Vote> call = api.vote(eventId, id, UserUltis.getUserId(getRealContext()), up, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mRecyclerView.setRefreshing(false);
                Toast.makeText(getRealContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                if (mAdapter == null) {
                    mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "vote: " + updateData(response));

            }

            @Override
            public void onFailure(Throwable t) {
                mRecyclerView.setRefreshing(false);
                Toast.makeText(getRealContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(getRealContext(), getResources().getString(R.string.you_already_vote_this_question),
                Toast.LENGTH_SHORT).show();
    }

    boolean isAdapterEmpty = true;
    public void processUpdateQuestion() {
        if (isAdapterEmpty || mAdapter.getItemCount() == 0){
            firstLoad = true;
            processLoadQuestion(eventId, userId, false);
            return;
        }
        ApiEndpoint api = ApiService.build();
        Log.d("MYTAG","EQF update, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));

        Call<Vote> call = api.updateQuestionsList(DeviceUltis.getDeviceId(getRealContext()), eventId,
                UserUltis.getUserId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mRecyclerView.hideEmptyView();
                mRecyclerView.setRefreshing(false);
                Log.d("MYTAG2","mAdapter: " + (mAdapter == null) );
                if (mAdapter == null) {
                    mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "EQF update: " + updateData(response));
                if (mAdapter.getItemCount() == 0) {
                    mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                            .setText(getResources().getString(R.string.no_question_tap_to_retry));
                    mRecyclerView.getEmptyView().findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userId, true);
                        }
                    });
                    mRecyclerView.showEmptyView();
                } else {
                    mRecyclerView.hideEmptyView();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG","EQF update fail: " + t.getMessage());
                mRecyclerView.setRefreshing(false);
                Toast.makeText(getRealContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    int eventId = -1, userId = -1;
    boolean firstLoad = true;
    ArrayList<Result> mModels;

    @DebugLog
    public void processLoadQuestion(final int eventId, final int userid, boolean loading) {
        Log.d("MYTAG2", "processLoadQuestion " + loading);
        if (firstLoad) {
            firstLoad = false;
        } else {
            return;
        }
        this.eventId = eventId;
        this.userId = userid;
        DateTimeUltis.setLastCheck(getRealContext());

        if (loading){
            mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
            ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                    .setText(getResources().getString(R.string.loading));
            mRecyclerView.showEmptyView();
        }
        ApiEndpoint api = ApiService.build();
        Call<Question> call = api.getAllQuestions(eventId, userid, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                Log.d("MYTAG", "EQF url: " + response.raw().request().url());
                mRecyclerView.hideEmptyView();
                if (response.body().getResults().size() == 0) {
                    isAdapterEmpty = true;
                    mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                            .setText(getResources().getString(R.string.no_question_tap_to_retry));
                    mRecyclerView.getEmptyView().findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userid, true);
                        }
                    });
                    mRecyclerView.showEmptyView();
                } else {
                    isAdapterEmpty = false;
                    mModels = new ArrayList<>();
                    mModels.addAll(response.body().getResults());
                    mAdapter = new SimpleQuestionAdapter(response.body().getResults(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "EQF on fail ! " + t.getMessage());
                mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                        .setText(getResources().getString(R.string.error_tap_to_retry));
                mRecyclerView.showEmptyView();
                mRecyclerView.getEmptyView().findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstLoad = true;
                        processLoadQuestion(eventId, userid, true);
                    }
                });
            }
        });
    }
}
