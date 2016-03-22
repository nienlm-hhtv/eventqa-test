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
import com.hhtv.eventqa.adapter.EventHighestVoteQuestionAdapter;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.customView.FixedRecyclerView;
import com.hhtv.eventqa.helper.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa.helper.ultis.DeviceUltis;
import com.hhtv.eventqa.helper.ultis.UserUltis;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Result;
import com.hhtv.eventqa.model.question.Vote;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
public class EventHighestVoteFragment2 extends BaseFragment implements IOnAdapterInteractListener {
    public static EventHighestVoteFragment2 newInstance(int eventId, int userId) {
        EventHighestVoteFragment2 f = new EventHighestVoteFragment2();
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


    EventHighestVoteQuestionAdapter mAdapter = null;
    GridLayoutManager gridLayoutManager;


    public EventHighestVoteFragment2() {
    }

    @Nullable
    @Override
    @DebugLog
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question2, container, false);

        ButterKnife.bind(this, v);
        firstLoad = true;
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getRealContext(),1) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };

        mRecyclerView.setLayoutManager(gridLayoutManager);
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

    @OnClick(R.id.event_question_notfound_layout)
    void onNotfoundClicked() {
        processLoadQuestion(eventId, userId, true);
    }

    void changeViewOrder(boolean listView, boolean loading, boolean notfound) {
        mRecyclerView.setVisibility(listView ? View.VISIBLE : View.GONE);
        mLoadingLayout.setVisibility(loading ? View.VISIBLE : View.GONE);
        mNotfoundLayout.setVisibility(notfound ? View.VISIBLE : View.GONE);
    }

    public String updateData(final List<List<Result>> m) {
        final List<Result> new_questions = m.get(0);
        final List<Result> removed_questions = m.get(1);
        mAdapter.insertNewItemAtBottom(new_questions, new EventHighestVoteQuestionAdapter.IOnUpdateItemsComplete() {
            @Override
            public void onComplete() {
                if (mAdapter != null) {
                    mAdapter.removeItems(removed_questions, new EventHighestVoteQuestionAdapter.IOnUpdateItemsComplete() {
                        @Override
                        public void onComplete() {
                            mAdapter.updatePosition(m.get(2));
                        }
                    });
                }
            }
        });


        return "new: " + new_questions.size()
                + " remove: " + removed_questions.size();
    }

    /*public void instantInsert(String body){
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String now = new DateTime(DateTimeZone.UTC).toString(dtf);
        Result result = new Result(-1, "", body, now, -1, "", 0, 0, false, 1);
        mAdapter.insert(mAdapter.getmModel(), result, mAdapter.getmModel().size());
    }*/
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

        mSwipeView.setRefreshing(true);
        ApiEndpoint api = ApiService.build();
        Log.d("MYTAG", "EHVF vote, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));
        Call<Vote> call = api.vote(eventId, id, UserUltis.getUserId(getRealContext()), up, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mSwipeView.setRefreshing(false);
                Log.d("MYTAG", "EHVF vote: " + response.raw().request().url());
                Toast.makeText(getRealContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                if (mAdapter == null) {
                    mAdapter = new EventHighestVoteQuestionAdapter(new ArrayList<Result>(), EventHighestVoteFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                //processLoadQuestion(eventId, userId, false);
                ((MainActivity) getRealContext()).reloadContent(true);
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

    @Override
    public void scroll(int position) {
        int firstVisible = gridLayoutManager.findFirstVisibleItemPosition();
        //int lastVisible = gridLayoutManager.findLastVisibleItemPosition();

        if (firstVisible < 2)
            gridLayoutManager.scrollToPosition(0);
    }

    int eventId = -1, userId = -1;
    boolean firstLoad = true;

    @DebugLog
    public void processLoadQuestion(final int eventId, final int userid, boolean loading) {

        this.eventId = eventId;
        this.userId = userid;
        if (loading) {
            mSwipeView.setRefreshing(true);
            /*mRecyclerView.getEmptyView().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
            ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.textView2))
                    .setText(getResources().getString(R.string.loading));
            mRecyclerView.showEmptyView();*/
        }
        ApiEndpoint api = ApiService.build();
        Call<Question> call = api.getHighestVoteQuestion(eventId, userid, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                mSwipeView.setRefreshing(false);
                Log.d("MYTAG", "EHVF url: " + response.raw().request().url());

                if (response.body().getResults().size() == 0) {
                    changeViewOrder(false, false, true);
                } else if (mAdapter == null || mAdapter.getItemCount() == 0) {
                    changeViewOrder(true, false, false);
                    mAdapter = new EventHighestVoteQuestionAdapter(response.body().getResults(), EventHighestVoteFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);

                } else {
                    changeViewOrder(true, false, false);
                    if (!(mRecyclerView.getAdapter() instanceof EventHighestVoteQuestionAdapter)) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    List<List<Result>> list = reArrangeModels(response.body().getResults());
                    Log.d("TIMER", "EHVQF: " + updateData(list));

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "EHVF on fail ! " + t.getMessage());
                changeViewOrder(false, false, true);
            }
        });
    }

    public void processReLoadAllQuestion(final int eventId, final int userid) {

        this.eventId = eventId;
        this.userId = userid;
        changeViewOrder(false, true, false);
        ApiEndpoint api = ApiService.build();
        Call<Question> call = api.getHighestVoteQuestion(eventId, userid, DeviceUltis.getDeviceId(getRealContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                mSwipeView.setRefreshing(false);
                Log.d("MYTAG", "EHVF url: " + response.raw().request().url());
                changeViewOrder(true, false, false);
                if (response.body().getResults().size() == 0) {
                    changeViewOrder(false, false, true);
                } else {
                    mAdapter = new EventHighestVoteQuestionAdapter(response.body().getResults(), EventHighestVoteFragment2.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "EHVF on fail ! " + t.getMessage());
                changeViewOrder(false, false, true);
            }
        });
    }

    List<List<Result>> reArrangeModels(List<Result> questions) {
        List<List<Result>> finals = new ArrayList<>();
        ResultArrayList new_questions = new ResultArrayList();
        ResultArrayList removed_questions = new ResultArrayList();
        List<Result> model = mAdapter.mModel;

        new_questions.addAll(questions);
        new_questions.removeDiff(model);
        removed_questions.addAll(model);
        removed_questions.removeDiff(questions);
        finals.add(new_questions);
        finals.add(removed_questions);
        finals.add(questions);
        Log.d("MYTAG", "new: " + finals.get(0).size() + " removed: " + finals.get(1).size() + " original: "
                + finals.get(2).size());
        return finals;
    }

    public class ResultArrayList extends ArrayList<Result> {
        public boolean removeDiff(List<Result> collection) {
            for (Result result : collection
                    ) {
                int i = contain(result);
                if (i != -1)
                    remove(i);
            }
            return true;
        }

        public int contain(Result object) {
            for (Result r : this) {
                if (r.getId() == object.getId()) return indexOf(r);
            }
            return -1;
        }
    }
}
