package com.letssolvetogether.omr.settings.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class OpenSourceLicensePreference extends DialogPreference {

    private final static String OPEN_SOURCE_LICENSE_HTML_URL = "file:////android_asset/opensource_license.html";

    public OpenSourceLicensePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public OpenSourceLicensePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OpenSourceLicensePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OpenSourceLicensePreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView() {
        WebView webView = new WebView(getContext());
        webView.loadUrl(OPEN_SOURCE_LICENSE_HTML_URL);
        return webView;
    }
}