package com.kevinkoech.paympesa.api;

import com.kevinkoech.paympesa.models.LNMPaymentReponse;
import com.kevinkoech.paympesa.models.OAuthAccess;
import com.kevinkoech.paympesa.models.STKPush;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Mark Njung'e.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

public interface ApiService {

    @GET("oauth/v1/generate?grant_type=client_credentials")
    @Headers("cache-control: no-cache")
    Call<OAuthAccess> getAccessToken(@Header("Authorization") String authHeader);

    @POST("mpesa/stkpush/v1/processrequest")
    Call<LNMPaymentReponse> sendPush(@Header("Authorization") String authHeader, @Body STKPush stkPush);

    @GET("jobs/pending")
    Call<STKPush> getTasks(@Header("Authorization") String authHeader);
}
