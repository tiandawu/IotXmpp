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
 * Interface used by parties interested in monitoring inbound responses
 * to the client from the connection manager (CM).  No opportunity is provided
 * to manipulate the response.
 * <p/>
 * Listeners are executed by the message processing thread and should not
 * block for any significant amount of time.
 */
public interface BOSHClientResponseListener {

    /**
     * Called when the listener is being notified that a response has been
     * received from the connection manager.
     *
     * @param event event instance containing the message being sent
     */
    void responseReceived(BOSHMessageEvent event);

}
