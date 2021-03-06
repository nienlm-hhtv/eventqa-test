/*
package com.hhtv.eventqa.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhtv.eventqa.fragment.EventHighestVoteFragment;
import com.hhtv.eventqa.helper.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.model.question.Result;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hugo.weaving.DebugLog;

*/
/**
 * Created by nienb on 1/3/16.
 *//*

public class SimpleQuestionAdapter extends UltimateViewAdapter<SimpleQuestionAdapter.ItemViewViewHolder> {
    private List<Result> mModel;
    private static final PrettyTime PT = new PrettyTime();
    private IOnAdapterInteractListener mFragment;

    public SimpleQuestionAdapter(List<Result> mModel, IOnAdapterInteractListener mFragment) {
        this.mModel = mModel;
        this.mFragment = mFragment;
    }

    public void setmModel(List<Result> mModel) {
        this.mModel = mModel;
    }

    @Override
    public long getItemId(int position) {
        return mModel.get(position).hashCode();
    }

    @Override
    public ItemViewViewHolder getViewHolder(View view) {
        return new ItemViewViewHolder(view, false);
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @DebugLog
    public void insertNewItem(List<Result> results, IOnUpdateItemsComplete i) {
        if (mModel.size() == 0){
            for (Result result: results
                    ) {
                mModel.add(0, result);
                notifyItemInserted(0);
                notifyItemRangeChanged(0, mModel.size());
                //insert(mModel, result, 0);
                */
/*notifyItemRangeInserted(0, mModel.size());*//*

                */
/*notifyItemRangeChanged(0, mModel.size());*//*

            }
            return;
        }
        for (Result result : results) {
            int p = isItemInModel(result.getBody());
            if (p == -1) {
                mModel.add(0, result);
                notifyItemInserted(0);
                notifyItemRangeChanged(0, mModel.size());
                Log.d("MYTAG", "insert item: " + result.getBody() + ". Size: " + mModel.size()
                        + " item: " + mModel.get(0).getBody());
                notifyItemRangeChanged(0, mModel.size());
            } else {
                updateModel(result, p);
            }
        }
        i.onComplete();
    }



    @DebugLog
    public void insertNewItemOnBottom(List<Result> results, IOnUpdateItemsComplete i) {
        for (Result result : results) {
            mModel.add(mModel.size(), result);
            notifyItemInserted(mModel.size());
            notifyItemRangeChanged(0, mModel.size());
            */
/*insert(mModel, result, mModel.size());*//*

        }
        i.onComplete();
    }

    @DebugLog
    public void removeItemsWithCallback(List<Result> results, IOnUpdateItemsComplete i) {
        for (Result result : results) {
            int p = isItemInModel(result.getId());
            if (p != -1) {
                mModel.remove(p);
                notifyItemRemoved(p);
                notifyItemRangeChanged(p, mModel.size());
            }
        }
        i.onComplete();
    }

    public void updatePosition(List<Result> newModels){
        boolean hasTop = false;
        for (int i = 0; i < newModels.size(); i++){
            int i1 = isItemInModel(newModels.get(i).getId());
            if(switchItem(i1, i) > 0 && i1 == 0)
                hasTop = true;
        }
        //if (hasTop)
            mFragment.scroll(0);
        updateContent(newModels);
    }

    public void updateContent(List<Result> newModels){
        for (int i = 0; i < mModel.size(); i++){
            try {
                Result oldModel = mModel.get(i);
                Result newModel = newModels.get(i);
                if (oldModel.getvote_up_count() == newModel.getvote_up_count()
                        && oldModel.getvote_down_count() == newModel.getvote_down_count()
                        && oldModel.getIsVoted() == newModel.getIsVoted())
                    continue;
                oldModel.setvote_up_count(newModel.getvote_up_count());
                oldModel.setvote_down_count(newModel.getvote_down_count());
                oldModel.setIsVoted(newModel.getIsVoted());
                notifyItemChanged(i);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }

    private int switchItem(int from, int to){
        if(from < 0 || to < 0)
            return -1;
        if (from  == to)
            return -1;
        Result r = mModel.remove(from);
        mModel.add(to, r);
        notifyItemMoved(from, to);
        return 1;
    }

    private void instantswitch(int from, int vote){
        Result r = mModel.remove(from);
        for (Result re: mModel
             ) {
            if (re.getvote_up_count() <= vote){
                int to = mModel.indexOf(re);
                mModel.add(to, r);
                notifyItemMoved(from, to);
                return;
            }

        }
    }

    public interface IOnUpdateItemsComplete{
        void onComplete();
    }

    @DebugLog
    public void removeItems(List<Result> results) {
        for (Result result : results) {
            int p = isItemInModel(result.getBody());
            if (p != -1) {
                */
