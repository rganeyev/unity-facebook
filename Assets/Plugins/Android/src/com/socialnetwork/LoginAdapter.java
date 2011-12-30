package com.socialnetwork;

import android.app.Activity;
import android.util.Log;
import com.socialnetwork.facebook.SNFacebook;
import com.socialnetwork.vkontakte.SNVkontakte;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LoginAdapter
{
    public class LoginListener implements ISocialNetwork.ISNEventListener
    {
        public int port;
        public String host;

        public void HandleSNEvent(ISocialNetwork.SNEvent snEvent)
        {
            try
            {
                Socket socket = new Socket(host, port);
                OutputStream out =  socket.getOutputStream();
                out.write(snEvent.code.toString().getBytes());
                if (snEvent.code == ISocialNetwork.OperationCode.SUCCESS)
                {
                    for (Object value : (Collection) snEvent.value)
                    {
                        out.write(' ');
                        out.write(value.toString().getBytes());
                    }
                }
                else
                {
                    out.write(snEvent.value.toString().getBytes());
                }
                out.close();
                socket.close();
            }
            catch (UnknownHostException ex) { Log.e("LAUH", ex.getMessage()); }
            catch (IOException ex) { Log.e("LAIO", ex.getMessage()); }
        }
    }

    private ISocialNetwork.OperationCode result;
    private LoginListener listener = new LoginListener();
    private SNUser snUser = new SNUser();
    private ISocialNetwork socialNetwork;
    private Activity activity;

    public LoginAdapter(Activity activity)
    { this.activity = activity; }

    public boolean Login(String appID, String host, String port, String sn)
    {
        result = ISocialNetwork.OperationCode.NO;
        if (sn.equals("vk"))
            FeelVKUserParam(appID);
        else if (sn.equals("fb"))
            FeelFacebookParams(appID);
        else
            return false;

        listener.port = new Integer(port);
        listener.host = host;
        socialNetwork.SetLoginListener(listener);

        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (socialNetwork.Login(snUser))
                    result = ISocialNetwork.OperationCode.SUCCESS;
                else
                    result = ISocialNetwork.OperationCode.ERROR;
            }
        });
        while (result == ISocialNetwork.OperationCode.NO);

        if (result == ISocialNetwork.OperationCode.SUCCESS)
            return true;

        socialNetwork.RemoveLoginListener(listener);
        return false;
    }

    private void FeelVKUserParam(String appID)
    {
        socialNetwork = new SNVkontakte(activity);
        snUser.Add(SNVkontakte.ClientParams.vk_client_id, appID);
        snUser.Add(SNVkontakte.ClientParams.vk_display, SNVkontakte.Display.touch.toString());
        snUser.Add(SNVkontakte.ClientParams.vk_redirect_uri, "http://api.vk.com/blank.html");
        List<String> permissions = new LinkedList<String>();
        permissions.add(SNVkontakte.Permission.friends.toString());
        permissions.add(SNVkontakte.Permission.offline.toString());
        snUser.Add(SNVkontakte.ClientParams.vk_scope, permissions);
        snUser.Add(SNVkontakte.ClientParams.vk_response_type, SNVkontakte.ResponseType.token);
    }

    private void FeelFacebookParams(String appID)
    {
        socialNetwork = new SNFacebook(activity);
        snUser.Add(SNFacebook.ClientParams.fb_client_id, appID);
        List<String> permission = new LinkedList<String>();
        permission.add(SNFacebook.UserReadPermission.read_friendlists.toString());
        permission.add(SNFacebook.WritePermission.offline_access.toString());
        snUser.Add(SNFacebook.ClientParams.fb_permission, permission.toArray(new String[0]));
    }
}