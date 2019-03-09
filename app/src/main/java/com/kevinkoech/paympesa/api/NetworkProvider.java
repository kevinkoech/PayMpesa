package com.kevinkoech.paympesa.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mark Njung'e.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

public class NetworkProvider {
    private static final String API_URL = "https://sandbox.safaricom.co.ke/";

    private ApiService apiService;

    public NetworkProvider() {
        Retrofit retrofit = provideRetrofit(API_URL);
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    private Retrofit provideRetrofit(String url){
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideLoggingCapableHttpClient())
                .build();
    }

    private OkHttpClient provideLoggingCapableHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }


}
