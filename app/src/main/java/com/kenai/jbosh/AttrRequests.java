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
 * Data type representing the value of the {@code requests} attribute of the
 * {@code bosh} element.
 */
final class AttrRequests extends AbstractIntegerAttr {
    
    /**
     * Creates a new attribute object.
     * 
     * @param val attribute value
     * @throws BOSHException on parse or validation failure
     */
    private AttrRequests(final String val) throws BOSHException {
        super(val);
        checkMinValue(1);
    }

    /**
     * Creates a new attribute instance from the provided String.
     * 
     * @param str string representation of the attribute
     * @return instance of the attribute for the specified string, or
     *  {@code null} if input string is {@code null}
     * @throws BOSHException on parse or validation failure
     */
    static AttrRequests createFromString(final String str)
    throws BOSHException {
        if (str == null) {
            return null;
        } else {
            return new AttrRequests(str);
        }
    }
    
}
