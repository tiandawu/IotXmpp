package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/19.
 */
public class UnsubNode extends IQ {


    //<iq id='1234' type='set' from='client@xmpp/B'><req var='usub'><item pub='client2@xmpp/s2' attrName='temprature' type='period'/></req></iq>

    private String pubName, attrName, subType;

    public UnsubNode() {
        setType(Type.SET);
    }

    public UnsubNode(String pubName, String attrName, String subType) {
        this.pubName = pubName;
        this.attrName = attrName;
        this.subType = subType;
    }

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

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<req var='usub'><item pub='");
        builder.append(getPubName());
        builder.append("' attrName='");
        builder.append(getAttrName());
        builder.append("' type='");
        builder.append(getSubType());
        builder.append("'/></req>");
        return builder.toString();
    }
}
