package com.hhtv.eventqa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.activity.EventDetailActivity;
import com.hhtv.eventqa.adapter.SimpleQuestionAdapter;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.DateTimeUltis;
import com.hhtv.eventqa.helper.DeviceUltis;
import com.hhtv.eventqa.helper.UserUltis;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Result;
import com.hhtv.eventqa.model.question.Vote;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
public class EventQuestionFragment extends BaseFragment {
    public static EventQuestionFragment newInstance(int eventId, int userId) {
        EventQuestionFragment f = new EventQuestionFragment();
        f.userId = userId;
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    UltimateRecyclerView mRecyclerView;


    SimpleQuestionAdapter mAdapter = null;
    LinearLayoutManager linearLayoutManager;
    Timer mTimer;
    final int TIMER_DELAY = 15 * 1000;

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
        linearLayoutManager = new LinearLayoutManager(getRealContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), this);
        mRecyclerView.setEmptyView(getResources().getIdentifier("loading_view", "layout",
                getRealContext().getPackageName()));
        mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);

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
                                mRecyclerView.scrollVerticallyTo(0);
                            }
                        });
                        addedItems.clear();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && ((EventDetailActivity) getActivity()).isMFabShown()) {
                    ((EventDetailActivity) getActivity()).hideFab(true);
                } else if (dy < 0 && !((EventDetailActivity) getActivity()).isMFabShown()) {
                    ((EventDetailActivity) getActivity()).hideFab(false);
                }
            }
        });
        processLoadQuestion(eventId, userId);
        return v;
    }

    private void initTimer(){
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("MYTAG", "timer excecute at: " + DateTime.now().toString("hh:mm:ss"));
                processUpdateQuestion();
            }
        }, TIMER_DELAY, TIMER_DELAY);
    }

    private void cancelTimer(){
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        cancelTimer();
        if (firstLoad = false)
            firstLoad = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTimer == null){
            initTimer();
        }
        if (firstLoad = false)
            firstLoad = true;
    }


    public List<Result> addedItems = new ArrayList<>();
    public String updateData(Response<Vote> response) {
        if (response.body().getChanged_questions().size() > 0) {
            if (mAdapter != null) {
                mAdapter.upDateItemChanged(response.body().getChanged_questions());
            }
        }
        if (response.body().getNew_questions().size() > 0) {
            if (mAdapter != null) {
                addedItems.addAll(response.body().getNew_questions());
                Log.d("MYTAG:" ,"> " + linearLayoutManager.findFirstVisibleItemPosition() + " > "
                + mRecyclerView.canScrollVertically(-1));
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0){
                    mAdapter.insertNewItem(response.body().getNew_questions(), new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                        @Override
                        public void onComplete() {
                            mRecyclerView.scrollVerticallyTo(0);
                        }
                    });
                    addedItems.clear();
                }
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

    public void processVote(final int questionId, final boolean up) {
        if (UserUltis.getUserId(getRealContext()) == -1) {
            Toast.makeText(getRealContext(), "Signin before vote !", Toast.LENGTH_SHORT).show();
            return;
        }
        cancelTimer();
        mRecyclerView.setRefreshing(true);
        ApiEndpoint api = ApiService.build();
        Log.d("MYTAG","vote, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));
        Call<Vote> call = api.vote(questionId, UserUltis.getUserId(getRealContext()), up, DeviceUltis.getDeviceId(getRealContext()));
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
                initTimer();
            }
            @Override
            public void onFailure(Throwable t) {
                mRecyclerView.setRefreshing(false);
                Toast.makeText(getRealContext(), "Network error !", Toast.LENGTH_SHORT).show();
                initTimer();
            }
        });
    }

    public void processUpdateQuestion() {
        cancelTimer();
        ApiEndpoint api = ApiService.build();
        Log.d("MYTAG","update, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));
        Call<Vote> call = api.updateQuestionsList(DeviceUltis.getDeviceId(getRealContext()), eventId,
                UserUltis.getUserId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mRecyclerView.hideEmptyView();
                mRecyclerView.setRefreshing(false);
                if (mAdapter == null) {
                    mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "update: " + updateData(response));
                if (mAdapter.getItemCount() == 0) {
                    mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                            .setText("No questions, tap to retry !");
                    mRecyclerView.getEmptyView().findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userId);
                        }
                    });
                    mRecyclerView.showEmptyView();
                } else {
                    mRecyclerView.hideEmptyView();
                }
                initTimer();
            }
            @Override
            public void onFailure(Throwable t) {
                mRecyclerView.setRefreshing(false);
                Toast.makeText(getRealContext(), "Network error !", Toast.LENGTH_SHORT).show();
                initTimer();
            }
        });
    }


    int eventId = -1, userId = -1;
    boolean firstLoad = true;
    ArrayList<Result> mModels;

    @DebugLog
    public void processLoadQuestion(final int eventId, final int userid) {
        Log.d("MYTAG", eventId + " - " + userid + " - " + firstLoad);
        if (firstLoad) {
            firstLoad = false;
        } else {
            return;
        }
        this.eventId = eventId;
        this.userId = userid;
        DateTimeUltis.setLastCheck(getRealContext());
        mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
        ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                .setText("Loading");
        mRecyclerView.showEmptyView();
        ApiEndpoint api = ApiService.build();
        Call<Question> call = api.getAllQuestions(eventId, userid, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                Log.d("MYTAG", "url: " + response.raw().request().url());
                mRecyclerView.hideEmptyView();
                if (response.body().getResults().size() == 0) {
                    mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                            .setText("No questions, tap to retry !");
                    mRecyclerView.getEmptyView().findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userid);
                        }
                    });
                    mRecyclerView.showEmptyView();
                } else {
                    mModels = new ArrayList<Result>();
                    mModels.addAll(response.body().getResults());
                    mAdapter = new SimpleQuestionAdapter(response.body().getResults(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "on fail !");
                mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                        .setText("Error, tap to retry !");
                mRecyclerView.showEmptyView();
                mRecyclerView.getEmptyView().findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstLoad = true;
                        processLoadQuestion(eventId, userid);
                    }
                });
            }
        });
    }
}
