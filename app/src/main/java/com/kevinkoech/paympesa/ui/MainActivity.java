/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.kevinkoech.paympesa.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kevinkoech.paympesa.BuildConfig;
import com.kevinkoech.paympesa.PayMpesa;
import com.kevinkoech.paympesa.R;
import com.kevinkoech.paympesa.api.NetworkProvider;
import com.kevinkoech.paympesa.utils.Config;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements PriceTransfer, MainView {

    @BindView(R.id.cart_list)
    RecyclerView cart_list;
    @BindView(R.id.buttonCheckout)
    Button buttonCheckout;

    private ArrayList<Integer> prices = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;
    private ProgressDialog dialog;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);

        NetworkProvider networkProvider = ((PayMpesa) getApplication()).networkProvider;

        presenter = new MainPresenter(this, networkProvider.getApiService());

        // Add CONSUMER_KEY and CONSUMER_SECRET (with surrounding "") to gradle.properties
        presenter.getToken(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

        cart_list.setLayoutManager(layoutManager);

        ArrayList<String> cart_items = new ArrayList<>();
        cart_items.add("Tomatoes");
        cart_items.add("Apples");
        cart_items.add("Bananas");

        ArrayList<String> cart_prices = new ArrayList<>();
        cart_prices.add("1");
        cart_prices.add("200");
        cart_prices.add("120");

        cart_list.setAdapter(new CartListAdapter(MainActivity.this, cart_items, cart_prices, MainActivity.this));

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    Timber.i("FCM registration completed.");

                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Timber.i("New notification received.");
                    Timber.d(intent.getExtras().toString());

                    String title = intent.getStringExtra("title");
                    String message = intent.getStringExtra("message");

                    Notification notification = new NotificationCompat.Builder(MainActivity.this)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ic_money).build();

                    if (notificationManager != null) {
                        notificationManager.notify(1, notification);
                    }
                }
            }
        };

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prices.size() > 0) {
                    getPhoneNumber();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        broadcastReceiver,
                        new IntentFilter(Config.REGISTRATION_COMPLETE)
                );

        // register FCM new message receiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        broadcastReceiver,
                        new IntentFilter(Config.PUSH_NOTIFICATION)
                );
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    public void getPhoneNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the customer's phone number");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHint("07xxxxxxxx");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = input.getText().toString();
                performSTKPush(phoneNumber);
            }
        });

        builder.setNegativeButton("Clear Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prices.clear();
                buttonCheckout.setText("Checkout");
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void setPrices(ArrayList<Integer> prices) {
        this.prices = prices;
        buttonCheckout.setText("Checkout Kshs. " + String.valueOf(getTotal(prices)));
    }

    public int getTotal(ArrayList<Integer> prices) {
        int sum = 0;
        for (int i = 0; i < prices.size(); i++) {
            sum = sum + prices.get(i);
        }

        if (prices.size() == 0) {
            Toast.makeText(MainActivity.this, String.valueOf("Total: " + sum), Toast.LENGTH_SHORT).show();
            return 0;
        } else
            return sum;
    }

    public void performSTKPush(String phoneNumber) {
        dialog.setMessage("Processing..");
        dialog.setTitle("Please Wait");
        dialog.setIndeterminate(true);
        dialog.show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String FcmToken = preferences.getString(Config.TOKEN_KEY, "");

        presenter.performSTKPush(phoneNumber, String.valueOf(getTotal(prices)), FcmToken, new MainPresenter.StkPushListener() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
            }

            @Override
            public void onError(String reason) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
