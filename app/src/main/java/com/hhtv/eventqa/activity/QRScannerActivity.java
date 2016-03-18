package com.hhtv.eventqa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.model.event.EventDetail;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.livotov.labs.android.camview.ScannerLiveView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 29/2/16.
 */
public class QRScannerActivity extends FragmentActivity {

    @Bind(R.id.qrscanner_camera)
    ScannerLiveView mCamera;
    @Bind(R.id.qrscanner_progressbar)
    CircleProgressBar mLoading;
    ApiEndpoint api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        ButterKnife.bind(this);
        mLoading.setVisibility(View.INVISIBLE);
        api = ApiService.build();
        mCamera.startScanner();
        mCamera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {

            }

            @Override
            public void onScannerError(Throwable err) {

            }

            @Override
            public void onCodeScanned(String data) {
                Log.d("MYTAG", "data scanned: " + data);
                mLoading.setVisibility(View.VISIBLE);
                Call<EventDetail> call = api.getEventDetail(data);
                call.enqueue(new Callback<EventDetail>() {
                    @Override
                    public void onResponse(final Response<EventDetail> response, Retrofit retrofit) {
                        mLoading.setVisibility(View.INVISIBLE);
                        if (response.body().getSuccess()) {
                            mCamera.stopScanner();
                            gotoEventDetail(response.body());
                        } else {
                            mCamera.stopScanner();
                            new MaterialDialog
                                    .Builder(QRScannerActivity.this)
                                    .title(R.string.event_not_found)
                                    .content(response.body().getMessage())
                                    .negativeText(R.string.exit)
                                    .theme(Theme.LIGHT)
                                    .positiveText(R.string.rescan)
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            mCamera.stopScanner();
                                            dialog.dismiss();
                                            QRScannerActivity.this.finish();
                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            mCamera.startScanner();
                                        }
                                    }).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("MYTAG", "QR code retrofit error: " + t.getMessage());
                        mLoading.setVisibility(View.INVISIBLE);
                        mCamera.stopScanner();
                        new MaterialDialog
                                .Builder(QRScannerActivity.this)
                                .title(R.string.event_not_found)
                                .content(R.string.ev_not_found_or_wrong_qr)
                                .negativeText(R.string.exit)
                                .theme(Theme.LIGHT)
                                .positiveText(R.string.rescan)
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        mCamera.stopScanner();
                                        dialog.dismiss();
                                        QRScannerActivity.this.finish();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        mCamera.startScanner();
                                    }
                                }).show();

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    public void gotoEventDetail(EventDetail eventDetail) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("curEvent", eventDetail);
        startActivity(i);
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCamera.stopScanner();
    }
}
