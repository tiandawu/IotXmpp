package com.cqupt.xmpp.provider;

import com.cqupt.xmpp.packet.GetDataResp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created by tiandawu on 2016/4/14.
 */
public class GetDataRespProvider implements IQProvider {


    //<resp><attribute var='temprature'>17.43</attribute></resp>

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {

        GetDataResp getDataResp = new GetDataResp();
        boolean done = false;

        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {

                if (parser.getName().equals("attribute")) {
                    String var = parser.getAttributeValue("", "var");
                    String value = parser.nextText();


                    getDataResp.setVar(var);
                    getDataResp.setValue(value);
                }


            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("resp")) {
                    done = true;
                }
            }
        }

        return getDataResp;
    }

}
