using System;
using UnityEngine;

public class AndroidLogin : FacebookLogin
{
	private readonly int port = 8000;
	private readonly string host = "localhost";
	private SocketServer socketServer;

	~AndroidLogin ()
	{
		JavaVM.DetachCurrentThread ();
	}

	protected override void Login (string facebookAppID)
	{
		JavaVM.AttachCurrentThread ();
		//Find current Activity
		IntPtr clsActivity = JNI.FindClass ("com/unity3d/player/UnityPlayer");
		IntPtr objActivity = JNI.GetStaticObjectField (clsActivity, JNI.GetStaticFieldID (clsActivity, "currentActivity", "Landroid/app/Activity;"));
		//Create LoginAdapter obj
		IntPtr clsLAdapter = JNI.FindClass ("com/socialnetwork/LoginAdapter");
		IntPtr objlAdapter = JNI.NewObject (clsLAdapter, JNI.GetMethodID (clsLAdapter, "<init>", "(Landroid/app/Activity;)V"), objActivity);
		IntPtr lAdapter = JNI.NewGlobalRef (objlAdapter);
		
		if (JNI.CallBooleanMethod (lAdapter, JNI.GetMethodID (clsLAdapter, "Login", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z"), JNI.NewStringUTF (facebookAppID), JNI.NewStringUTF (host), JNI.NewStringUTF (port.ToString ()), JNI.NewStringUTF ("fb")) == 0) {
			return;
		}
		socketServer = new SocketServer (port);
		socketServer.ReadString += OnLoginEnded;
	}

	private void OnLoginEnded (string obj)
	{
		Debug.LogWarning (obj);
		int splitPosition = obj.IndexOf (' ');
		LoginCallback (obj.Substring (0, splitPosition) == "SUCCESS", obj.Substring (splitPosition + 1));
		socketServer.Stop ();
		socketServer = null;
	}
}
