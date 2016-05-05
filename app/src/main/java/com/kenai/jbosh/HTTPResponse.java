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
 * This class represents a complete HTTP response to a request made via
 * a {@code HTTPSender} send request.  Instances of this interface are
 * intended to represent a deferred, future response, not necessarily a
 * response which is immediately available.
 */
interface HTTPResponse {

    /**
     * Close out any resources still held by the original request.  The
     * conversation may need to be aborted if the session it was a part of
     * gets abruptly terminated.
     */
    void abort();

    /**
     * Get the HTTP status code of the response (e.g., 200, 404, etc.).  If
     * the response has not yet been received from the remote server, this
     * method should block until the response has arrived.
     *
     * @return HTTP status code
     * @throws InterruptedException if interrupted while awaiting response
     */
    int getHTTPStatus() throws InterruptedException, BOSHException;

    /**
     * Get the HTTP response message body.  If the response has not yet been
     * received from the remote server, this method should block until the
     * response has arrived.
     *
     * @return response message body
     * @throws InterruptedException if interrupted while awaiting response
     */
    AbstractBody getBody() throws InterruptedException, BOSHException;
    
}
