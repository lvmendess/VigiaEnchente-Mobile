package com.example.vigiaenchente_mobile;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit getIpClient(){
        return new Retrofit.Builder()
                .baseUrl("https://ipinfo.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getClimaClient(){
        return new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
