package com.hhtv.eventqa.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.model.event.EventDetail;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 29/2/16.
 */
public class HomeActivity extends Activity {

    @Bind(R.id.homeactivity_qrcode_btn)
    Button mQRCodeBtn;

    @Bind(R.id.homeactivity_edittext)
    EditText mEdittext;

    @Bind(R.id.homeactivity_directbtn)
    Button mDirectBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);
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

    @OnClick(R.id.homeactivity_qrcode_btn)
    void onQRCodeBtnPressed() {
        Intent i = new Intent(this, QRScannerActivity.class);
        startActivity(i);
    }


    @OnClick(R.id.homeactivity_directbtn)
    void onDirectBtnPressed() {
        try {
            String id = mEdittext.getText().toString();
            Call<EventDetail> call = ApiService.build().getEventDetail(id);
            call.enqueue(new Callback<EventDetail>() {
                @Override
                public void onResponse(final Response<EventDetail> response, Retrofit retrofit) {

                    if (response.body().getSuccess()) {
                        Intent i = new Intent(HomeActivity.this,MainActivity.class);
                        i.putExtra("curEvent", response.body());
                        startActivity(i);
                        //HomeActivity.this.finish();
                    } else {
                        Toast.makeText(HomeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    Log.d("MYTAG", "retrofit: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
