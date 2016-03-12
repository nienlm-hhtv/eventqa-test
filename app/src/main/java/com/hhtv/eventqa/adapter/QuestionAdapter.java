package com.hhtv.eventqa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.model.question.Result;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

/**
 * Created by nienb on 1/3/16.
 */
public class QuestionAdapter extends UltimateViewAdapter<QuestionAdapter.ItemViewViewHolder> {
    private List<Result> mModel;
    private static final PrettyTime PT = new PrettyTime();
    private Context mContext;

    public QuestionAdapter(List<Result> mModel, Context mContext) {
        this.mModel = mModel;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final ItemViewViewHolder holder, final int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= mModel.size() : position < mModel.size()) && (customHeaderView != null ? position > 0 : true)) {
            holder.mUserName.setText(mModel.get(customHeaderView != null ? position - 1 : position).getName());
            holder.mVoteUpCount.setText(mModel.get(customHeaderView != null ? position - 1 : position).getvote_up_count() + "");
            holder.mVoteDownCount.setText(mModel.get(customHeaderView != null ? position - 1 : position).getvote_down_count() + "");
            holder.mContent.setText(mModel.get(customHeaderView != null ? position - 1 : position).getBody());

            holder.item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Result model = getItem(position);
                    Intent i = new Intent(mContext, ArticleDetailActivity.class);
                    i.putExtra("article", Parcels.wrap(model));
                    mContext.startActivity(i);*/
                }
            });
        }
    }

    @Override
    public int getAdapterItemCount() {
        return mModel.size();
    }

    @Override
    public ItemViewViewHolder getViewHolder(View view) {
        return new ItemViewViewHolder(view, false);
    }

    @Override
    public ItemViewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_detail_item, parent, false);
        ItemViewViewHolder vh = new ItemViewViewHolder(v, true);
        return vh;
    }


    public void insert(List<Result> result, int position) {
        for (Result r : result
                ) {
            insert(mModel, r, position);
        }

    }

    public void remove(int position) {
        remove(mModel, position);
    }

    public void clear() {
        clear(mModel);
    }

    @Override
    public void toggleSelection(int pos) {
        super.toggleSelection(pos);
    }

    @Override
    public void setSelected(int pos) {
        super.setSelected(pos);
    }

    @Override
    public void clearSelection(int pos) {
        super.clearSelection(pos);
    }


    public void swapPositions(int from, int to) {
        swapPositions(mModel, from, to);
    }


    @Override
    public long generateHeaderId(int position) {
        /*if (getItem(position).length() > 0)
            return getItem(position).charAt(0);
        else return -1;*/
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.stick_header_item, viewGroup, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        swapPositions(fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
        super.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        remove(position);
        super.onItemDismiss(position);
    }

    public void setOnDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }

    public class ItemViewViewHolder extends UltimateRecyclerviewViewHolder {
        TextView mUserName, mVoteUpCount, mVoteDownCount, mContent;
        ImageView mVoteUpBtn, mVoteDownBtn;
        View item_view;

        public ItemViewViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                mUserName = (TextView) itemView.findViewById(
                        R.id.qdetail_username);
                mVoteUpCount = (TextView) itemView.findViewById(
                        R.id.qdetail_voteupcount);
                mVoteDownCount = (TextView) itemView.findViewById(
                        R.id.qdetail_votedowncount);
                mContent = (TextView) itemView.findViewById(
                        R.id.qdetail_content);
                mVoteUpBtn = (ImageView) itemView.findViewById(R.id.qdetail_upbtn);
                mVoteDownBtn = (ImageView) itemView.findViewById(R.id.qdetail_downbtn);
                item_view = itemView.findViewById(R.id.qdetail_main);
            }

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public Result getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < mModel.size())
            return mModel.get(position);
        else return null;
    }
}