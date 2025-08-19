package com.example.vigiaenchente_mobile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClimaService {

    @GET("data/2.5/weather")
    Call<ClimaResponse> getClima(
            @Query("q") String cidade,
            @Query("units") String units,
            @Query("lang") String lang,
            @Query("appid") String apiKey
    );
}
