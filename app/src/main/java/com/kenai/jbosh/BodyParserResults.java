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
 * Data extracted from a raw XML message by a BodyParser implementation.
 * Currently, this is limited to the attributes of the wrapper element.
 */
final class BodyParserResults {

    /**
     * Map of qualified names to their values.  This map is defined to
     * match the requirement of the {@code Body} class to prevent
     * excessive copying.
     */
    private final Map<BodyQName, String> attrs =
            new HashMap<BodyQName, String>();

    /**
     * Constructor.
     */
    BodyParserResults() {
        // Empty
    }

    /**
     * Add an attribute definition to the results.
     *
     * @param name attribute's qualified name
     * @param value attribute value
     */
    void addBodyAttributeValue(
            final BodyQName name,
            final String value) {
        attrs.put(name, value);
    }

    /**
     * Returns the map of attributes added by the parser.
     *
     * @return map of atributes. Note: This is the live instance, not a copy.
     */
    Map<BodyQName, String> getAttributes() {
        return attrs;
    }

}