/* remove(mModel, p); *//*

                mModel.remove(p);
                notifyItemRemoved(p);
                notifyItemRangeChanged(p, mModel.size());
            }
        }
    }



    @DebugLog
    public void upDateItemChanged(List<Result> results) {
        for (Result result : results) {
            int p = isItemInModel(result.getId());
            if (p != -1)
                updateModel(result,p);
        }
    }

    @DebugLog
    public void updateModel(Result item, int p) {
        Result r = mModel.get(p);
        r.setId(item.getId());
        r.setName(item.getName());
        r.setBody(item.getBody());
        r.setcreate_at(item.getcreate_at());
        r.setcreator_id(item.getcreator_id());
        r.setCreator_name(item.getCreator_name());
        r.setStatus(item.getStatus());
        r.setvote_down_count(item.getvote_down_count());
        r.setvote_up_count(item.getvote_up_count());
        r.setIsVoted(item.getIsVoted());
        p = mModel.indexOf(r);
        notifyItemChanged(p);

    }



    public int isItemInModel(int id) {
        for (Result r : mModel
                ) {
            if (r.getId() == id) return mModel.indexOf(r);
        }
        return -1;
    }

    public int isItemInModel(String body) {
        for (Result r : mModel
                ) {
            if (r.getBody().equals(body)) return mModel.indexOf(r);
        }
        return -1;
    }

    public List<Result> getmModel() {
        return mModel;
    }

    @Override
    public ItemViewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_detail_item, parent, false);
        ItemViewViewHolder vh = new ItemViewViewHolder(v, true);
        return vh;
    }

    @Override
    public int getAdapterItemCount() {
        return mModel.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    public Date getPostDate(String src) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = format.parse(src);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public void onBindViewHolder(final ItemViewViewHolder holder, final int position) {
        holder.mId.setText(mModel.get(position).getId() + "");
        holder.mUserName.setText((mModel.get(position).getCreator_name().equals(""))? "Guest" : mModel.get(position).getCreator_name());
        holder.mVoteUpCount.setText(mModel.get(position).getvote_up_count() + "");
        holder.mVoteDownCount.setText(mModel.get(position).getvote_down_count() + "");
        holder.mContent.setText(mModel.get(position).getBody());
        //holder.mPostFrom.setText(PT.format(getPostDate(mModel.get(position).getcreate_at())));
        holder.mPostFrom.setText(android.text.format.DateFormat.format("dd/MM/yyyy hh:mm",
                getPostDate(mModel.get(position).getcreate_at())));
        holder.mVoteUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = isItemInModel(holder.mContent.getText().toString().trim());
                if (pos != -1 && !mModel.get(pos).getIsVoted()){
                    holder.mVoteUpCount.setText((Integer.parseInt(holder.mVoteUpCount.getText().toString()) + 1) + "");
                    Log.d("MYTAG", "press vote at position: " + pos + ". item: " + mModel.get(pos).getBody());
                    if (mFragment instanceof EventHighestVoteFragment){
                        instantswitch(pos, mModel.get(pos).getvote_up_count() + 1);
                    }
                    mFragment.processVote(Integer.parseInt(holder.mId.getText().toString()), pos,  true);
                }else{
                    mFragment.showToast("");
                }

            }
        });
        holder.mVoteDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = isItemInModel(holder.mContent.getText().toString().trim());
                if (pos != -1 && !mModel.get(pos).getIsVoted()){

                    holder.mVoteDownCount.setText((Integer.parseInt(holder.mVoteDownCount.getText().toString()) + 1) + "");
                    Log.d("MYTAG", "press vote at position: " + pos + ". item: " + mModel.get(pos).getBody());
                    mFragment.processVote(Integer.parseInt(holder.mId.getText().toString()), pos, false);
                }else{
                    mFragment.showToast("");
                }

            }
        });
    }



    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }


    public class ItemViewViewHolder extends UltimateRecyclerviewViewHolder {
        TextView mUserName, mVoteUpCount, mVoteDownCount, mContent, mPostFrom, mId;
        ImageView mVoteUpBtn, mVoteDownBtn;
        View item_view;

        public ItemViewViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if(isItem){
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
                mId = (TextView) itemView.findViewById(R.id.qdetail_id);
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
}
*/
