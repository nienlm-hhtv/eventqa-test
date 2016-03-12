package com.hhtv.eventqa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.model.event.EventDetail;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nienb on 1/3/16.
 */
public class EventDetailFragment extends BaseFragment{
    public static EventDetailFragment newInstance(EventDetail mModel){
        EventDetailFragment f= new EventDetailFragment();
        f.mModel =mModel;
        return f;
    }

    private EventDetail mModel;
    @Bind(R.id.f_evdetail_img)
    ImageView mImgView;
    @Bind(R.id.f_evdetail_name)
    TextView mTextName;
    @Bind(R.id.f_evdetail_creator)
    TextView mTextCreator;
    @Bind(R.id.f_evdetail_description)
    TextView mTextDes;
    @Bind(R.id.f_evdetail_statistic)
    TextView mTextStatistic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, v);
        Glide.with(getRealContext()).load(mModel.getImageLink()).into(mImgView);
        mTextName.setText(mModel.getName());
        mTextStatistic.setText("Total questions: " + mModel.gettotal_question() + ". Answerred: " + mModel.getanswered_question());
        PrettyTime prettyTime = new PrettyTime();
        mTextCreator.setText("Created by " + mModel.getcreator_name() + " " + prettyTime.format(getPostDate(mModel.getcreate_at())));
        //mTextCreator.setText("Created by " + mModel.getcreator_name() + " " + mModel.getcreate_at());
        mTextDes.setText(mModel.getDescription());
        return v;
    }

    public Date getPostDate(String src){
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
}
