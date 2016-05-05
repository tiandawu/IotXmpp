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

import javax.xml.XMLConstants;

/**
 * Class containing constants for attribute definitions used by the
 * XEP-0124 specification.  We shouldn't need to expose these outside
 * our package, since nobody else should be needing to worry about
 * them.
 */
final class Attributes {

    /**
     * Private constructor to prevent construction of library class.
     */
    private Attributes() {
        super();
    }

    static final BodyQName ACCEPT = BodyQName.createBOSH("accept");
    static final BodyQName AUTHID = BodyQName.createBOSH("authid");
    static final BodyQName ACK = BodyQName.createBOSH("ack");
    static final BodyQName CHARSETS = BodyQName.createBOSH("charsets");
    static final BodyQName CONDITION = BodyQName.createBOSH("condition");
    static final BodyQName CONTENT = BodyQName.createBOSH("content");
    static final BodyQName FROM = BodyQName.createBOSH("from");
    static final BodyQName HOLD = BodyQName.createBOSH("hold");
    static final BodyQName INACTIVITY = BodyQName.createBOSH("inactivity");
    static final BodyQName KEY = BodyQName.createBOSH("key");
    static final BodyQName MAXPAUSE = BodyQName.createBOSH("maxpause");
    static final BodyQName NEWKEY = BodyQName.createBOSH("newkey");
    static final BodyQName PAUSE = BodyQName.createBOSH("pause");
    static final BodyQName POLLING = BodyQName.createBOSH("polling");
    static final BodyQName REPORT = BodyQName.createBOSH("report");
    static final BodyQName REQUESTS = BodyQName.createBOSH("requests");
    static final BodyQName RID = BodyQName.createBOSH("rid");
    static final BodyQName ROUTE = BodyQName.createBOSH("route");
    static final BodyQName SECURE = BodyQName.createBOSH("secure");
    static final BodyQName SID = BodyQName.createBOSH("sid");
    static final BodyQName STREAM = BodyQName.createBOSH("stream");
    static final BodyQName TIME = BodyQName.createBOSH("time");
    static final BodyQName TO = BodyQName.createBOSH("to");
    static final BodyQName TYPE = BodyQName.createBOSH("type");
    static final BodyQName VER = BodyQName.createBOSH("ver");
    static final BodyQName WAIT = BodyQName.createBOSH("wait");
    static final BodyQName XML_LANG =
            BodyQName.createWithPrefix(XMLConstants.XML_NS_URI, "lang", "xml");
}
