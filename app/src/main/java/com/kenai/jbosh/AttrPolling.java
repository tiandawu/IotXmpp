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

import java.util.concurrent.TimeUnit;

/**
 * Data type representing the getValue of the {@code polling} attribute of the
 * {@code bosh} element.
 */
final class AttrPolling extends AbstractIntegerAttr {

    /**
     * Creates a new attribute object.
     * 
     * @param val attribute getValue
     * @throws BOSHException on parse or validation failure
     */
    private AttrPolling(final String str) throws BOSHException {
        super(str);
        checkMinValue(0);
    }

    /**
     * Creates a new attribute instance from the provided String.
     * 
     * @param str string representation of the attribute
     * @return instance of the attribute for the specified string, or
     *  {@code null} if input string is {@code null}
     * @throws BOSHException on parse or validation failure
     */
    static AttrPolling createFromString(final String str)
    throws BOSHException {
        if (str == null) {
            return null;
        } else {
            return new AttrPolling(str);
        }
    }

    /**
     * Get the polling interval in milliseconds.
     *
     * @return polling interval in milliseconds
     */
    public int getInMilliseconds() {
        return (int) TimeUnit.MILLISECONDS.convert(
                intValue(), TimeUnit.SECONDS);
    }
    
}
