package com.socialnetwork;

import java.util.Dictionary;
import java.util.Hashtable;

public class SNUser
{
    Dictionary<Object, Object> userInfo = new Hashtable<Object, Object>();

    public void Add(Object key, Object value)
    { userInfo.put(key, value); }

    public Object Get(Object key)
    { return userInfo.get(key); }
}
