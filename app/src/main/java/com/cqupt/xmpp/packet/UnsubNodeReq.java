package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/19.
 */
public class UnsubNodeReq extends IQ {

    //<req xmlns='req:usubd' var='usubd'><item pub='client2@xmpp/s2' attrName='temprature' type='period'/></req>

    private  String pubName, attrName, pubType;

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getPubType() {
        return pubType;
    }

    public void setPubType(String pubType) {
        this.pubType = pubType;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<req xmlns='req:usubd' var='usubd'><item pub='");
        builder.append(getPubName());
        builder.append("' attrName='");
        builder.append(getAttrName());
        builder.append("' type='");
        builder.append(getPubType());
        builder.append("'/></req>");
        return builder.toString();
    }
}
