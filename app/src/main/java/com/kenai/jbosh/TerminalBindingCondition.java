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

import java.util.HashMap;
import java.util.Map;

/**
 * Terminal binding conditions and their associated messages.
 */
final class TerminalBindingCondition {

    /**
     * Map of condition names to condition instances.
     */
    private static final Map<String, TerminalBindingCondition>
            COND_TO_INSTANCE = new HashMap<String, TerminalBindingCondition>();

    /**
     * Map of HTTP response codes to condition instances.
     */
    private static final Map<Integer, TerminalBindingCondition>
            CODE_TO_INSTANCE = new HashMap<Integer, TerminalBindingCondition>();

    static final TerminalBindingCondition BAD_REQUEST =
            createWithCode("bad-request", "The format of an HTTP header or "
            + "binding element received from the client is unacceptable "
            + "(e.g., syntax error).", Integer.valueOf(400));

    static final TerminalBindingCondition HOST_GONE =
            create("host-gone", "The target domain specified in the 'to' "
            + "attribute or the target host or port specified in the 'route' "
            + "attribute is no longer serviced by the connection manager.");

    static final TerminalBindingCondition HOST_UNKNOWN =
            create("host-unknown", "The target domain specified in the 'to' "
            + "attribute or the target host or port specified in the 'route' "
            + "attribute is unknown to the connection manager.");

    static final TerminalBindingCondition IMPROPER_ADDRESSING =
            create("improper-addressing", "The initialization element lacks a "
            + "'to' or 'route' attribute (or the attribute has no value) but "
            + "the connection manager requires one.");

    static final TerminalBindingCondition INTERNAL_SERVER_ERROR =
            create("internal-server-error", "The connection manager has "
            + "experienced an internal error that prevents it from servicing "
            + "the request.");

    static final TerminalBindingCondition ITEM_NOT_FOUND =
            createWithCode("item-not-found", "(1) 'sid' is not valid, (2) "
            + "'stream' is not valid, (3) 'rid' is larger than the upper limit "
            + "of the expected window, (4) connection manager is unable to "
            + "resend response, (5) 'key' sequence is invalid.",
            Integer.valueOf(404));

    static final TerminalBindingCondition OTHER_REQUEST =
            create("other-request", "Another request being processed at the "
            + "same time as this request caused the session to terminate.");

    static final TerminalBindingCondition POLICY_VIOLATION =
            createWithCode("policy-violation", "The client has broken the "
            + "session rules (polling too frequently, requesting too "
            + "frequently, sending too many simultaneous requests).",
            Integer.valueOf(403));

    static final TerminalBindingCondition REMOTE_CONNECTION_FAILED =
            create("remote-connection-failed", "The connection manager was "
            + "unable to connect to, or unable to connect securely to, or has "
            + "lost its connection to, the server.");

    static final TerminalBindingCondition REMOTE_STREAM_ERROR =
            create("remote-stream-error", "Encapsulated transport protocol "
            + "error.");

    static final TerminalBindingCondition SEE_OTHER_URI =
            create("see-other-uri", "The connection manager does not operate "
            + "at this URI (e.g., the connection manager accepts only SSL or "
            + "TLS connections at some https: URI rather than the http: URI "
            + "requested by the client).");

    static final TerminalBindingCondition SYSTEM_SHUTDOWN =
            create("system-shutdown", "The connection manager is being shut "
            + "down. All active HTTP sessions are being terminated. No new "
            + "sessions can be created.");

    static final TerminalBindingCondition UNDEFINED_CONDITION =
            create("undefined-condition", "Unknown or undefined error "
            + "condition.");

    /**
     * Condition name.
     */
    private final String cond;

    /**
     * Descriptive message.
     */
    private final String msg;

    /**
     * Private constructor to pre
     */
    private TerminalBindingCondition(
            final String condition,
            final String message) {
        cond = condition;
        msg = message;
    }
    
    /**
     * Helper method to call the helper method to add entries.
     */
    private static TerminalBindingCondition create(
            final String condition,
            final String message) {
        return createWithCode(condition, message, null);
    }
    
    /**
     * Helper method to add entries.
     */
    private static TerminalBindingCondition createWithCode(
            final String condition,
            final String message,
            final Integer code) {
        if (condition == null) {
            throw(new IllegalArgumentException(
                    "condition may not be null"));
        }
        if (message == null) {
            throw(new IllegalArgumentException(
                    "message may not be null"));
        }
        if (COND_TO_INSTANCE.get(condition) != null) {
            throw(new IllegalStateException(
                    "Multiple definitions of condition: " + condition));
        }
        TerminalBindingCondition result =
                new TerminalBindingCondition(condition, message);
        COND_TO_INSTANCE.put(condition, result);
        if (code != null) {
            if (CODE_TO_INSTANCE.get(code) != null) {
                throw(new IllegalStateException(
                        "Multiple definitions of code: " + code));
            }
            CODE_TO_INSTANCE.put(code, result);
        }
        return result;
    }

    /**
     * Lookup the terminal binding condition instance with the condition
     * name specified.
     *
     * @param condStr condition name
     * @return terminal binding condition instance, or {@code null} if no
     *  instance is known by the name specified
     */
    static TerminalBindingCondition forString(final String condStr) {
        return COND_TO_INSTANCE.get(condStr);
    }

    /**
     * Lookup the terminal binding condition instance associated with the
     * HTTP response code specified.
     *
     * @param httpRespCode HTTP response code
     * @return terminal binding condition instance, or {@code null} if no
     *  instance is known by the response code specified
     */
    static TerminalBindingCondition forHTTPResponseCode(final int httpRespCode) {
        return CODE_TO_INSTANCE.get(Integer.valueOf(httpRespCode));
    }

    /**
     * Get the name of the condition.
     *
     * @return condition name
     */
    String getCondition() {
        return cond;
    }

    /**
     * Get the human readable error message associated with this condition.
     *
     * @return error message
     */
    String getMessage() {
        return msg;
    }

}
