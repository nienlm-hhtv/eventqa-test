package com.hhtv.eventqa.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.helper.ultis.GeneralHelper;
import com.hhtv.eventqa.model.user.CreateUserResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 15/3/16.
 */
public class UserSignupActivity extends Activity {

    @Bind(R.id.backbtn)
    ImageView backbtn;
    @Bind(R.id.useremail)
    EditText useremail;
    @Bind(R.id.username)
    EditText username;
    /*@Bind(R.id.userpassword)
    EditText userpassword;
    @Bind(R.id.userpasswordconfirm)
    EditText userpasswordconfirm;*/
    @Bind(R.id.signupbtn)
    Button signupbtn;
    @Bind(R.id.signinbtn)
    TextView signinbtn;
    MaterialDialog loadingDialog, signupfailDialog, networkfailDialog, signupokDialog;

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
        setContentView(R.layout.activity_usersignup_main);
        ButterKnife.bind(this);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);*/
        networkfailDialog = new MaterialDialog.Builder(UserSignupActivity.this)
                .title(R.string.network_error)
                .content(R.string.please_check_connection)
                .negativeText(R.string.dismiss).build();
        loadingDialog = new  MaterialDialog.Builder(this)
                .title(R.string.create_account)
                .content(R.string.please_wait)
                .progress(true, 0)
                .cancelable(false).build();
        signupfailDialog = new MaterialDialog.Builder(UserSignupActivity.this)
                .title(R.string.signup_fail)
                .content(R.string.please_check_your_credential)
                .negativeText(R.string.dismiss).build();
        signupokDialog = new MaterialDialog.Builder(UserSignupActivity.this)
                .title(R.string.successful)
                .content(R.string.please_check_your_credential)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        onBackbtnPressed(true);
                    }
                })
                .negativeText(R.string.dismiss).build();
    }


    private void onSignupbtnPressed(){
        if (!validateInput())
            return;
        loadingDialog.show();
        final String useremailtext = useremail.getText().toString().trim();
        //final String userpasswordtext = userpassword.getText().toString().trim();
        final String usernametext = username.getText().toString().trim();
        ApiEndpoint api = ApiService.build();
        Call<CreateUserResponse> call = api.signup(usernametext, useremailtext);
        call.enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(Response<CreateUserResponse> response, Retrofit retrofit) {
                loadingDialog.dismiss();
                if (response.body().isSuccess()) {
                    /*UserUltis.setUserId(UserSignupActivity.this, response.body().getCode());
                    UserUltis.setUserEmail(UserSignupActivity.this, response.body().getUseremail());
                    UserUltis.setUserName(UserSignupActivity.this, response.body().getUsername());
                    onBackbtnPressed(true);*/

                    signupokDialog.setContent(response.body().getMsg());
                    signupokDialog.show();

                } else {
                    String mess;
                    /*switch (response.body().getCode()){
                        case -1:
                            mess = getResources().getString(R.string.email_already_taken).replace("{email}",useremailtext);
                            break;
                        default:

                    }*/
                    mess = response.body().getMsg();
                    signupfailDialog.setContent(mess);
                    signupfailDialog.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                loadingDialog.dismiss();
                networkfailDialog.show();
            }
        });
    }

    private boolean validateInput(){
        String useremailText = useremail.getText().toString();
        String usernameText = username.getText().toString();
        /*String userpasswordText = userpassword.getText().toString();
        String userpasswordconfirmText = userpasswordconfirm.getText().toString();*/
        useremail.setError(null);
        username.setError(null);
        /*userpassword.setError(null);
        userpasswordconfirm.setError(null);*/
        boolean isValidate = true;
        if (!GeneralHelper.emailValidator(useremailText)){
            isValidate = false;
            useremail.setError(getResources().getString(R.string.incorrect_email));
        }
        if (usernameText.trim().length() == 0){
            isValidate = false;
            username.setError(getResources().getString(R.string.username_cannot_be_blank));
        }
        /*if (userpasswordText.trim().length() < 6){
            isValidate = false;
            userpassword.setError(getResources().getString(R.string.password_must_has_atleast_6_letters));
        }
        if (!userpasswordconfirmText.equals(userpasswordText)) {
            isValidate = false;
            userpasswordconfirm.setError(getResources().getString(R.string.password_confirmation_incorrect));
        }*/
        return isValidate;
    }

    private void onBackbtnPressed(boolean isSignedup){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("signup",isSignedup);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        onBackbtnPressed(false);
    }

    @OnClick({R.id.backbtn, R.id.signupbtn, R.id.signinbtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backbtn:
            case R.id.signinbtn:
                onBackbtnPressed(false);
                break;
            case R.id.signupbtn:
                onSignupbtnPressed();
                break;

        }
    }
}
