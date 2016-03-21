package com.hhtv.eventqa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiEndpoint;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.model.event.EventDetail;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 21/3/16.
 */
public class QRScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    ApiEndpoint api;
    MaterialDialog loadingDialog;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
        loadingDialog = new MaterialDialog.Builder(this)
                .title(R.string.please_wait)
                .content(R.string.getting_event_information)
                .theme(Theme.LIGHT)
                .progress(true, 1)
                .cancelable(false).build();
        api = ApiService.build();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("MYTAG2", rawResult.getContents());
        Log.v("MYTAG2", rawResult.getBarcodeFormat().getName());
        //mScannerView.stopCamera();
        if (rawResult.getBarcodeFormat().getName().equals("QRCODE")) {
            loadingDialog.show();
            Call<EventDetail> call = api.getEventDetail(rawResult.getContents());
            call.enqueue(new Callback<EventDetail>() {
                @Override
                public void onResponse(final Response<EventDetail> response, Retrofit retrofit) {
                    loadingDialog.dismiss();
                    if (response.body().getSuccess()) {
                        mScannerView.stopCamera();
                        gotoEventDetail(response.body());
                    } else {
                        mScannerView.stopCamera();
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
                                        mScannerView.stopCamera();
                                        dialog.dismiss();
                                        QRScannerActivity.this.finish();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        mScannerView.resumeCameraPreview(QRScannerActivity.this);
                                    }
                                }).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("MYTAG2", "QR code retrofit error: " + t.getMessage());
                    loadingDialog.dismiss();
                    mScannerView.stopCamera();
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
                                    mScannerView.stopCamera();
                                    dialog.dismiss();
                                    QRScannerActivity.this.finish();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    mScannerView.startCamera();
                                }
                            }).show();

                }
            });
        } else {
            mScannerView.resumeCameraPreview(this);
        }
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
}
