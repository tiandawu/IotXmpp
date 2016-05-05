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
 * Interface used by parties interested in monitoring outbound requests made
 * by the client to the connection manager (CM).  No opportunity is provided
 * to manipulate the outbound request.
 * <p/>
 * The messages being sent are typically modified copies of the message
 * body provided to the {@code BOSHClient} instance, built from the
 * originally provided message body plus additional BOSH protocol
 * state and information.  Messages may also be sent automatically when the
 * protocol requires it, such as maintaining a minimum number of open
 * connections to the connection manager.
 * <p/>
 * Listeners are executed by the sending thread immediately prior to
 * message transmission and should not block for any significant amount
 * of time.
 */
public interface BOSHClientRequestListener {

    /**
     * Called when the listener is being notified that a client request is
     * about to be sent to the connection manager.
     *
     * @param event event instance containing the message being sent
     */
    void requestSent(BOSHMessageEvent event);

}
