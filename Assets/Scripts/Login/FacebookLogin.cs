using UnityEngine;

public abstract class FacebookLogin
{
	public event System.Action<bool, string> LoginComplete;

	public static FacebookLogin Create ()
	{
		switch (Application.platform) {
		case RuntimePlatform.Android:
			return new AndroidLogin ();
		case RuntimePlatform.IPhonePlayer:
			return new IOSLogin ();
		default:
			return null;
		}
	}

	protected FacebookLogin ()
	{
		LoginComplete += OnLogined;
		blocked = false;
	}

	public void StartLogin (string facebookAppID)
	{
		if (blocked)
			return;
		blocked = true;
		Login (facebookAppID);
	}

	protected abstract void Login (string facebookAppID);

	protected void LoginCallback (bool success, string msg)
	{
		if (LoginComplete != null)
			LoginComplete (success, msg);
	}

	private void OnLogined (bool success, string accessToken)
	{
		blocked = false;
	}

	private bool blocked;
}


