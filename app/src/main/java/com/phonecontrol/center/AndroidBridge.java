package com.phonecontrol.center;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

public class AndroidBridge {

    private final MainActivity activity;

    public AndroidBridge(MainActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public String getCallLog() {
        return activity.getCallLogJson();
    }

    @JavascriptInterface
    public void openNotificationSettings() {
        activity.openNotificationSettings();
    }

    @JavascriptInterface
    public void openAppSettings() {
        activity.openAppSettings();
    }

    @JavascriptInterface
    public void call(String number) {

        Intent intent =
                new Intent(Intent.ACTION_DIAL);

        intent.setData(
                Uri.parse(
                        "tel:" +
                        Uri.encode(number)
                )
        );

        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void sms(
            String number,
            String body
    ) {

        Intent intent =
                new Intent(Intent.ACTION_SENDTO);

        intent.setData(
                Uri.parse(
                        "smsto:" +
                        Uri.encode(number)
                )
        );

        intent.putExtra(
                "sms_body",
                body
        );

        activity.startActivity(intent);
    }
}
