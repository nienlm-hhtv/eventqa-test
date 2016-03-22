package com.hhtv.eventqa.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhtv.eventqa.R;

import com.hhtv.eventqa.helper.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa.model.question.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hugo.weaving.DebugLog;

/**
 * Created by nienb on 21/3/16.
 */
public class EventQuestionAdapter extends RecyclerView.Adapter<EventQuestionAdapter.ItemViewViewHolder> {
    public List<Result> mModel;
    public IOnAdapterInteractListener mFragment;

    public EventQuestionAdapter(List<Result> mModel, IOnAdapterInteractListener mFragment) {
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
    public void onBindViewHolder(final ItemViewViewHolder holder, int position) {
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
                    /*if (mFragment instanceof EventHighestVoteFragment){
                        instantswitch(pos, mModel.get(pos).getvote_up_count() + 1);
                    }*/
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
            } else {
                updateModel(result, p);
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
    public void removeItems(List<Result> results, IOnUpdateItemsComplete i) {
        for (Result result : results) {
            int p = isItemInModel(result.getBody());
            if (p != -1) {
                /* remove(mModel, p); */
                mModel.remove(p);
                //notifyDataSetChanged();
                //notifyItemRemoved(p);
                //notifyItemRangeChanged(p, mModel.size());
                notifyItemRemoved(p);
                notifyItemRangeChanged(p,getItemCount());
            }
        }
        i.onComplete();
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
    public class ItemViewViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName, mVoteUpCount, mVoteDownCount, mContent, mPostFrom, mId;
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
            mId = (TextView) itemView.findViewById(R.id.qdetail_id);
            item_view = itemView.findViewById(R.id.qdetail_main);
        }

    }
}
