package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/10.
 */
public class SubscribNode extends IQ {

    private String pubName, pubType, pubTypeNode;

    public SubscribNode() {
    }

    public SubscribNode(String pubName, String pubType, String pubTypeNode) {
        this.pubName = pubName;
        this.pubType = pubType;
        this.pubTypeNode = pubTypeNode;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<req var='sub'><item pub='");
        stringBuilder.append(pubName);
        stringBuilder.append("' attrName='");
        stringBuilder.append(pubTypeNode);
        stringBuilder.append("' type='");
        stringBuilder.append(pubType);
        stringBuilder.append("'/></req>");
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

    public String getPubTypeNode() {
        return pubTypeNode;
    }

    public void setPubTypeNode(String pubTypeNode) {
        this.pubTypeNode = pubTypeNode;
    }
}
