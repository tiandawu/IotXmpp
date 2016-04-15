package com.cqupt.xmpp.packet;

import android.util.Log;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/10.
 */
public class SubscribNode extends IQ {

    private String pubName, pubType;

    public SubscribNode() {
    }

    public SubscribNode(String pubName, String pubType) {
        this.pubName = pubName;
        this.pubType = pubType;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<req var='sub'><item pub='");
        stringBuilder.append(pubName);
        stringBuilder.append("' attrName='temprature' type='");
        stringBuilder.append(pubType);
        stringBuilder.append("'/></req>");
        Log.e("tt", "sbToStr=" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getPubType() {
        return pubType;
    }

    public void setPubType(String pubType) {
        this.pubType = pubType;
    }

}
