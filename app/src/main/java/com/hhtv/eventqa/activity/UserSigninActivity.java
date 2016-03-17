package com.hhtv.eventqa.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.ultis.UserUltis;
import com.hhtv.eventqa.model.user.GetUserResponse;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 14/3/16.
 */
public class UserSigninActivity extends Activity {


    @Bind(R.id.backbtn)
    ImageView backbtn;
    @Bind(R.id.useremail)
    EditText useremail;
    @Bind(R.id.userpassword)
    EditText userpassword;
    @Bind(R.id.signinbtn)
    Button signinbtn;
    @Bind(R.id.signupbtn)
    TextView signupbtn;
    MaterialDialog loadingDialog, signinfailDialog, networkfailDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_usersignin_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);
        networkfailDialog = new MaterialDialog.Builder(UserSigninActivity.this)
                .title(R.string.network_error)
                .content(R.string.please_check_connection)
                .negativeText(R.string.dismiss).build();
        loadingDialog = new  MaterialDialog.Builder(this)
                .title(R.string.signin)
                .content(R.string.please_wait)
                .progress(true, 0)
                .cancelable(false).build();
        signinfailDialog = new MaterialDialog.Builder(UserSigninActivity.this)
                .title(R.string.signin_fail)
                .content(R.string.please_check_your_credential)
                .negativeText(R.string.dismiss).build();
    }

    private void onSigninbtnPressed(){
        loadingDialog.show();
        String useremailtext = useremail.getText().toString().trim();
        String userpasswordtext = userpassword.getText().toString().trim();
        ApiEndpoint api = ApiService.build();
        Call<GetUserResponse> call = api.signin(useremailtext, userpasswordtext);
        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Response<GetUserResponse> response, Retrofit retrofit) {
                loadingDialog.dismiss();
                if (response.body().isSuccess()){
                    UserUltis.setUserId(UserSigninActivity.this, Integer.parseInt(response.body().getCode()));
                    UserUltis.setUserEmail(UserSigninActivity.this, response.body().getEmail());
                    UserUltis.setUserName(UserSigninActivity.this, response.body().getUsername());
                    finish();
                }else{
                    signinfailDialog.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                loadingDialog.dismiss();
                networkfailDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SIGNUPREQCODE:
                Intent resultIntent = new Intent();
                if (data.getBooleanExtra("signup",false) == true){
                    resultIntent.putExtra("signup",true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }

                break;
            default:
                break;
        }
    }

    final int SIGNUPREQCODE = 101;
    private void onSignupbtnPressed(){
        Intent i = new Intent(this, UserSignupActivity.class);
        startActivityForResult(i, SIGNUPREQCODE);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
    private void onBackbtnPressed(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("signup",false);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        onBackbtnPressed();
    }

    @OnClick({R.id.signinbtn, R.id.signupbtn, R.id.backbtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signinbtn:
                onSigninbtnPressed();
                break;
            case R.id.signupbtn:
                onSignupbtnPressed();
                break;
            case R.id.backbtn:
                onBackbtnPressed();
                break;
        }
    }
}
