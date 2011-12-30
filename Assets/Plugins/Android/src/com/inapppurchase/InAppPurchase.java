package com.inapppurchase;

import android.text.format.Time;
import com.inapppurchase.Security;
import com.inapppurchase.Consts.PurchaseState;
import com.inapppurchase.Consts.ResponseCode;
import com.inapppurchase.BillingService.RequestPurchase;
import com.inapppurchase.BillingService.RestoreTransactions;


import android.provider.Settings.Secure;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class InAppPurchase
{
    private static final String TAG = "InAppPurchase";
    private static final long restoreWait = 2000;

    private static Handler mHandler;
    private static PurchaseObserver mPurchaseObserver;
    private static BillingService mBillingService;

    private String item;
    private Timer timer;
    private long waitTime = restoreWait;

    private int port;
    private String host;

    private String data = "";
    private String sign = "";

    class PurchaseObserverDriver extends PurchaseObserver
    {
        private String[] responseString = new String[]
        {
            "Thank you for purchasing, the upgrade has completed successfully",
            "Stoped by user",
            "Sorry, service is unavailable. Please, try later.",
            "Sorry, billing is unavailable. Please, try later.",
            "Sorry, option is unavailable. Please, try later.",
            "RESULT_DEVELOPER_ERROR",
            "The upgrade hasn't completed successfully",
            "RESULT_NO",
            "Connection failed! Check internet connection and try again"
        };
        private static final String TAG = "PurchaseObserverDriver";

        @Override
        public void onBillingSupported(boolean supported)
        {
            if (Consts.DEBUG)
                Log.d(TAG, "supported: " + supported);

            if (!supported)
            {
                OnResponse(ResponseCode.RESULT_BILLING_UNAVAILABLE);
            }
            else if (waitTime == 0)
            {
                if (!mBillingService.requestPurchase(item, null))
                    OnResponse(ResponseCode.RESULT_DISCONNECT);
            }
            else if (!mBillingService.restoreTransactions())
            {
                OnResponse(ResponseCode.RESULT_DISCONNECT);
            }
            else
            {
                timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        if (!mBillingService.requestPurchase(item, null))
                            OnResponse(ResponseCode.RESULT_DISCONNECT);
                    }
                }, waitTime);
            }
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                                          long purchaseTime, String developerPayload, String data, String sign)
        {
            if (Consts.DEBUG)
                Log.d(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);

            InAppPurchase.this.data = data;
            InAppPurchase.this.sign = sign;

            StopTimer();
            if (purchaseState == PurchaseState.PURCHASED || purchaseState == PurchaseState.REFUNDED)
                OnResponse(Consts.ResponseCode.RESULT_OK);
            else
                OnResponse(Consts.ResponseCode.RESULT_ERROR);
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                                              ResponseCode responseCode)
        {
            StopTimer();
            if (Consts.DEBUG)
                Log.d(TAG, "onRequestPurchaseResponse " + request.mProductId + ": " + responseCode);
            if (responseCode != ResponseCode.RESULT_OK)
                OnResponse(responseCode);
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                                                  ResponseCode responseCode)
        {
            if (Consts.DEBUG)
                Log.d(TAG, "completed RestoreTransactions request with result " + responseCode.toString());
            if (responseCode != ResponseCode.RESULT_OK)
            {
                StopTimer();
                if (!mBillingService.requestPurchase(item, null))
                     OnResponse(ResponseCode.RESULT_DISCONNECT);
            }
        }

        private void OnResponse(ResponseCode code)
        {
            try
            {
                Socket socket = new Socket(host, port);
                OutputStream out =  socket.getOutputStream();
                out.write(((code == Consts.ResponseCode.RESULT_OK) ? "SUCCESS" : "ERROR").getBytes());
                out.write(' ');
                if (code == Consts.ResponseCode.RESULT_OK && !Consts.USE_VERIFICATION)
                {
                    out.write(data.getBytes());
                    out.write(' ');
                    out.write(sign.getBytes());
                }
                else
                {
                    out.write(responseString[ResponseCode.codeOf(code)].getBytes());
                }
                out.close();
                socket.close();
            }
            catch (UnknownHostException ex) { Log.e("LAUH", ex.getMessage()); }
            catch (IOException ex) { Log.e("LAIO", ex.getMessage()); }
        }
    }

    public InAppPurchase(String host, String port, Activity activity)
    {
        mPurchaseObserver = new PurchaseObserverDriver();
        this.port = Integer.parseInt(port);
        this.host = host;

        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                mHandler = new Handler();
            }
        });

        while (mHandler == null);
        mPurchaseObserver.init(activity, mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(activity);

        ResponseHandler.register(mPurchaseObserver);

        Security.SetUID(Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID));
    }

    public void SetKey(String key)
    { Security.SetKey(key); }

    public boolean Purchase(String i, String useRestore)
    {
        if (Consts.DEBUG)
            Log.d(TAG, "Purchase " + i);
        this.waitTime = (useRestore == "true") ? restoreWait : 0;
        this.item = i;
        return mBillingService.checkBillingSupported();
    }


    public void Close()
    {
        if (Consts.DEBUG)
            Log.d(TAG, "finalize");
        ResponseHandler.unregister(mPurchaseObserver);
        mBillingService.unbind();
    }

    private void StopTimer()
    {
        try
        {
            if (timer != null)
                timer.cancel();
        }
        catch (Exception e){}
        timer = null;
    }
}