using System;

public class FacebookGraphAPIException : Exception
{
	public string Type { get; set; }

	public FacebookGraphAPIException (string type, string message) : base(message)
	{
		Type = type;
	}
}


