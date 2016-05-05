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
 * Exception class used by the BOSH API to minimize the number of checked
 * exceptions which must be handled by the user of the API.
 */
public class BOSHException extends Exception {

    /**
     * Servial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception isntance with the specified descriptive message.
     *
     * @param msg description of the exceptional condition
     */
    public BOSHException(final String msg) {
        super(msg);
    }

    /**
     * Creates a new exception isntance with the specified descriptive
     * message and the underlying root cause of the exceptional condition.
     *
     * @param msg description of the exceptional condition
     * @param cause root cause or instigator of the condition
     */
    public BOSHException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
