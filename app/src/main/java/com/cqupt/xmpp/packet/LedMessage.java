package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class LedMessage extends Message {

    private String from, to, ledState;

    /**
     * LED（4种灯，主动控制）:A3@xmpp/A
     * 开/关：（1 开； 0 关）
     * C->N:(10  1等序号  0开关状态)  第一位代表灯序号；第二位代表开关命令1--开、0--关；
     * <message from='XX@xmpp/XX' to='A3@xmpp/A'>10</message>
     * N->C:
     * <message from='A3@xmpp/A' to='XX@xmpp/XX'>10</message>
     */

    public LedMessage() {
    }

    @Override
    public String toXML() {

        StringBuilder builder = new StringBuilder();
        builder.append("<message from='");
        builder.append(getFrom());
        builder.append("' to='");
        builder.append(getTo());
        builder.append("'>");
        builder.append(getLedState());
        builder.append("</message>");
        return builder.toString();
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

    public String getLedState() {
        return ledState;
    }

    public void setLedState(String ledState) {
        this.ledState = ledState;
    }
}
