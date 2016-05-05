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

/**
 * Qualified name of an attribute of the wrapper element.  This class is
 * analagous to the {@code javax.xml.namespace.QName} class.
 * Each qualified name consists of a namespace URI and a local name.
 * <p/>
 * Instances of this class are immutable and thread-safe.
 */
public final class BodyQName {

    /**
     * BOSH namespace URI.
     */
    static final String BOSH_NS_URI =
            "http://jabber.org/protocol/httpbind";

    /**
     * Namespace URI.
     */
    private final QName qname;

    /**
     * Private constructor to prevent direct construction.
     *
     * @param wrapped QName instance to wrap
     */
    private BodyQName(
            final QName wrapped) {
        qname = wrapped;
    }

    /**
     * Creates a new qualified name using a namespace URI and local name.
     *
     * @param uri namespace URI
     * @param local local name
     * @return BodyQName instance
     */
    public static BodyQName create(
            final String uri,
            final String local) {
        return createWithPrefix(uri, local, null);
    }

    /**
     * Creates a new qualified name using a namespace URI and local name
     * along with an optional prefix.
     *
     * @param uri namespace URI
     * @param local local name
     * @param prefix optional prefix or @{code null} for no prefix
     * @return BodyQName instance
     */
    public static BodyQName createWithPrefix(
            final String uri,
            final String local,
            final String prefix) {
        if (uri == null || uri.length() == 0) {
            throw(new IllegalArgumentException(
                    "URI is required and may not be null/empty"));
        }
        if (local == null || local.length() == 0) {
            throw(new IllegalArgumentException(
                    "Local arg is required and may not be null/empty"));
        }
        if (prefix == null || prefix.length() == 0) {
            return new BodyQName(new QName(uri, local));
        } else {
            return new BodyQName(new QName(uri, local, prefix));
        }
    }

    /**
     * Get the namespace URI of this qualified name.
     *
     * @return namespace uri
     */
    public String getNamespaceURI() {
        return qname.getNamespaceURI();
    }

    /**
     * Get the local part of this qualified name.
     *
     * @return local name
     */
    public String  getLocalPart() {
        return qname.getLocalPart();
    }

    /**
     * Get the optional prefix used with this qualified name, or {@code null}
     * if no prefix has been assiciated.
     *
     * @return prefix, or {@code null} if no prefix was supplied
     */
    public String getPrefix() {
        return qname.getPrefix();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof BodyQName) {
            BodyQName other = (BodyQName) obj;
            return qname.equals(other.qname);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return qname.hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Package-private methods:

    /**
     * Creates a new qualified name using the BOSH namespace URI and local name.
     *
     * @param local local name
     * @return BodyQName instance
     */
    static BodyQName createBOSH(
            final String local) {
        return createWithPrefix(BOSH_NS_URI, local, null);
    }

    /**
     * Convenience method to compare this qualified name with a
     * {@code javax.xml.namespace.QName}.
     *
     * @param otherName QName to compare to
     * @return @{code true} if the qualified name is the same, {@code false}
     *  otherwise
     */
    boolean equalsQName(final QName otherName) {
        return qname.equals(otherName);
    }

}
