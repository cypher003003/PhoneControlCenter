
package com.phonecontrol.center;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import org.json.JSONObject;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION
                .equals(intent.getAction())) {
            return;
        }

        SmsMessage[] messages =
                Telephony.Sms.Intents
                        .getMessagesFromIntent(intent);

        if (messages == null ||
                messages.length == 0) {
            return;
        }

        String sender = "";
        StringBuilder message =
                new StringBuilder();

        for (SmsMessage sms : messages) {

            if (sms == null) {
                continue;
            }

            if (sender.isEmpty()) {
                sender =
                        sms.getDisplayOriginatingAddress();
            }

            String body =
                    sms.getMessageBody();

            if (body != null) {
                message.append(body);
            }
        }

        try {

            JSONObject data =
                    new JSONObject();

            data.put(
                    "sender",
                    sender
            );

            data.put(
                    "body",
                    message.toString()
            );

            data.put(
                    "time",
                    System.currentTimeMillis()
            );

            if (MainActivity.instance != null) {

                MainActivity.instance.sendToJs(
                        "onSmsEvent",
                        data
                );
            }

        } catch (Exception ignored) {
        }
    }
}
