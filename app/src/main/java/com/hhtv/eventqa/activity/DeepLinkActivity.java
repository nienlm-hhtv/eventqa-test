package com.hhtv.eventqa.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hhtv.eventqa.R;
import com.hhtv.eventqa.api.ApiService;
import com.hhtv.eventqa.model.event.EventDetail;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 17/3/16.
 */

public class DeepLinkActivity extends Activity {


    @Bind(R.id.progressBar2)
    ProgressBar progressBar2;
    @Bind(R.id.textView2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getIntent().getBooleanExtra(DeepLink.IS_DEEP_LINK, false)) {
            Bundle parameters = getIntent().getExtras();

            if (parameters != null && parameters.getString("qp") != null) {
                String queryParameter = parameters.getString("qp");
                Log.d("MYTAG", "deeolink: " + queryParameter);
                // Do something with the query parameter...
            }
        }*/
        setContentView(R.layout.activity_deeplink);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        int id = 0;
        try {
            id = Integer.parseInt(data.getQueryParameter("id"));
            Call<EventDetail> call = ApiService.build().getEventDetail(id + "");
            call.enqueue(new Callback<EventDetail>() {
                @Override
                public void onResponse(final Response<EventDetail> response, Retrofit retrofit) {
                    Log.d("MYTAG", response.raw().request().urlString());
                    if (response.body().getSuccess()) {
                        Intent i = new Intent(DeepLinkActivity.this,MainActivity.class);
                        i.putExtra("curEvent", response.body());
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        //HomeActivity.this.finish();
                    } else {
                        //Toast.makeText(DeepLinkActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar2.setVisibility(View.GONE);
                        textView2.setText(response.body().getMessage());
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    Log.d("MYTAG", "retrofit: " + t.getMessage());
                }
            });
        }catch (Exception e){
            progressBar2.setVisibility(View.GONE);
            textView2.setText("Error while getting data from server, please try again later !");
        }
    }
}
