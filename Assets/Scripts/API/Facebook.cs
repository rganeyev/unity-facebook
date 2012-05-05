using UnityEngine;
using System;
using System.Text;
using System.Collections;
using System.Collections.Generic;
using System.Web;

public class Facebook : MonoBehaviour
{
	public string facebookAppID;

	public void Login ()
	{
		//if u're testing, u should already have access token
		if (Application.isEditor) {
			isLogined = true;
			return;
		}
		
		facebookLogin = FacebookLogin.Create ();
		facebookLogin.LoginComplete += OnLogined;
	}

	/// <summary>
	/// Fetches the given path in the Graph API.
	/// 
	/// Translates args to a valid query string. If post_args is given,
	/// sends a POST request to the given path with the given arguments.
	/// </summary>
	/// <param name="path">The path where the request is to be send</param>
	/// <param name="args">The Query arguments</param>
	/// <param name="postArgs">The POST arguments</param>
	/// <returns>A JObject of the facebook response</returns>
	private HTTP.Request SendRequest (string path, Dictionary<string, string> args, Dictionary<string, string> postArgs)
	{
		if (!isLogined) {
			throw new FacebookGraphAPIException ("not logined", "Login to perform action");
		}
		
		if (args == null) {
			args = new Dictionary<string, string> ();
		}
		if (postArgs != null) {
			postArgs["access_token"] = accessToken;
		} else {
			args["access_token"] = this.accessToken;
		}
		
		string uri = "https://graph.facebook.com/" + path + "?" + EncodeDictionary (args);
		var request = new HTTP.Request ("POST", uri);
		if (postArgs != null) {
			request.Text = EncodeDictionary (postArgs);
		}
		
		return request;
	}

	/// <summary>
	/// Encodes a dictionary keys to send them via HTTP request
	/// </summary>
	/// <param name="dict">Dictionary to be encoded</param>
	/// <returns>Encoded dictionary keys</returns>
	private string EncodeDictionary (Dictionary<string, string> dict)
	{
		string ret = "";
		if (dict != null) {
			foreach (var item in dict)
				ret += HttpUtility.UrlEncode (item.Key) + "=" + HttpUtility.UrlEncode (item.Value) + "&";
			ret = ret.TrimEnd ('&');
		}
		return ret;
	}

	private void OnLogined (bool success, string token)
	{
		isLogined = success;
		if (token != null) {
			string[] tokenArray = token.Split (' ');
			if (tokenArray != null) {
				userId = tokenArray[0];
				accessToken = tokenArray[1];
			}
		}
		Debug.Log(userId + " " + accessToken);
	}

	private bool isLogined = false;
	private FacebookLogin facebookLogin;
	private string accessToken;
	private string userId;
}
