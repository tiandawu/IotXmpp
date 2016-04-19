package com.cqupt.xmpp.bean;

/**
 * Created by tiandawu on 2016/4/18.
 */
public class NodeSubStatus {
    private String nodeName, period, highLimit, lowLimit;

    public NodeSubStatus() {
        this.period = "false";
        this.highLimit = "false";
        this.lowLimit = "false";
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }
}
