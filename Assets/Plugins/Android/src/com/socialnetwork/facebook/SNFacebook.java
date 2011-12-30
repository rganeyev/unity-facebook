package com.socialnetwork.facebook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.socialnetwork.ISocialNetwork;
import com.socialnetwork.SNUser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

public class SNFacebook implements ISocialNetwork
{
    public enum ReadPermission
    {
        about_me,
        activities,
        birthday,
        checkins,
        education_history,
        events,
        groups,
        hometown,
        interests,
        likes,
        location,
        notes,
        online_presence,
        photo_video_tags,
        photos,
        relationships,
        relationship_details,
        religion_politics,
        status,
        videos,
        website,
        work_history;

        String GetFriendPermission(ReadPermission rp)
        { return "freand_" + rp.toString(); }

        String GetUserPermission(ReadPermission rp)
        { return "user_" + rp.toString(); }
    }

    public enum UserReadPermission
    {
        email,
        read_friendlists,
        read_insights,
        read_mailbox,
        read_requests,
        read_stream,
        xmpp_login,
        ads_management
    }

    public enum WritePermission
    {
        create_event,
        manage_friendlists,
        manage_notifications,
        offline_access,
        publish_checkins,
        publish_stream,
        rsvp_event,
        sms,
        manage_pages
    }

    public enum ClientParams
    {
        fb_client_id,
        fb_permission
    }

    private Activity activity;
    private Facebook facebook;
    private SNEventSource loginSource;

    public SNFacebook(Activity activity)
    { this.activity = activity; loginSource = new SNEventSource(); }

    public boolean Login(SNUser snUser)
    {
        facebook = new Facebook(snUser.Get(ClientParams.fb_client_id).toString());
        facebook.authorize(activity, (String[]) snUser.Get(ClientParams.fb_permission), new Facebook.DialogListener()
        {
            public void onComplete(Bundle values)
            {
                LinkedList list = new LinkedList();
                list.add(facebook.getAccessToken());
                try
                {
                    JSONObject json = Util.parseJson(facebook.request("me"));
                    list.add(json.getString("id"));
                }
                catch (IOException e) { Log.e("SNF", e.toString()); }
                catch (JSONException e) { Log.e("SNF", e.toString()); }
                catch (FacebookError e) { Log.e("SNF", e.toString()); }

                loginSource.FireEvent(SNFacebook.this, OperationCode.SUCCESS, list);
            }

            public void onFacebookError(FacebookError e)
            { loginSource.FireEvent(SNFacebook.this, OperationCode.ERROR, e.getMessage()); }

            public void onError(DialogError e)
            { loginSource.FireEvent(SNFacebook.this, OperationCode.ERROR, e.getMessage()); }

            public void onCancel()
            { loginSource.FireEvent(SNFacebook.this, OperationCode.ERROR, "User Cancel"); }
        });
        return true;
    }

    public void SetLoginListener(ISNEventListener snListener)
    { loginSource.AddEventListener(snListener); }

    public void RemoveLoginListener(ISNEventListener listener)
    { loginSource.RemoveEventListener(listener); }
}
