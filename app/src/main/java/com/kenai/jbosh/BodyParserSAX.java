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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implementation of the BodyParser interface which uses the SAX API
 * that is part of the JDK.  Due to the fact that we can cache and reuse
 * SAXPArser instances, this has proven to be significantly faster than the
 * use of the javax.xml.stream API introduced in Java 6 while simultaneously
 * providing an implementation accessible to Java 5 users.
 */
final class BodyParserSAX implements BodyParser {

    /**
     * Logger.
     */
    private static final Logger LOG =
            Logger.getLogger(BodyParserSAX.class.getName());

    /**
     * SAX parser factory.
     */
    private static final SAXParserFactory SAX_FACTORY;
    static {
        SAX_FACTORY = SAXParserFactory.newInstance();
        SAX_FACTORY.setNamespaceAware(true);
        SAX_FACTORY.setValidating(false);
    }

    /**
     * Thread local to contain a SAX parser instance for each thread that
     * attempts to use one.  This allows us to gain an order of magnitude of
     * performance as a result of not constructing parsers for each
     * invocation while retaining thread safety.
     */
    private static final ThreadLocal<SoftReference<SAXParser>> PARSER =
        new ThreadLocal<SoftReference<SAXParser>>() {
            @Override protected SoftReference<SAXParser> initialValue() {
                return new SoftReference<SAXParser>(null);
            }
        };

    /**
     * SAX event handler class.
     */
    private static final class Handler extends DefaultHandler {
        private final BodyParserResults result;
        private final SAXParser parser;
        private String defaultNS = null;

        private Handler(SAXParser theParser, BodyParserResults results) {
            parser = theParser;
            result = results;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startElement(
                final String uri,
                final String localName,
                final String qName,
                final Attributes attributes) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Start element: " + qName);
                LOG.finest("    URI: " + uri);
                LOG.finest("    local: " + localName);
            }

            BodyQName bodyName = AbstractBody.getBodyQName();
            // Make sure the first element is correct
            if (!(bodyName.getNamespaceURI().equals(uri)
                    && bodyName.getLocalPart().equals(localName))) {
                throw(new IllegalStateException(
                        "Root element was not '" + bodyName.getLocalPart()
                        + "' in the '" + bodyName.getNamespaceURI()
                        + "' namespace.  (Was '" + localName + "' in '" + uri
                        + "')"));
            }

            // Read in the attributes, making sure to expand the namespaces
            // as needed.
            for (int idx=0; idx < attributes.getLength(); idx++) {
                String attrURI = attributes.getURI(idx);
                if (attrURI.length() == 0) {
                    attrURI = defaultNS;
                }
                String attrLN = attributes.getLocalName(idx);
                String attrVal = attributes.getValue(idx);
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("    Attribute: {" + attrURI + "}"
                            + attrLN + " = '" + attrVal + "'");
                }

                BodyQName aqn = BodyQName.create(attrURI, attrLN);
                result.addBodyAttributeValue(aqn, attrVal);
            }
            
            parser.reset();
        }

        /**
         * {@inheritDoc}
         *
         * This implementation uses this event hook to keep track of the
         * default namespace on the body element.
         */
        @Override
        public void startPrefixMapping(
                final String prefix,
                final String uri) {
            if (prefix.length() == 0) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Prefix mapping: <DEFAULT> => " + uri);
                }
                defaultNS = uri;
            } else {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.info("Prefix mapping: " + prefix + " => " + uri);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // BodyParser interface methods:

    /**
     * {@inheritDoc}
     */
    public BodyParserResults parse(String xml) throws BOSHException {
        BodyParserResults result = new BodyParserResults();
        Exception thrown;
        try {
            InputStream inStream = new ByteArrayInputStream(xml.getBytes());
            SAXParser parser = getSAXParser();
            parser.parse(inStream, new Handler(parser, result));
            return result;
        } catch (SAXException saxx) {
            thrown = saxx;
        } catch (IOException iox) {
            thrown = iox;
        }
        throw(new BOSHException("Could not parse body:\n" + xml, thrown));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private methods:

    /**
     * Gets a SAXParser for use in parsing incoming messages.
     *
     * @return parser instance
     */
    private static SAXParser getSAXParser() {
        SoftReference<SAXParser> ref = PARSER.get();
        SAXParser result = ref.get();
        if (result == null) {
            Exception thrown;
            try {
                result = SAX_FACTORY.newSAXParser();
                ref = new SoftReference<SAXParser>(result);
                PARSER.set(ref);
                return result;
            } catch (ParserConfigurationException ex) {
                thrown = ex;
            } catch (SAXException ex) {
                thrown = ex;
            }
            throw(new IllegalStateException(
                    "Could not create SAX parser", thrown));
        } else {
            result.reset();
            return result;
        }
    }
    
}
