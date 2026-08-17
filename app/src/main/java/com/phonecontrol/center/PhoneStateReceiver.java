
package com.phonecontrol.center;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED
                .equals(intent.getAction())) {
            return;
        }

        String state =
                intent.getStringExtra(
                        TelephonyManager.EXTRA_STATE
                );

        String number =
                intent.getStringExtra(
                        TelephonyManager.EXTRA_INCOMING_NUMBER
                );

        try {

            JSONObject data =
                    new JSONObject();

            data.put(
                    "state",
                    state == null ? "" : state
            );

            data.put(
                    "number",
                    number == null ? "" : number
            );

            data.put(
                    "time",
                    System.currentTimeMillis()
            );

            if (MainActivity.instance != null) {

                MainActivity.instance.sendToJs(
                        "onPhoneEvent",
                        data
                );
            }

        } catch (Exception ignored) {
        }
    }
}
