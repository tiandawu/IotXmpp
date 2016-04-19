package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/17.
 */
public class SubscribResp extends IQ {

    //<resp xmlns='resp:subd' var='subd'><item pub='client2@xmpp/s2' attrName='temprature' type='period'/></resp>


    private static String pubName, pubAttrName, pubType;


    public static String getPubName() {
        return pubName;
    }

    public static void setPubName(String pubName) {
        SubscribResp.pubName = pubName;
    }

    public static String getPubAttrName() {
        return pubAttrName;
    }

    public static void setPubAttrName(String pubAttrName) {
        SubscribResp.pubAttrName = pubAttrName;
    }

    public static String getPubType() {
        return pubType;
    }

    public static void setPubType(String pubType) {
        SubscribResp.pubType = pubType;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<resp xmlns='resp:subd' var='subd'><item pub='");
        builder.append(getPubName());
        builder.append("' attrName='");
        builder.append(getPubAttrName());
        builder.append("' type='");
        builder.append(getPubType());
        builder.append("'/></resp>");
        return builder.toString();
    }

}
