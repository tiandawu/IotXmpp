package com.cqupt.xmpp.bean;

import java.io.Serializable;

/**
 * Created by tiandawu on 2016/4/10.
 */
public class ChatMessage implements Serializable {
    /**
     * 消息来自哪里
     */
    private String from;
    /**
     * 消息发去哪里
     */
    private String to;
    /**
     * 消息内容
     */
    private String body;
    /**
     * 发送或者接受消息的时间
     */
    private String time;
    /**
     * 消息属于哪个人
     */
    private String owner;

    private String flag = "false";


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
