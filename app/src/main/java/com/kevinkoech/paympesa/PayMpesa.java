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

package com.kevinkoech.paympesa;

import android.app.Application;

import com.kevinkoech.paympesa.api.NetworkProvider;

import timber.log.Timber;

/**
 * Created  on 8/2/2017.
 */

public class PayMpesa extends Application{
    public NetworkProvider networkProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        networkProvider = new NetworkProvider();

        Timber.plant(new Timber.DebugTree(){
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return "Timber/" + element.getFileName()+ "." + element.getMethodName() + "(Ln" + element.getLineNumber() + ")";
            }
        });
    }
}
