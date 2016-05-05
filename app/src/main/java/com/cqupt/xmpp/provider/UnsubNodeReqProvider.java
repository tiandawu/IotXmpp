package com.cqupt.xmpp.provider;

import com.cqupt.xmpp.packet.UnsubNodeReq;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created by tiandawu on 2016/4/19.
 */
public class UnsubNodeReqProvider implements IQProvider {


    //<req xmlns='req:usubd' var='usubd'><item pub='client2@xmpp/s2' attrName='temprature' type='period'/></req>

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        UnsubNodeReq unsubNodeReq = new UnsubNodeReq();
        boolean done = false;

        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("item")) {
                    String pubName = parser.getAttributeValue("", "pub");
                    String attrName = parser.getAttributeValue("", "attrName");
                    String pubType = parser.getAttributeValue("", "type");
                    unsubNodeReq.setPubName(pubName);
                    unsubNodeReq.setPubType(pubType);
                    unsubNodeReq.setAttrName(attrName);
                }

            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("req")) {
                    done = true;
                }

            }
        }
        return unsubNodeReq;
    }
}
