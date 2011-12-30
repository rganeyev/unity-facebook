using System;
using UnityEngine;

public class IOSLogin : FacebookLogin
{
	public IOSLogin () : base()
	{
		GameObject go = new GameObject (IOSHelper.GAMEOBJECT);
		helper = go.AddComponent<IOSHelper> ();
		helper.LoginComplete += OnIOSLogined;
	}

	protected override void Login (string facebookAppID)
	{
		helper.Login (facebookAppID);
	}

	private void OnIOSLogined (bool success, string token)
	{
		LoginCallback (success, token);
	}

	private IOSHelper helper;
}


