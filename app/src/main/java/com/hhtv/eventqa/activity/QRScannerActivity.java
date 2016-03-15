package com.hhtv.eventqa.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
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

    Gson gson;
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
                //Toast.makeText(QRScannerActivity.this, data, Toast.LENGTH_SHORT).show();
                Log.d("MYTAG", "data scanned: " + data);
                mLoading.setVisibility(View.VISIBLE);
                Call<EventDetail> call = api.getEventDetail(data);
                call.enqueue(new Callback<EventDetail>() {
                    @Override
                    public void onResponse(final Response<EventDetail> response, Retrofit retrofit) {
                        mLoading.setVisibility(View.INVISIBLE);
                        if (response.body().getSuccess()) {
                            /*PostQuestionFragment f = PostQuestionFragment.newInstance(response.body(), QRScannerActivity.this);
                            FragmentManager fm = getSupportFragmentManager();
                            f.show(fm, "");*/
                            mCamera.stopScanner();
                            /*AlertDialog.Builder builder =
                                    new AlertDialog.Builder(QRScannerActivity.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Event found !");
                            builder.setMessage("Event " + response.body().getName() + " found ! Would you like to participate ?");
                            builder.setPositiveButton("Participate", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    gotoEventDetail(response.body());
                                }
                            });
                            builder.setNegativeButton("Rescan", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mCamera.startScanner();
                                    dialog.dismiss();
                                }
                            });
                            builder.show();*/

                            gotoEventDetail(response.body());
                        } else {
                            /*Toast.makeText(QRScannerActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            QRScannerActivity.this.finish();*/
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
                        Log.d("MYTAG", "retrofit: " + t.getMessage());
                        mLoading.setVisibility(View.INVISIBLE);
                        //Toast.makeText(QRScannerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        //QRScannerActivity.this.finish();
                        mCamera.stopScanner();
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(QRScannerActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Event not found !");
                        builder.setMessage("Network error or incorrect QR code, please try again !");
                        builder.setPositiveButton("Rescan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mCamera.startScanner();
                            }
                        });
                        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCamera.stopScanner();
                                dialog.dismiss();
                                QRScannerActivity.this.finish();
                            }
                        });
                        builder.show();

                    }
                });
            }
        });
    }


    public void gotoEventDetail(EventDetail eventDetail) {
        Intent i = new Intent(this, EventDetailActivity.class);
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
