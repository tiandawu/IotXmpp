package com.cqupt.xmpp.listener;

import android.util.Log;

import com.cqupt.xmpp.service.IotXmppService;

import org.jivesoftware.smack.ConnectionListener;

/**
 * Created by tiandawu on 2016/4/6.
 */
public class CheckConnectionListener implements ConnectionListener {
    private IotXmppService context;

    public CheckConnectionListener(IotXmppService context){
        this.context=context;
    }
    @Override
    public void connectionClosed() {
        Log.e("tt", "connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e("tt", "connectionClosedOnError = " + e.toString());
    }

    @Override
    public void reconnectingIn(int i) {
        Log.e("tt", "reconnectingIn = "+i);
    }

    @Override
    public void reconnectionSuccessful() {

        Log.e("tt", "reconnectionSuccessful");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.e("tt", "reconnectionFailed");
    }
}
