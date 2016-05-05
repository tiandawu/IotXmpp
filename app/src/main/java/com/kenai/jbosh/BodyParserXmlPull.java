/*
 * Copyright 2009 Mike Cumings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kenai.jbosh;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Implementation of the BodyParser interface which uses the XmlPullParser
 * API.  When available, this API provides an order of magnitude performance
 * improvement over the default SAX parser implementation.
 */
final class BodyParserXmlPull implements BodyParser {

    /**
     * Logger.
     */
    private static final Logger LOG =
            Logger.getLogger(BodyParserXmlPull.class.getName());

    /**
     * Thread local to contain a XmlPullParser instance for each thread that
     * attempts to use one.  This allows us to gain an order of magnitude of
     * performance as a result of not constructing parsers for each
     * invocation while retaining thread safety.
     */
    private static final ThreadLocal<SoftReference<XmlPullParser>> XPP_PARSER =
        new ThreadLocal<SoftReference<XmlPullParser>>() {
            @Override protected SoftReference<XmlPullParser> initialValue() {
                return new SoftReference<XmlPullParser>(null);
            }
        };

    ///////////////////////////////////////////////////////////////////////////
    // BodyParser interface methods:

    /**
     * {@inheritDoc}
     */
    public BodyParserResults parse(final String xml) throws BOSHException {
        BodyParserResults result = new BodyParserResults();
        Exception thrown;
        try {
            XmlPullParser xpp = getXmlPullParser();

            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Start tag: " + xpp.getName());
                    }
                } else {
                    eventType = xpp.next();
                    continue;
                }

                String prefix = xpp.getPrefix();
                if (prefix == null) {
                    prefix = XMLConstants.DEFAULT_NS_PREFIX;
                }
                String uri = xpp.getNamespace();
                String localName = xpp.getName();
                QName name = new QName(uri, localName, prefix);
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Start element: ");
                    LOG.finest("    prefix: " + prefix);
                    LOG.finest("    URI: " + uri);
                    LOG.finest("    local: " + localName);
                }

                BodyQName bodyName = AbstractBody.getBodyQName();
                if (!bodyName.equalsQName(name)) {
                    throw(new IllegalStateException(
                            "Root element was not '" + bodyName.getLocalPart()
                            + "' in the '" + bodyName.getNamespaceURI()
                            + "' namespace.  (Was '" + localName
                            + "' in '" + uri + "')"));
                }

                for (int idx=0; idx < xpp.getAttributeCount(); idx++) {
                    String attrURI = xpp.getAttributeNamespace(idx);
                    if (attrURI.length() == 0) {
                        attrURI = xpp.getNamespace(null);
                    }
                    String attrPrefix = xpp.getAttributePrefix(idx);
                    if (attrPrefix == null) {
                        attrPrefix = XMLConstants.DEFAULT_NS_PREFIX;
                    }
                    String attrLN = xpp.getAttributeName(idx);
                    String attrVal = xpp.getAttributeValue(idx);
                    BodyQName aqn = BodyQName.createWithPrefix(
                            attrURI, attrLN, attrPrefix);
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("        Attribute: {" + attrURI + "}"
                                + attrLN + " = '" + attrVal + "'");
                    }
                    result.addBodyAttributeValue(aqn, attrVal);
                }
                break;
            }
            return result;
        } catch (RuntimeException rtx) {
            thrown = rtx;
        } catch (XmlPullParserException xmlppx) {
            thrown = xmlppx;
        } catch (IOException iox) {
            thrown = iox;
        }
        throw(new BOSHException("Could not parse body:\n" + xml, thrown));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private methods:

    /**
     * Gets a XmlPullParser for use in parsing incoming messages.
     *
     * @return parser instance
     */
    private static XmlPullParser getXmlPullParser() {
        SoftReference<XmlPullParser> ref = XPP_PARSER.get();
        XmlPullParser result = ref.get();
        if (result == null) {
            Exception thrown;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setValidating(false);
                result = factory.newPullParser();
                ref = new SoftReference<XmlPullParser>(result);
                XPP_PARSER.set(ref);
                return result;
            } catch (Exception ex) {
                thrown = ex;
            }
            throw(new IllegalStateException(
                    "Could not create XmlPull parser", thrown));
        } else {
            return result;
        }
    }

}
