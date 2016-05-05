package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/17.
 */
public class WriteDataToNode extends IQ {


    //<iq id='123' type='get' from='client@xmp/B'to='client2@/s2'><req var='write'><attr var='samplePeri'>11</attr></req></iq>

    private String writeVar, data;


    public WriteDataToNode() {

    }

    /**
     * @param var  写入分为：samplePeri  highLimit   lowLimit
     * @param data 写入的数据
     */
    public WriteDataToNode(String var, String data) {
        this.writeVar = var;
        this.data = data;
    }

    public String getWriteVar() {
        return writeVar;
    }

    public void setWriteVar(String writeVar) {
        this.writeVar = writeVar;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override

    public String getChildElementXML() {

        StringBuilder builder = new StringBuilder();
        builder.append("<req var='write'><attr var='");
        builder.append(writeVar);
        builder.append("'>");
        builder.append(data);
        builder.append("</attr></req>");

        return builder.toString();
    }
}
