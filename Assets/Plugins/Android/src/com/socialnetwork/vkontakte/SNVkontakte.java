package com.socialnetwork.vkontakte;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.socialnetwork.ISocialNetwork;
import com.socialnetwork.SNDialog;
import com.socialnetwork.SNUser;

import java.util.LinkedList;
import java.util.List;

public class SNVkontakte implements ISocialNetwork
{
    public enum Permission
    {
        notify,
        friends,
        photos,
        audio,
        video,
        docs,
        notes,
        pages,
        offers,
        questions,
        wall,
        messages,
        ads,
        offline
    }

    public enum ClientParams
    {
        vk_client_id,
        vk_scope,
        vk_redirect_uri,
        vk_display,
        vk_response_type
    }

    public enum Display
    {
        page,
        popup,
        touch,
        wap
    }

    public enum ResponseType
    {
        code,
        token
    }

    public enum RequestToken
    {
        client_id,
        scope,
        redirect_uri,
        display,
        response_type,
    }

    public enum ResponseToken
    {
        access_token,
        user_id
    }

    private SNDialog snDialog;
    private Activity activity;
    private SNEventSource loginSource;
    private String authURL = "https://api.vk.com/oauth/authorize?";
    private String[] accessURL = {"api.vk.com/oauth", "login.vk.com"};

    public SNVkontakte(Activity app)
    { activity = app; loginSource = new SNEventSource(); }

    public boolean Login(final SNUser snUser)
    {
        //Create URL request
        StringBuilder sb = new StringBuilder(authURL);

        if (snUser.Get(ClientParams.vk_client_id) == null)
            return false;
        sb.append(RequestToken.client_id.toString()).append('=').
                append(snUser.Get(ClientParams.vk_client_id).toString()).append('&');

        List<String> permissions = (List<String>)snUser.Get(ClientParams.vk_scope);
        if (permissions == null)
            return false;
        sb.append(RequestToken.scope.toString()).append('=');
        for (String permission : permissions)
            sb.append(permission).append(',');
        sb.deleteCharAt(sb.length() - 1).append('&');

        if (snUser.Get(ClientParams.vk_redirect_uri) == null)
            return false;
        sb.append(RequestToken.redirect_uri).append('=').
                append(snUser.Get(ClientParams.vk_redirect_uri).toString()).append('&');

        if (snUser.Get(ClientParams.vk_display) == null)
            return false;
        sb.append(RequestToken.display.toString()).append('=').
                append(snUser.Get(ClientParams.vk_display).toString());

        if (snUser.Get(ClientParams.vk_response_type) != null)
            sb.append('&').append(RequestToken.response_type).append('=').
                    append(snUser.Get(ClientParams.vk_response_type));

        CookieSyncManager.createInstance(activity);
        snDialog = new SNDialog(activity, sb.toString(), new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                String redirect = snUser.Get(ClientParams.vk_redirect_uri).toString();
                if (url.startsWith(redirect))
                {
                    if (url.charAt(redirect.length()) == '?')
                    {
                        String error = url.substring(url.charAt(redirect.length() + 1));
                        error.replace('&', '\n');
                        error.replace('+', ' ');
                        loginSource.FireEvent(SNVkontakte.this, ISocialNetwork.OperationCode.ERROR, error);
                    }
                    else if (url.charAt(redirect.length()) == '#')
                    {
                        String[] strings = url.substring(redirect.length() + 1).split("&");
                        LinkedList list = new LinkedList();
                        for (String string : strings)
                        {
                            if (string.contains(ResponseToken.access_token.toString()))
                            {
                                list.add(string.substring(ResponseToken.access_token.toString().length() + 1));
                            }
                            else if (string.contains(ResponseToken.user_id.toString()))
                            {
                                list.add(string.substring(ResponseToken.user_id.toString().length() + 1));
                            }
                        }
                        loginSource.FireEvent(SNVkontakte.this, ISocialNetwork.OperationCode.SUCCESS, list);
                    }
                    else
                    {
                        loginSource.FireEvent(SNVkontakte.this, ISocialNetwork.OperationCode.ERROR, "can_not_parse");
                    }
                    snDialog.dismiss();
                    return true;
                }
                else
                    for (String string : accessURL)
                        if (url.contains(string))
                            return false;
                snDialog.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String url)
            {
                loginSource.FireEvent(SNVkontakte.this, ISocialNetwork.OperationCode.ERROR, "received_error");
                snDialog.dismiss();
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                snDialog.GetSpinner().show();
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                snDialog.GetSpinner().dismiss();
            }
        }, loginSource);
        snDialog.show();
        return true;
    }

    public void SetLoginListener(ISNEventListener listener)
    { loginSource.AddEventListener(listener); }

    public void RemoveLoginListener(ISNEventListener listener)
    { loginSource.RemoveEventListener(listener); }
}
