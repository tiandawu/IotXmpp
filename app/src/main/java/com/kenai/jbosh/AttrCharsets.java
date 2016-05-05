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
 * Data type representing the getValue of the {@code charsets} attribute of the
 * {@code bosh} element.
 */
final class AttrCharsets extends AbstractAttr<String> {

    /**
     * Array of the accepted character sets.
     */
    private final String[] charsets;

    /**
     * Creates a new attribute object.
     * 
     * @param val attribute getValue
     */
    private AttrCharsets(final String val) {
        super(val);
        charsets = val.split("\\ +");
    }
    
    /**
     * Creates a new attribute instance from the provided String.
     * 
     * @param str string representation of the attribute
     * @return attribute instance or {@code null} if provided string is
     *  {@code null}
     */
    static AttrCharsets createFromString(final String str) {
        if (str == null) {
            return null;
        } else {
            return new AttrCharsets(str);
        }
    }

    /**
     * Determines whether or not the specified charset is supported.
     *
     * @param name encoding name
     * @result {@code true} if the encoding is accepted, {@code false}
     *  otherwise
     */
    boolean isAccepted(final String name) {
        for (String str : charsets) {
            if (str.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
