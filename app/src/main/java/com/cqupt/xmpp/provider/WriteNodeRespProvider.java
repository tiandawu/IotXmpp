package com.cqupt.xmpp.provider;

import com.cqupt.xmpp.packet.WriteNodeResp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created by tiandawu on 2016/4/17.
 */
public class WriteNodeRespProvider implements IQProvider {
    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        return new WriteNodeResp();
    }
}
