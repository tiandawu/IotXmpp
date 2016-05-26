package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class DmMessage extends Message {

    private String from, to, doorState;

    /**
     * 门磁（被动控制）:B1@xmpp/B
     * 开（默认）
     * N->C:
     * <message from='B1@xmpp/B' to='XX@xmpp/XX'>1/0</message>
     */
    @Override
    public String toXML() {

        StringBuilder builder = new StringBuilder();
        builder.append("<message from='");
        builder.append(getFrom());
        builder.append("' to='");
        builder.append(getTo());
        builder.append("'>");
        builder.append(getDoorState());
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

    public String getDoorState() {
        return doorState;
    }

    public void setDoorState(String doorState) {
        this.doorState = doorState;
    }
}
