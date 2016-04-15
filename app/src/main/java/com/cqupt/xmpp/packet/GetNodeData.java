package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/10.
 */
public class GetNodeData extends IQ {

    private String dataType;

    public GetNodeData() {

    }

    public GetNodeData(String dataType) {
        this.dataType = dataType;
    }


    @Override
    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<req var='read'><attr var='");
        stringBuilder.append(dataType);
        stringBuilder.append("'/></req>");
        return stringBuilder.toString();
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
