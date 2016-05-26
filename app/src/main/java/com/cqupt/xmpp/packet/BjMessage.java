package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class BjMessage extends Message {

    private String from, to, number, speed, dirction;

    @Override
    public String toXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<message from='");
        builder.append(getFrom());
        builder.append("' to='");
        builder.append(getTo());
        builder.append("'>");
        builder.append(getNumber() + ",");
        builder.append(getSpeed() + ",");
        builder.append(getDirction());
        builder.append("</message>");

        return builder.toString();
    }

    /**
     * 步进电机:A4@xmpp/A
     * 开：
     * C->N:(圈数，转速，方向)
     * <message from='XX@xmpp/XX' to='A4@xmpp/A'>1-100，1-5，1/0</message>
     * <p/>
     * N->C:
     * <message from='A4@xmpp/C' to='XX@xmpp/XX'>1-100，1-5，1/0</message>
     */


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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirction() {
        return dirction;
    }

    public void setDirction(String dirction) {
        this.dirction = dirction;
    }
}
