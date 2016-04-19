package com.cqupt.xmpp.provider;

import com.cqupt.xmpp.packet.SubscribResp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created by tiandawu on 2016/4/17.
 */
public class SubscribRespProvider implements IQProvider {

    //<resp xmlns='resp:subd' var='subd'><item pub='client2@xmpp/s2' attrName='temprature' type='period'/></resp>

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        SubscribResp subscribResp = new SubscribResp();
        boolean done = false;

        while (!done) {
            int eventType = parser.next();

            if (eventType == XmlPullParser.START_TAG) {

                if (parser.getName().equals("item")) {
                    String pub = parser.getAttributeValue("", "pub");
                    String attrName = parser.getAttributeValue("", "attrName");
                    String type = parser.getAttributeValue("", "type");
                    subscribResp.setPubName(pub);
                    subscribResp.setPubAttrName(attrName);
                    subscribResp.setPubType(type);
                }

            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("resp")) {
                    done = true;
                }
            }

        }


        return subscribResp;
    }
}
