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

import java.net.URI;
import javax.net.ssl.SSLContext;

/**
 * BOSH client configuration information.  Instances of this class contain
 * all information necessary to establish connectivity with a remote
 * connection manager.
 * <p/>
 * Instances of this class are immutable, thread-safe,
 * and can be re-used to configure multiple client session instances.
 */
public final class BOSHClientConfig {

    /**
     * Connection manager URI.
     */
    private final URI uri;

    /**
     * Target domain.
     */
    private final String to;

    /**
     * Client ID of this station.
     */
    private final String from;

    /**
     * Default XML language.
     */
    private final String lang;

    /**
     * Routing information for messages sent to CM.
     */
    private final String route;

    /**
     * Proxy host.
     */
    private final String proxyHost;

    /**
     * Proxy port.
     */
    private final int proxyPort;

    /**
     * SSL context.
     */
    private final SSLContext sslContext;

    /**
     * Flag indicating that compression should be attempted, if possible.
     */
    private final boolean compressionEnabled;

    ///////////////////////////////////////////////////////////////////////////
    // Classes:

    /**
     * Class instance builder, after the builder pattern.  This allows each
     * {@code BOSHClientConfig} instance to be immutable while providing
     * flexibility when building new {@code BOSHClientConfig} instances.
     * <p/>
     * Instances of this class are <b>not</b> thread-safe.  If template-style
     * use is desired, see the {@code create(BOSHClientConfig)} method.
     */
    public static final class Builder {
        // Required args
        private final URI bURI;
        private final String bDomain;

        // Optional args
        private String bFrom;
        private String bLang;
        private String bRoute;
        private String bProxyHost;
        private int bProxyPort;
        private SSLContext bSSLContext;
        private Boolean bCompression;

        /**
         * Creates a new builder instance, used to create instances of the
         * {@code BOSHClientConfig} class.
         *
         * @param cmURI URI to use to contact the connection manager
         * @param domain target domain to communicate with
         */
        private Builder(final URI cmURI, final String domain) {
            bURI = cmURI;
            bDomain = domain;
        }

        /**
         * Creates a new builder instance, used to create instances of the
         * {@code BOSHClientConfig} class.
         *
         * @param cmURI URI to use to contact the connection manager
         * @param domain target domain to communicate with
         * @return builder instance
         */
        public static Builder create(final URI cmURI, final String domain) {
            if (cmURI == null) {
                throw(new IllegalArgumentException(
                        "Connection manager URI must not be null"));
            }
            if (domain == null) {
                throw(new IllegalArgumentException(
                        "Target domain must not be null"));
            }
            String scheme = cmURI.getScheme();
            if (!("http".equals(scheme) || "https".equals(scheme))) {
                throw(new IllegalArgumentException(
                        "Only 'http' and 'https' URI are allowed"));
            }
            return new Builder(cmURI, domain);
        }

        /**
         * Creates a new builder instance using the existing configuration
         * provided as a starting point.
         *
         * @param cfg configuration to copy
         * @return builder instance
         */
        public static Builder create(final BOSHClientConfig cfg) {
            Builder result = new Builder(cfg.getURI(), cfg.getTo());
            result.bFrom = cfg.getFrom();
            result.bLang = cfg.getLang();
            result.bRoute = cfg.getRoute();
            result.bProxyHost = cfg.getProxyHost();
            result.bProxyPort = cfg.getProxyPort();
            result.bSSLContext = cfg.getSSLContext();
            result.bCompression = cfg.isCompressionEnabled();
            return result;
        }

        /**
         * Set the ID of the client station, to be forwarded to the connection
         * manager when new sessions are created.
         *
         * @param id client ID
         * @return builder instance
         */
        public Builder setFrom(final String id) {
            if (id == null) {
                throw(new IllegalArgumentException(
                        "Client ID must not be null"));
            }
            bFrom = id;
            return this;
        }
        
        /**
         * Set the default language of any human-readable content within the
         * XML.
         *
         * @param lang XML language ID
         * @return builder instance
         */
        public Builder setXMLLang(final String lang) {
            if (lang == null) {
                throw(new IllegalArgumentException(
                        "Default language ID must not be null"));
            }
            bLang = lang;
            return this;
        }

        /**
         * Sets the destination server/domain that the client should connect to.
         * Connection managers may be configured to enable sessions with more
         * that one server in different domains.  When requesting a session with
         * such a "proxy" connection manager, a client should use this method to
         * specify the server with which it wants to communicate.
         *
         * @param protocol connection protocol (e.g, "xmpp")
         * @param host host or domain to be served by the remote server.  Note
         *  that this is not necessarily the host name or domain name of the
         *  remote server.
         * @param port port number of the remote server
         * @return builder instance
         */
        public Builder setRoute(
                final String protocol,
                final String host,
                final int port) {
            if (protocol == null) {
                throw(new IllegalArgumentException("Protocol cannot be null"));
            }
            if (protocol.contains(":")) {
                throw(new IllegalArgumentException(
                        "Protocol cannot contain the ':' character"));
            }
            if (host == null) {
                throw(new IllegalArgumentException("Host cannot be null"));
            }
            if (host.contains(":")) {
                throw(new IllegalArgumentException(
                        "Host cannot contain the ':' character"));
            }
            if (port <= 0) {
                throw(new IllegalArgumentException("Port number must be > 0"));
            }
            bRoute = protocol + ":" + host + ":" + port;
            return this;
        }

