using UnityEngine;
using System.Collections;
using System.Collections.Generic;

// JobManager is just a proxy object so we have a launcher for the coroutines
public class JobManager : MonoBehaviour
{	
	// only one JobManager can exist. We use a singleton pattern to enforce this.
	static JobManager _instance = null;
	
	void Awake ()
	{
		_instance = this;
	}

	public static JobManager instance {
		get {
			return _instance;
		}
	}

	void OnApplicationQuit ()
	{
		// release reference on exit
		_instance = null;
	}

}


