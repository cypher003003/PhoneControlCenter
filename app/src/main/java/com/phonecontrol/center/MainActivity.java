package com.phonecontrol.center;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.webkit.WebViewAssetLoader;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    public static MainActivity instance;

    private WebView webView;

    private static final int REQ_PERMS = 1001;

    private final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        WebViewAssetLoader assetLoader =
                new WebViewAssetLoader.Builder()
                        .addPathHandler(
                                "/assets/",
                                new WebViewAssetLoader.AssetsPathHandler(this)
                        )
                        .build();

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public android.webkit.WebResourceResponse
            shouldInterceptRequest(
                    WebView view,
                    WebResourceRequest request
            ) {
                return assetLoader.shouldInterceptRequest(
                        request.getUrl()
                );
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(
                    final PermissionRequest request
            ) {
                runOnUiThread(() -> {

                    request.grant(
                            request.getResources()
                    );

                });
            }
        });

        webView.addJavascriptInterface(
                new AndroidBridge(this),
                "Android"
        );

        webView.loadUrl(
                "https://appassets.androidplatform.net/assets/index.html"
        );

        requestMissingPermissions();
    }

    private void requestMissingPermissions() {

        java.util.ArrayList<String> missing =
                new java.util.ArrayList<>();

        for (String permission : permissions) {

            if (
                    ContextCompat.checkSelfPermission(
                            this,
                            permission
                    ) != PackageManager.PERMISSION_GRANTED
            ) {

                missing.add(permission);
            }
        }

        if (!missing.isEmpty()) {

            ActivityCompat.requestPermissions(
                    this,
                    missing.toArray(new String[0]),
                    REQ_PERMS
            );
        }
    }

    public void sendToJs(
            String function,
            JSONObject data
    ) {

        if (webView == null) return;

        String safe =
                data.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'");

        runOnUiThread(() ->
                webView.evaluateJavascript(
                        "window." +
                                function +
                                "('" +
                                safe +
                                "')",
                        null
                )
        );
    }

    public String getCallLogJson() {

        JSONArray arr =
                new JSONArray();

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_CALL_LOG
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            return arr.toString();
        }

        android.database.Cursor cursor =
                getContentResolver().query(
                        CallLog.Calls.CONTENT_URI,

                        new String[]{
                                CallLog.Calls.NUMBER,
                                CallLog.Calls.CACHED_NAME,
                                CallLog.Calls.TYPE,
                                CallLog.Calls.DATE,
                                CallLog.Calls.DURATION
                        },

                        null,
                        null,

                        CallLog.Calls.DATE +
                                " DESC"
                );

        if (cursor != null) {

            int count = 0;

            while (
                    cursor.moveToNext()
                            && count++ < 50
            ) {

                try {

                    JSONObject object =
                            new JSONObject();

                    object.put(
                            "number",
                            cursor.getString(0)
                    );

                    object.put(
                            "name",
                            cursor.getString(1)
                    );

                    object.put(
                            "type",
                            cursor.getInt(2)
                    );

                    object.put(
                            "date",
                            cursor.getLong(3)
                    );

                    object.put(
                            "duration",
                            cursor.getLong(4)
                    );

                    arr.put(object);

                } catch (Exception ignored) {
                }
            }

            cursor.close();
        }

        return arr.toString();
    }

    public void openNotificationSettings() {

        startActivity(
                new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                )
        );
    }

    public void openAppSettings() {

        Intent intent =
                new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                );

        intent.setData(
                Uri.parse(
                        "package:" +
                                getPackageName()
                )
        );

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {

        if (instance == this) {
            instance = null;
        }

        super.onDestroy();
    }
}
