package com.hhtv.eventqa.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hhtv.eventqa.helper.Constant;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by nienb on 1/3/16.
 */
public class ApiService {
    static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.APIENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public static ApiEndpoint build() {
        return retrofit.create(ApiEndpoint.class);
    }

}
