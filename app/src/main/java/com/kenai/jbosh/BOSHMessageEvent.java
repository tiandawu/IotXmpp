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

import java.util.EventObject;

/**
 * Event representing a message sent to or from a BOSH connection manager.
 * <p/>
 * This class is immutable and thread-safe.
 */
public final class BOSHMessageEvent extends EventObject {

    /**
     * Serialized version.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Message which was sent or received.
     */
    private final AbstractBody body;

    /**
     * Creates a new message event instance.
     *
     * @param source event source
     * @param cBody message body
     */
    private BOSHMessageEvent(
            final Object source,
            final AbstractBody cBody) {
        super(source);
        if (cBody == null) {
            throw(new IllegalArgumentException(
                    "message body may not be null"));
        }
        body = cBody;
    }

    /**
     * Creates a new message event for clients sending events to the
     * connection manager.
     *
     * @param source sender of the message
     * @param body message body
     * @return event instance
     */
    static BOSHMessageEvent createRequestSentEvent(
            final BOSHClient source,
            final AbstractBody body) {
        return new BOSHMessageEvent(source, body);
    }

    /**
     * Creates a new message event for clients receiving new messages
     * from the connection manager.
     *
     * @param source receiver of the message
     * @param body message body
     * @return event instance
     */
    static BOSHMessageEvent createResponseReceivedEvent(
            final BOSHClient source,
            final AbstractBody body) {
        return new BOSHMessageEvent(source, body);
    }

    /**
     * Gets the message body which was sent or received.
     *
     * @return message body
     */
    public AbstractBody getBody() {
        return body;
    }
    
}
