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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

/**
 * Client connection event, notifying of changes in connection state.
 * <p/>
 * This class is immutable and thread-safe.
 */
public final class BOSHClientConnEvent extends EventObject {

    /**
     * Serialized version.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Boolean flag indicating whether or not a session has been established
     * and is currently active.
     */
    private final boolean connected;

    /**
     * List of outstanding requests which may not have been sent and/or
     * acknowledged by the remote CM.
     */
    private final List<ComposableBody> requests;

    /**
     * Cause of the session termination, or {@code null}.
     */
    private final Throwable cause;

    /**
     * Creates a new connection event instance.
     *
     * @param source event source
     * @param cConnected flag indicating whether or not the session is
     *  currently active
     * @param cRequests outstanding requests when an error condition is
     *  detected, or {@code null} when not an error condition
     * @param cCause cause of the error condition, or {@code null} when no
     *  error condition is present
     */
    private BOSHClientConnEvent(
            final BOSHClient source,
            final boolean cConnected,
            final List<ComposableBody> cRequests,
            final Throwable cCause) {
        super(source);
        connected = cConnected;
        cause = cCause;

        if (connected) {
            if (cCause != null) {
                throw(new IllegalStateException(
                        "Cannot be connected and have a cause"));
            }
            if (cRequests != null && cRequests.size() > 0) {
                throw(new IllegalStateException(
                        "Cannot be connected and have outstanding requests"));
            }
        }

        if (cRequests == null) {
            requests = Collections.emptyList();
        } else {
            // Defensive copy:
            requests = Collections.unmodifiableList(
                    new ArrayList<ComposableBody>(cRequests));
        }
    }

    /**
     * Creates a new connection establishment event.
     *
     * @param source client which has become connected
     * @return event instance
     */
    static BOSHClientConnEvent createConnectionEstablishedEvent(
            final BOSHClient source) {
        return new BOSHClientConnEvent(source, true, null, null);
    }

    /**
     * Creates a new successful connection closed event.  This represents
     * a clean termination of the client session.
     *
     * @param source client which has been disconnected
     * @return event instance
     */
    static BOSHClientConnEvent createConnectionClosedEvent(
            final BOSHClient source) {
        return new BOSHClientConnEvent(source, false, null, null);
    }

    /**
     * Creates a connection closed on error event.  This represents
     * an unexpected termination of the client session.
     *
     * @param source client which has been disconnected
     * @param outstanding list of requests which may not have been received
     *  by the remote connection manager
     * @param cause cause of termination
     * @return event instance
     */
    static BOSHClientConnEvent createConnectionClosedOnErrorEvent(
            final BOSHClient source,
            final List<ComposableBody> outstanding,
            final Throwable cause) {
        return new BOSHClientConnEvent(source, false, outstanding, cause);
    }

    /**
     * Gets the client from which this event originated.
     *
     * @return client instance
     */
    public BOSHClient getBOSHClient() {
        return (BOSHClient) getSource();
    }

    /**
     * Returns whether or not the session has been successfully established
     * and is currently active.
     *
     * @return {@code true} if a session is active, {@code false} otherwise
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Returns whether or not this event indicates an error condition.  This
     * will never return {@code true} when {@code isConnected()} returns
     * {@code true}.
     *
     * @return {@code true} if the event indicates a terminal error has
     *  occurred, {@code false} otherwise.
     */
    public boolean isError() {
        return cause != null;
    }

    /**
     * Returns the underlying cause of the error condition.  This method is
     * guaranteed to return {@code null} when @{code isError()} returns
     * {@code false}.  Similarly, this method is guaranteed to return
     * non-@{code null} if {@code isError()} returns {@code true}.
     *
     * @return underlying cause of the error condition, or {@code null} if
     *  this event does not represent an error condition
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Get the list of requests which may not have been sent or were not
     * acknowledged by the remote connection manager prior to session
     * termination.
     *
     * @return list of messages which may not have been received by the remote
     *  connection manager, or an empty list if the session is still connected
     */
    public List<ComposableBody> getOutstandingRequests() {
        return requests;
    }
    
}
