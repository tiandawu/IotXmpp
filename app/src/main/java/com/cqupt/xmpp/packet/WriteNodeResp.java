package com.cqupt.xmpp.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/17.
 */
public class WriteNodeResp extends IQ {


    //<iq id='123' type='result' from='client2@xmpp/s2' to='client@xmpp/B'><resp xmlns='write:data'/></iq>       (写入成功)

    @Override
    public String getChildElementXML() {
        return "<resp xmlns='write:data'/>";
    }
}
