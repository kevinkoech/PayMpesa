package com.kevinkoech.paympesa.ui;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.Gson;
import com.kevinkoech.paympesa.api.ApiService;
import com.kevinkoech.paympesa.models.LNMPaymentReponse;
import com.kevinkoech.paympesa.models.OAuthAccess;
import com.kevinkoech.paympesa.models.STKPush;
import com.kevinkoech.paympesa.utils.Config;
import com.kevinkoech.paympesa.utils.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Mark Njung'e.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class MainPresenter {

    private MainView view;
    private ApiService apiService;
    private OAuthAccess oAuthAccess = null;

    MainPresenter(MainView view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    void getToken(String consumerKey, String consumerSecret) {
        String keys = consumerKey + ":" + consumerSecret;
        String authHeader = "Basic " + Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);

        apiService.getAccessToken(authHeader)
                .enqueue(new Callback<OAuthAccess>() {
                    @Override
                    public void onResponse(@NonNull Call<OAuthAccess> call, @NonNull Response<OAuthAccess> response) {
                        if (response.isSuccessful()) {
                            oAuthAccess = response.body();
                            Timber.d(oAuthAccess.toString());
                        } else {
                            try {
                                Timber.e(response.errorBody().string());
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OAuthAccess> call, @NonNull Throwable t) {
                        Timber.e(t);
                    }
                });
    }

    void performSTKPush(String phoneNumber, String total, final String FcmToken, final StkPushListener listener) {
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(Config.BUSINESS_SHORT_CODE,
                Utils.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, timestamp),
                timestamp,
                Config.TRANSACTION_TYPE,
                total,
                Utils.sanitizePhoneNumber(phoneNumber),
                Config.PARTY_B,
                Utils.sanitizePhoneNumber(phoneNumber),
                Config.CALLBACK_URL + FcmToken,
                "test", //The account reference
                "test"); //The transaction description

        Timber.d(new Gson().toJson(stkPush));

        String authHeader = "Bearer " + oAuthAccess.getAccessToken();

        apiService.sendPush(authHeader, stkPush)
                .enqueue(new Callback<LNMPaymentReponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LNMPaymentReponse> call, @NonNull Response<LNMPaymentReponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                Timber.i("Post submitted to API. ");
                                view.displayMessage(response.body().getCustomerMessage());
                                listener.onSuccess();
                            } else {
                                Timber.e(response.errorBody().toString());
                                view.displayMessage(response.errorBody().string());
                                listener.onError(response.errorBody().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LNMPaymentReponse> call, @NonNull Throwable t) {
                        Timber.e(t);
                        listener.onError(t.getMessage());
                    }
                });
    }

    interface StkPushListener {
        void onSuccess();

        void onError(String reason);
    }
}
