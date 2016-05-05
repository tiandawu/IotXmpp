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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a single message to or from the BOSH connection
 * manager (CM).
 * <p/>
 * These messages consist of a single {@code body} element
 * (qualified within the BOSH namespace:
 * {@code http://jabber.org/protocol/httpbind}) and contain zero or more
 * child elements (of any namespace).  These child elements constitute the
 * message payload.
 * <p/>
 * In addition to the message payload, the attributes of the wrapper
 * {@code body} element may also need to be used as part of the communication
 * protocol being implemented on top of BOSH, or to define additional
 * namespaces used by the child "payload" elements.  These attributes are
 * exposed via accessors.
 */
public abstract class AbstractBody {

    ///////////////////////////////////////////////////////////////////////////
    // Constructor:

    /**
     * Restrict subclasses to the local package.
     */
    AbstractBody() {
        // Empty
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public methods:

    /**
     * Get a set of all defined attribute names.
     *
     * @return set of qualified attribute names
     */
    public final Set<BodyQName> getAttributeNames() {
        Map<BodyQName, String> attrs = getAttributes();
        return Collections.unmodifiableSet(attrs.keySet());
    }

    /**
     * Get the value of the specified attribute.
     *
     * @param attr name of the attribute to retriece
     * @return attribute value, or {@code null} if not defined
     */
    public final String getAttribute(final BodyQName attr) {
        Map<BodyQName, String> attrs = getAttributes();
        return attrs.get(attr);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Abstract methods:

    /**
     * Get a map of all defined attribute names with their corresponding values.
     *
     * @return map of qualified attributes
     */
    public abstract Map<BodyQName, String> getAttributes();

    /**
     * Get an XML String representation of this message. 
     *
     * @return XML string representing the body message
     */
    public abstract String toXML();

    ///////////////////////////////////////////////////////////////////////////
    // Package-private methods:

    /**
     * Returns the qualified name of the root/wrapper element.
     *
     * @return qualified name
     */
    static BodyQName getBodyQName() {
        return BodyQName.createBOSH("body");
    }

}
