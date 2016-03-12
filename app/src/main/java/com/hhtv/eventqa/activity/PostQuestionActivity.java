package com.hhtv.eventqa.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.DateTimeUltis;
import com.hhtv.eventqa.helper.UserUltis;
import com.hhtv.eventqa.model.question.Vote;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 4/3/16.
 */
public class PostQuestionActivity extends Activity {
    @Bind(R.id.pq_btncancel)
    ImageView mBtnCancel;
    @Bind(R.id.pq_btnsend)
    ImageView mBtnSend;
    @Bind(R.id.pq_textname)
    TextView mTextName;
    @Bind(R.id.pq_edittext)
    AppCompatEditText mEdittext;
    @Bind(R.id.pq_progressbar)
    CircleProgressBar mProg;
    int eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        ButterKnife.bind(this);
        eventId = getIntent().getIntExtra("eventid", -1);
        if (eventId == -1) {
            onBackPressed();
        }

        if (UserUltis.getUserId(this) == -1) {
            onBackPressed();
        }
        mTextName.setText("Signed in as: " + UserUltis.getUserName(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorAccent);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    @OnClick(R.id.pq_btncancel)
    public void onCancelBtnPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("post",false);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @OnClick(R.id.pq_btnsend)
    public void onSendBtnPressed() {
        if (mEdittext.getText().toString().length() > 0) {
            mBtnSend.setVisibility(View.GONE);
            mProg.setVisibility(View.VISIBLE);

            ApiEndpoint api = ApiService.build();
            Call<Vote> call = api.createQuestion(DateTimeUltis.getLastCheck(PostQuestionActivity.this),eventId, UserUltis.getUserId(PostQuestionActivity.this), mEdittext.getText().toString());
            call.enqueue(new Callback<Vote>() {
                @Override
                public void onResponse(Response<Vote> response, Retrofit retrofit) {
                    DateTimeUltis.setLastCheck(PostQuestionActivity.this);
                    if (response.body().getSuccess()) {
                        //Toast.makeText(PostQuestionActivity.this, "Question created !", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("post",true);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(PostQuestionActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        mBtnSend.setVisibility(View.VISIBLE);
                        mProg.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    DateTimeUltis.setLastCheck(PostQuestionActivity.this);
                    Toast.makeText(PostQuestionActivity.this, "Network error !", Toast.LENGTH_SHORT).show();
                    mBtnSend.setVisibility(View.VISIBLE);
                    mProg.setVisibility(View.GONE);
                }
            });
        }
    }
}
