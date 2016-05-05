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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A request and response pair representing a single exchange with a remote
 * content manager.  This is primarily a container class intended to maintain
 * the relationship between the request and response but allows the response
 * to be added after the fact.
 */
final class HTTPExchange {

    /**
     * Logger.
     */
    private static final Logger LOG =
            Logger.getLogger(HTTPExchange.class.getName());
    
    /**
     * Request body.
     */
    private final AbstractBody request;

    /**
     * Lock instance used to protect and provide conditions.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Condition used to signal when the response has been set.
     */
    private final Condition ready = lock.newCondition();

    /**
     * HTTPResponse instance.
     */
    private HTTPResponse response;

    ///////////////////////////////////////////////////////////////////////////
    // Constructor:

    /**
     * Create a new request/response pair object.
     *
     * @param req request message body
     */
    HTTPExchange(final AbstractBody req) {
        if (req == null) {
            throw(new IllegalArgumentException("Request body cannot be null"));
        }
        request = req;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Package-private methods:

    /**
     * Get the original request message.
     *
     * @return request message body.
     */
    AbstractBody getRequest() {
        return request;
    }

    /**
     * Set the HTTPResponse instance.
     *
     * @return HTTPResponse instance associated with the request.
     */
    void setHTTPResponse(HTTPResponse resp) {
        lock.lock();
        try {
            if (response != null) {
                throw(new IllegalStateException(
                        "HTTPResponse was already set"));
            }
            response = resp;
            ready.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the HTTPResponse instance.
     *
     * @return HTTPResponse instance associated with the request.
     */
    HTTPResponse getHTTPResponse() {
        lock.lock();
        try {
            while (response == null) {
                try {
                    ready.await();
                } catch (InterruptedException intx) {
                    LOG.log(Level.FINEST, "Interrupted", intx);
                }
            }
            return response;
        } finally {
            lock.unlock();
        }
    }

}
