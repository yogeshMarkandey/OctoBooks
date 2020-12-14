package com.example.octobooks.networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static RetrofitClient instance;
    public Retrofit retrofit;
    private Gson gson;
    public static synchronized RetrofitClient getInstance(){
        Log.d(TAG, "getInstance: called");
        if(instance == null){
            Log.d(TAG, "getInstance: getting new instance");
            instance = new RetrofitClient();
        }
        return instance;
    }

    private RetrofitClient(){
        Log.d(TAG, "RetrofitClient: called");
        gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://homeworkapp.ai/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
