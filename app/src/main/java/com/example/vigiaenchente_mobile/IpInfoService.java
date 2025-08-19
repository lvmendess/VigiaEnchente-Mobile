package com.example.vigiaenchente_mobile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface IpInfoService {
    @GET("json")
    Call<IpInfoResponse> getCidade(@Query("token") String token);
}
