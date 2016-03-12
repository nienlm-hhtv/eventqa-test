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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.activity.EventDetailActivity;
import com.hhtv.eventqa.adapter.QuestionAdapter2;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.DateTimeUltis;
import com.hhtv.eventqa.helper.DeviceUltis;
import com.hhtv.eventqa.helper.UserUltis;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Result;
import com.hhtv.eventqa.model.question.Vote;

import org.joda.time.DateTime;

import java.util.ArrayList;
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
public class EventQuestionFragment2 extends BaseFragment {
    public static EventQuestionFragment2 newInstance(int eventId, int userId) {
        EventQuestionFragment2 f = new EventQuestionFragment2();
        f.userId = userId;
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.event_question_loadingview)
    LinearLayout mLoadingView;
    @Bind(R.id.event_question_swipe_refresh_layout)
    SwipeRefreshLayout mRefreshView;

    QuestionAdapter2 mAdapter = null;
    LinearLayoutManager linearLayoutManager;
    Timer mTimer;
    final int TIMER_DELAY = 20 * 1000;

    public EventQuestionFragment2() {
    }

    private void showLoadingView(boolean show){
        mRecyclerView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mLoadingView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }


    @Nullable
    @Override
    @DebugLog
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question2, container, false);

        ButterKnife.bind(this, v);
        firstLoad = true;
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getRealContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new QuestionAdapter2(new ArrayList<Result>(), this);
        showLoadingView(true);
        mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);

        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstLoad = true;
                processUpdateQuestion();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
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
            mTimer.purge();
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


    public String updateData(Response<Vote> response) {
        if (response.body().getChanged_questions().size() > 0) {
            if (mAdapter != null) {
                mAdapter.upDateItemChanged(response.body().getChanged_questions());
            }
        }
        if (response.body().getNew_questions().size() > 0) {
            if (mAdapter != null) {
                mRecyclerView.scrollToPosition(0);
                mAdapter.insertNewItem(response.body().getNew_questions());
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
        mRefreshView.setRefreshing(true);
        ApiEndpoint api = ApiService.build();
        Call<Vote> call = api.vote(questionId, UserUltis.getUserId(getRealContext()), up, DateTimeUltis.getLastCheck(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mRefreshView.setRefreshing(false);
                Toast.makeText(getRealContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                if (mAdapter == null) {
                    mAdapter = new QuestionAdapter2(new ArrayList<Result>(), EventQuestionFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "vote: " + updateData(response));
                initTimer();
            }
            @Override
            public void onFailure(Throwable t) {
                mRefreshView.setRefreshing(false);
                Toast.makeText(getRealContext(), "Network error !", Toast.LENGTH_SHORT).show();
                initTimer();
            }
        });
    }

    public void processUpdateQuestion() {
        cancelTimer();
        ApiEndpoint api = ApiService.build();
        Call<Vote> call = api.updateQuestionsList(DateTimeUltis.getLastCheck(getRealContext()), eventId,
                UserUltis.getUserId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mRefreshView.setRefreshing(false);
                if (mAdapter == null) {
                    mAdapter = new QuestionAdapter2(new ArrayList<Result>(), EventQuestionFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "update: " + updateData(response));
                if (mAdapter.getItemCount() == 0) {
                    mLoadingView.findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    ((TextView) mLoadingView.findViewById(R.id.textView2))
                            .setText("No questions, tap to retry !");
                    mLoadingView.findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userId);
                        }
                    });
                    showLoadingView(true);
                } else {
                    showLoadingView(false);
                }
                initTimer();
            }
            @Override
            public void onFailure(Throwable t) {
                mRefreshView.setRefreshing(false);
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

        mLoadingView.findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
        ((TextView) mLoadingView.findViewById(R.id.textView2))
                .setText("Loading");
        showLoadingView(true);
        ApiEndpoint api = ApiService.build();
        Call<Question> call = api.getAllQuestions(eventId, userid, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                Log.d("MYTAG", "url: " + response.raw().request().url());
                showLoadingView(false);
                if (response.body().getResults().size() == 0) {
                    mLoadingView.findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    ((TextView) mLoadingView.findViewById(R.id.textView2))
                            .setText("No questions, tap to retry !");
                    mLoadingView.findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userid);
                        }
                    });
                    showLoadingView(false);
                } else {
                    mModels = new ArrayList<Result>();
                    mModels.addAll(response.body().getResults());
                    mAdapter = new QuestionAdapter2(response.body().getResults(), EventQuestionFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "on fail !");
                mLoadingView.findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                ((TextView) mLoadingView.findViewById(R.id.textView2))
                        .setText("Error, tap to retry !");
                showLoadingView(false);
                mLoadingView.findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
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
