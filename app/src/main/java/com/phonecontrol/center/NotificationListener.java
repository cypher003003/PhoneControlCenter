
package com.phonecontrol.center;

import android.app.Notification;
import android.app.NotificationChannel;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.json.JSONObject;

public class NotificationListener
        extends NotificationListenerService {

    @Override
    public void onNotificationPosted(
            StatusBarNotification sbn) {

        if (sbn == null) {
            return;
        }

        Notification notification =
                sbn.getNotification();

        if (notification == null) {
            return;
        }

        Bundle extras =
                notification.extras;

        String title = "";
        String text = "";

        if (extras != null) {

            CharSequence titleValue =
                    extras.getCharSequence(
                            Notification.EXTRA_TITLE
                    );

            CharSequence textValue =
                    extras.getCharSequence(
                            Notification.EXTRA_TEXT
                    );

            if (titleValue != null) {
                title =
                        titleValue.toString();
            }

            if (textValue != null) {
                text =
                        textValue.toString();
            }
        }

        try {

            JSONObject data =
                    new JSONObject();

            data.put(
                    "package",
                    sbn.getPackageName()
            );

            data.put(
                    "title",
                    title
            );

            data.put(
                    "text",
                    text
            );

            data.put(
                    "time",
                    System.currentTimeMillis()
            );

            if (MainActivity.instance != null) {

                MainActivity.instance.sendToJs(
                        "onNotificationEvent",
                        data
                );
            }

        } catch (Exception ignored) {
        }
    }
}
