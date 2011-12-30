using System;
using System.Collections.Generic;
using UnityEngine;
using System.Runtime.InteropServices;
using System.Runtime.CompilerServices;

public class IOSHelper : MonoBehaviour
{
	public const string GAMEOBJECT = "IOSLOGIN";

	public event Action<bool, string> LoginComplete;

	[DllImport("__Internal")]
	private static extern void _fbConnect (string appId);
	[DllImport("__Internal")]
	private static extern void _fbClose ();


	public void Login (string appId)
	{
		_fbConnect (appId);
	}

	public void Close ()
	{
		_fbClose ();
		Destroy (gameObject);
	}

	//Callbacks
	public void DidLogin (string accessToken)
	{
		if (LoginComplete != null) {
			LoginComplete (true, accessToken);
		}
	}

	public void DidNotLogin ()
	{
		if (LoginComplete != null) {
			LoginComplete (false, null);
		}
	}
	
	
}

