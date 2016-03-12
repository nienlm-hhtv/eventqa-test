package com.hhtv.eventqa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.UserUltis;
import com.hhtv.eventqa.model.user.CreateUserResponse;
import com.hhtv.eventqa.model.user.GetUserResponse;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class EventUserFragment extends BaseFragment{
    public static EventUserFragment newInstance(){
        return new EventUserFragment();
    }

    @Bind(R.id.user_layout_welcome)
    LinearLayout mWelcomeLayout;
    @Bind(R.id.user_layout_done)
    LinearLayout mDoneLayout;
    @Bind(R.id.user_layout_entername)
    LinearLayout mEnternameLayout;
    @Bind(R.id.ulw_edittext)
    EditText mUlw_edittext;
    @Bind(R.id.uln_edittext)
    EditText mUln_edittext;
    @Bind(R.id.ulw_nextbtn)
    Button mUlw_button;
    @Bind(R.id.uln_backbtn)
    Button mUln_backbtn;
    @Bind(R.id.uln_finishbtn)
    Button mUln_finishbtn;
    @Bind(R.id.uld_emailtext)
    TextView mUld_emailText;
    @Bind(R.id.uld_signoutbtn)
    Button mUld_btn;
    @Bind(R.id.ulw_progbar)
    CircleProgressBar mUlw_progbar;
    @Bind(R.id.uln_progbar)
    CircleProgressBar mUln_progbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_user_notsigned, container, false);
        ButterKnife.bind(this, v);
        if (UserUltis.getUserId(getRealContext()) == -1){
            showHideLayout(DisplayState.WELCOME);
        }else{
            showHideLayout(DisplayState.DONE);
            mUld_emailText.setText("You're logged in with email " + UserUltis.getUserEmail(getRealContext()));
        }
        return v;
    }
    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @OnClick(R.id.ulw_nextbtn)
    public void onNextBtnPressed(){
        if (!emailValidator(mUlw_edittext.getText().toString())){
            Toast.makeText(getRealContext(),"Please provide valid email !",Toast.LENGTH_SHORT).show();
        }else{
            mUlw_button.setVisibility(View.GONE);
            mUlw_progbar.setVisibility(View.VISIBLE);
            checkUser();
        }
    }
    @OnClick(R.id.uln_backbtn)
    public void onBackBtnPressed(){
        showHideLayout(DisplayState.WELCOME);
    }
    @OnClick(R.id.uln_finishbtn)
    public void onFinishBtnPressed(){
        if (mUln_edittext.getText().length() >= 6){
            mUln_backbtn.setVisibility(View.GONE);
            mUln_finishbtn.setVisibility(View.GONE);
            mUln_progbar.setVisibility(View.VISIBLE);
            createUser();
        }else{
            Toast.makeText(getRealContext(),"Name must has at least 6 characters !", Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.uld_signoutbtn)
    public void onSignoutBtnPressed(){
        UserUltis.setUserId(getRealContext(), -1);
        UserUltis.setUserEmail(getRealContext(), "");
        UserUltis.setUserName(getRealContext(), "");
        showHideLayout(DisplayState.WELCOME);
    }

    @DebugLog
    private void createUser(){
        ApiEndpoint api = ApiService.build();
        Call<CreateUserResponse> call = api.createUser(
                mUlw_edittext.getText().toString(), mUln_edittext.getText().toString()
        );
        call.enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(Response<CreateUserResponse> response, Retrofit retrofit) {
                if (response.body().getSuccess()){
                    UserUltis.setUserId(getRealContext(), response.body().getUserid());
                    UserUltis.setUserEmail(getRealContext(), response.body().getUseremail());
                    UserUltis.setUserName(getRealContext(), response.body().getUsername());
                    mUld_emailText.setText("You're logged in with email " + response.body().getUseremail());
                    showHideLayout(DisplayState.DONE);
                }else{
                    showHideLayout(DisplayState.ENTERNAME);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mUln_backbtn.setVisibility(View.VISIBLE);
                mUln_finishbtn.setVisibility(View.VISIBLE);
                mUln_progbar.setVisibility(View.GONE);
                Toast.makeText(getRealContext(), "Network error !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @DebugLog
    private void checkUser(){
        ApiEndpoint api = ApiService.build();
        Call<GetUserResponse> call = api.getUser(mUlw_edittext.getText().toString());
        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Response<GetUserResponse> response, Retrofit retrofit) {
                if (response.body().getSuccess()){
                    UserUltis.setUserId(getRealContext(), response.body().getCode());
                    UserUltis.setUserEmail(getRealContext(), response.body().getUseremail());
                    UserUltis.setUserName(getRealContext(), response.body().getUsername());
                    mUld_emailText.setText("You're logged in with email " + response.body().getUseremail());
                    showHideLayout(DisplayState.DONE);
                }else{
                    showHideLayout(DisplayState.ENTERNAME);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mUlw_button.setVisibility(View.VISIBLE);
                mUlw_progbar.setVisibility(View.GONE);
                Toast.makeText(getRealContext(), "Network error !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    enum  DisplayState{
        WELCOME,ENTERNAME,DONE
    }
    private void showHideLayout(DisplayState state){
        switch (state){
            case WELCOME:
                mWelcomeLayout.setVisibility(View.VISIBLE);
                mEnternameLayout.setVisibility(View.GONE);
                mDoneLayout.setVisibility(View.GONE);
                mUlw_button.setVisibility(View.VISIBLE);
                mUlw_progbar.setVisibility(View.GONE);
                break;
            case ENTERNAME:
                mWelcomeLayout.setVisibility(View.GONE);
                mEnternameLayout.setVisibility(View.VISIBLE);
                mDoneLayout.setVisibility(View.GONE);
                mUln_backbtn.setVisibility(View.VISIBLE);
                mUln_finishbtn.setVisibility(View.VISIBLE);
                mUln_progbar.setVisibility(View.GONE);
                break;
            case DONE:
                mWelcomeLayout.setVisibility(View.GONE);
                mEnternameLayout.setVisibility(View.GONE);
                mDoneLayout.setVisibility(View.VISIBLE);
                break;
        }
        /*switch (state){
            case WELCOME:
                if (currentState == DisplayState.ENTERNAME){
                    YoYo.with(Techniques.SlideOutRight).duration(300).playOn(mEnternameLayout);
                }else{
                    YoYo.with(Techniques.SlideOutRight).duration(300).playOn(mDoneLayout);
                }
                YoYo.with(Techniques.SlideInLeft).duration(300).playOn(mWelcomeLayout);
                mUlw_button.setVisibility(View.VISIBLE);
                mUlw_progbar.setVisibility(View.GONE);
                break;
            case ENTERNAME:
                if (currentState == DisplayState.WELCOME){
                    YoYo.with(Techniques.SlideOutLeft).duration(300).playOn(mWelcomeLayout);
                    YoYo.with(Techniques.SlideInRight).duration(300).playOn(mEnternameLayout);
                }else{
                    YoYo.with(Techniques.SlideOutRight).duration(300).playOn(mDoneLayout);
                    YoYo.with(Techniques.SlideInLeft).duration(300).playOn(mEnternameLayout);
                }
                mUln_backbtn.setVisibility(View.VISIBLE);
                mUln_finishbtn.setVisibility(View.VISIBLE);
                mUln_progbar.setVisibility(View.GONE);
                break;
            case DONE:
                if (currentState == DisplayState.ENTERNAME){
                    YoYo.with(Techniques.SlideOutLeft).duration(300).playOn(mEnternameLayout);
                }else{
                    YoYo.with(Techniques.SlideOutLeft).duration(300).playOn(mWelcomeLayout);
                }
                YoYo.with(Techniques.SlideInRight).duration(300).playOn(mDoneLayout);
                break;
        }
        currentState = state;*/
    }
}
