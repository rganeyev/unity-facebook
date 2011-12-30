package com.socialnetwork;

import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public interface ISocialNetwork
{
    enum OperationCode
    {
        ERROR,
        SUCCESS,
        NO
    }

    public class SNEvent extends EventObject
    {
        public OperationCode code;
        public Object value;
        public SNEvent(Object source, OperationCode code, Object value)
        { super(source); this.code = code; this.value = value; }
    }

    public interface ISNEventListener
    {
        public void HandleSNEvent(SNEvent snEvent);
    }

    public class SNEventSource
    {
        private List<ISNEventListener> listeners = new LinkedList<ISNEventListener>();

        public synchronized void AddEventListener(ISNEventListener listener)
        { listeners.add(listener); }

        public synchronized void RemoveEventListener(ISNEventListener listener)
        { listeners.remove(listener); }

        public synchronized void FireEvent(Object obj, OperationCode code, Object value)
        {
            SNEvent snEvent = new SNEvent(obj, code, value);
            Iterator<ISNEventListener> it = listeners.iterator();
            while (it.hasNext())
                it.next().HandleSNEvent(snEvent);
        }
    }

    public boolean Login(SNUser snUser);
    public void SetLoginListener(ISNEventListener snListener);
    public void RemoveLoginListener(ISNEventListener listener);
}
