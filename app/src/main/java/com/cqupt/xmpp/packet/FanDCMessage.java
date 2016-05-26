package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class FanDCMessage extends Message {

    private String from, to, flag;

    public FanDCMessage() {

    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public void setTo(String to) {
        this.to = to;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * 打开风扇
     * <message from='XX@xmpp/XX' to='A1@xmpp/A'>1/0</message>
     *
     * 打开直流电机
     * <message from='XX@xmpp/XX' to='A2@xmpp/A'>1/0</message>
     *
     * @return
     */
    @Override
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<message from='");
        builder.append(getFrom());
        builder.append("' to='");
        builder.append(getTo());
        builder.append("'>");
        builder.append(getFlag());
        builder.append("</message>");
        return builder.toString();
    }
}
