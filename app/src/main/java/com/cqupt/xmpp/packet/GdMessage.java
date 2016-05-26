package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class GdMessage extends Message {

    private String from, to, gdState;

    /**
     * 光电式接近传器:B2@xmpp/B
     * 无（默认）
     * N->C:
     * <message from='B2@xmpp/B' to='XX@xmpp/XX'>1/0</message>
     */

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<message from='");
        builder.append(getFrom());
        builder.append("' to='");
        builder.append(getTo());
        builder.append("'>");
        builder.append(getGdState());
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

    public String getGdState() {
        return gdState;
    }

    public void setGdState(String gdState) {
        this.gdState = gdState;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public void setTo(String to) {
        this.to = to;
    }
}
