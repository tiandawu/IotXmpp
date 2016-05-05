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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Implementation of the {@code AbstractBody} class which allows for the
 * definition of messages from pre-existing message content.  Instances of
 * this class are based on the underlying data and therefore cannot be
 * modified.  In order to obtain the wrapper element namespace and
 * attribute information, the body content is partially parsed.
 * <p/>
 * This class does only minimal syntactic and semantic checking with respect
 * to what the generated XML will look like.  It is up to the developer to
 * protect against the definition of malformed XML messages when building
 * instances of this class.
 * <p/>
 * Instances of this class are immutable and thread-safe.
 */
final class StaticBody extends AbstractBody {

    /**
     * Selected parser to be used to process raw XML messages.
     */
    private static final BodyParser PARSER =
            new BodyParserXmlPull();

    /**
     * Size of the internal buffer when copying from a stream.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Map of all attributes to their values.
     */
    private final Map<BodyQName, String> attrs;

    /**
     * This body message in raw XML form.
     */
    private final String raw;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors:

    /**
     * Prevent direct construction.
     */
    private StaticBody(
            final Map<BodyQName, String> attrMap,
            final String rawXML) {
        attrs = attrMap;
        raw = rawXML;
    }

    /**
     * Creates an instance which is initialized by reading a body
     * message from the provided stream.
     *
     * @param inStream stream to read message XML from
     * @return body instance
     * @throws BOSHException on parse error
     */
    public static StaticBody fromStream(
            final InputStream inStream)
            throws BOSHException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            do {
                read = inStream.read(buffer);
                if (read > 0) {
                    byteOut.write(buffer, 0, read);
                }
            } while (read >= 0);
        } catch (IOException iox) {
            throw(new BOSHException(
                    "Could not read body data", iox));
        }
        return fromString(byteOut.toString());
    }

    /**
     * Creates an instance which is initialized by reading a body
     * message from the provided raw XML string.
     *
     * @param rawXML raw message XML in string form
     * @return body instance
     * @throws BOSHException on parse error
     */
    public static StaticBody fromString(
            final String rawXML)
            throws BOSHException {
        BodyParserResults results = PARSER.parse(rawXML);
        return new StaticBody(results.getAttributes(), rawXML);
    }


    /**
     * {@inheritDoc}
     */
    public Map<BodyQName, String> getAttributes() {
        return Collections.unmodifiableMap(attrs);
    }

    /**
     * {@inheritDoc}
     */
    public String toXML() {
        return raw;
    }

}
