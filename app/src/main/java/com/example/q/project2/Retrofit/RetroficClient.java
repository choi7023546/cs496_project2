package com.example.q.project2.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroficClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {
        if(instance == null)
            instance = new Retrofit.Builder()
                        .baseUrl("http://socrip3.kaist.ac.kr:9280")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
        return instance;

    }
}