        /**
         * Specify the hostname and port of an HTTP proxy to connect through.
         *
         * @param hostName proxy hostname
         * @param port proxy port number
         * @return builder instance
         */
        public Builder setProxy(final String hostName, final int port) {
            if (hostName == null || hostName.length() == 0) {
                throw(new IllegalArgumentException(
                        "Proxy host name cannot be null or empty"));
            }
            if (port <= 0) {
                throw(new IllegalArgumentException(
                        "Proxy port must be > 0"));
            }
            bProxyHost = hostName;
            bProxyPort = port;
            return this;
        }

        /**
         * Set the SSL context to use for this session.  This can be used
         * to configure certificate-based authentication, etc..
         *
         * @param ctx SSL context
         * @return builder instance
         */
        public Builder setSSLContext(final SSLContext ctx) {
            if (ctx == null) {
                throw(new IllegalArgumentException(
                        "SSL context cannot be null"));
            }
            bSSLContext = ctx;
            return this;
        }

        /**
         * Set whether or not compression of the underlying data stream
         * should be attempted.  By default, compression is disabled.
         *
         * @param enabled set to {@code true} if compression should be
         *  attempted when possible, {@code false} to disable compression
         * @return builder instance
         */
        public Builder setCompressionEnabled(final boolean enabled) {
            bCompression = Boolean.valueOf(enabled);
            return this;
        }

        /**
         * Build the immutable object instance with the current configuration.
         *
         * @return BOSHClientConfig instance
         */
        public BOSHClientConfig build() {
            // Default XML language
            String lang;
            if (bLang == null) {
                lang = "en";
            } else {
                lang = bLang;
            }

            // Default proxy port
            int port;
            if (bProxyHost == null) {
                port = 0;
            } else {
                port = bProxyPort;
            }

            // Default compression
            boolean compression;
            if (bCompression == null) {
                compression = false;
            } else {
                compression = bCompression.booleanValue();
            }

            return new BOSHClientConfig(
                    bURI,
                    bDomain,
                    bFrom,
                    lang,
                    bRoute,
                    bProxyHost,
                    port,
                    bSSLContext,
                    compression);
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Constructor:

    /**
     * Prevent direct construction.
     *
     * @param cURI URI of the connection manager to connect to
     * @param cDomain the target domain of the first stream
     * @param cFrom client ID
     * @param cLang default XML language
     * @param cRoute target route
     * @param cProxyHost proxy host
     * @param cProxyPort proxy port
     * @param cSSLContext SSL context
     * @param cCompression compression enabled flag
     */
    private BOSHClientConfig(
            final URI cURI,
            final String cDomain,
            final String cFrom,
            final String cLang,
            final String cRoute,
            final String cProxyHost,
            final int cProxyPort,
            final SSLContext cSSLContext,
            final boolean cCompression) {
        uri = cURI;
        to = cDomain;
        from = cFrom;
        lang = cLang;
        route = cRoute;
        proxyHost = cProxyHost;
        proxyPort = cProxyPort;
        sslContext = cSSLContext;
        compressionEnabled = cCompression;
    }

    /**
     * Get the URI to use to contact the connection manager.
     *
     * @return connection manager URI.
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Get the ID of the target domain.
     *
     * @return domain id
     */
    public String getTo() {
        return to;
    }

    /**
     * Get the ID of the local client.
     *
     * @return client id, or {@code null}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get the default language of any human-readable content within the
     * XML.  Defaults to "en".
     *
     * @return XML language ID
     */
    public String getLang() {
        return lang;
    }

    /**
     * Get the routing information for messages sent to the CM.
     *
     * @return route attribute string, or {@code null} if no routing
     *  info was provided.
     */
    public String getRoute() {
        return route;
    }

    /**
     * Get the HTTP proxy host to use.
     *
     * @return proxy host, or {@code null} if no proxy information was specified
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Get the HTTP proxy port to use.
     *
     * @return proxy port, or 0 if no proxy information was specified
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Get the SSL context to use for this session.
     *
     * @return SSL context instance to use, or {@code null} if no
     *  context instance was provided.
     */
    public SSLContext getSSLContext() {
        return sslContext;
    }

    /**
     * Determines whether or not compression of the underlying data stream
     * should be attempted/allowed.  Defaults to {@code false}.
     *
     * @return {@code true} if compression should be attempted, {@code false}
     *  if compression is disabled or was not specified
     */
    boolean isCompressionEnabled() {
        return compressionEnabled;
    }

}
