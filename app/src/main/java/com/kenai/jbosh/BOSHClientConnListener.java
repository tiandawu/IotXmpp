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
 * Interface used by parties interested in monitoring the connection state
 * of a client session.
 */
public interface BOSHClientConnListener {

    /**
     * Called when the connection state of the client which the listener
     * is registered against has changed.  The event object supplied can
     * be used to determine the current session state.
     *
     * @param connEvent connection event describing the state
     */
    void connectionEvent(BOSHClientConnEvent connEvent);
    
}
