package com.hhtv.eventqa.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.fragment.EventQuestionFragment;
import com.hhtv.eventqa.fragment.EventQuestionFragment2;
import com.hhtv.eventqa.model.question.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hugo.weaving.DebugLog;

/**
 * Created by nienb on 7/3/16.
 */
public class QuestionAdapter2 extends RecyclerView.Adapter<QuestionAdapter2.ItemViewViewHolder>{
    private List<Result> mModel;
    private EventQuestionFragment2 mFragment;

    public QuestionAdapter2(List<Result> mModel, EventQuestionFragment2 mFragment) {
        this.mModel = mModel;
        this.mFragment = mFragment;
    }

    @Override
    public ItemViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_detail_item, parent, false);
        ItemViewViewHolder vh = new ItemViewViewHolder(v);
        return vh;
    }

    @DebugLog
    public void insertNewItem(List<Result> results) {
        for (Result result : results) {
            int p = isItemInModel(result);
            if (p == -1) {
                mModel.add(0, result);
                notifyItemInserted(0);
                Log.d("MYTAG", "insert item: " + result.getBody() + ". Size: " + mModel.size()
                        + " item: " + mModel.get(0).getBody());
            } else {
                updateModel(result,p);
            }
        }
    }

    @DebugLog
    public void removeItems(List<Result> results) {
        for (Result result : results) {
            int p = isItemInModel(result);
            if (p != -1) {
                mModel.remove(p);
                notifyItemRemoved(p);
            }
        }
    }



    @DebugLog
    public void upDateItemChanged(List<Result> results) {
        for (Result result : results) {
            int p = isItemInModel(result);
            //Log.d("MYTAG","update item in pos: " + p + " body: " + result.getBody());
            if (p != -1)
                updateModel(result,p);
        }
    }

    @DebugLog
    public void updateModel(Result item, int p) {
        Result r = mModel.get(p);
        r.setStatus(item.getStatus());
        r.setvote_down_count(item.getvote_down_count());
        r.setvote_up_count(item.getvote_up_count());
        r.setIsVoted(item.getIsVoted());
        p = mModel.indexOf(r);
        notifyItemChanged(p);
    }

    public int isItemInModel(Result item) {
        for (Result r : mModel
                ) {
            if (r.getBody().equals(item.getBody())) return mModel.indexOf(r);
        }
        return -1;
    }


    @Override
    public void onBindViewHolder(ItemViewViewHolder holder, final int position) {
        holder.mUserName.setText(mModel.get(position).getCreator_name());
        holder.mVoteUpCount.setText(mModel.get(position).getvote_up_count() + "");
        holder.mVoteDownCount.setText(mModel.get(position).getvote_down_count() + "");
        holder.mContent.setText(mModel.get(position).getBody());
        //holder.mPostFrom.setText(PT.format(getPostDate(mModel.get(position).getcreate_at())));
        holder.mPostFrom.setText(android.text.format.DateFormat.format("dd/MM/yyyy hh:mm",
                getPostDate(mModel.get(position).getcreate_at())));
        holder.mVoteUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MYTAG", "press vote at position: " + position + ". item: " + mModel.get(position).getBody());
                mFragment.processVote(mModel.get(position).getId(), true);
            }
        });
        holder.mVoteDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MYTAG", "press vote at position: " + position + ". item: " + mModel.get(position).getBody());
                mFragment.processVote(mModel.get(position).getId(), false);
            }
        });
    }
    public Date getPostDate(String src) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = format.parse(src);
            return date;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    public class ItemViewViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName, mVoteUpCount, mVoteDownCount, mContent, mPostFrom;
        ImageView mVoteUpBtn, mVoteDownBtn;
        View item_view;

        public ItemViewViewHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(
                    R.id.qdetail_username);
            mVoteUpCount = (TextView) itemView.findViewById(
                    R.id.qdetail_voteupcount);
            mVoteDownCount = (TextView) itemView.findViewById(
                    R.id.qdetail_votedowncount);
            mContent = (TextView) itemView.findViewById(
                    R.id.qdetail_content);
            mPostFrom = (TextView) itemView.findViewById(R.id.qdetail_postfrom);
            mVoteUpBtn = (ImageView) itemView.findViewById(R.id.qdetail_upbtn);
            mVoteDownBtn = (ImageView) itemView.findViewById(R.id.qdetail_downbtn);
            item_view = itemView.findViewById(R.id.qdetail_main);

        }
    }
}
