package com.socialnetwork;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sill.R;

public class SNDialog extends Dialog
{
    static final int BLUE = 0xFF6D84B4;
    static final float[] DIMENSIONS_DIFF_LANDSCAPE = {20, 60};
    static final float[] DIMENSIONS_DIFF_PORTRAIT = {40, 60};
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private String mUrl;
    private ISocialNetwork.SNEventSource eventSource;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private WebViewClient webViewClient;

    public SNDialog(Context context, String url, WebViewClient webClient, ISocialNetwork.SNEventSource source)
    {
        super(context);
        this.mUrl = url;
        this.webViewClient = webClient;
        this.eventSource = source;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpTitle();
        setUpWebView();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale =
            getContext().getResources().getDisplayMetrics().density;
        int orientation =
            getContext().getResources().getConfiguration().orientation;
        float[] dimensions =
            (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    ? DIMENSIONS_DIFF_LANDSCAPE : DIMENSIONS_DIFF_PORTRAIT;
        addContentView(mContent, new LinearLayout.LayoutParams(
                display.getWidth() - ((int) (dimensions[0] * scale + 0.5f)),
                display.getHeight() - ((int) (dimensions[1] * scale + 0.5f))));
    }

    public ProgressDialog GetSpinner()
    { return mSpinner; }

    private void setUpTitle()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void setUpWebView()
    {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(webViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            eventSource.FireEvent(this, ISocialNetwork.OperationCode.ERROR, "User Cancel");
            dismiss();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

