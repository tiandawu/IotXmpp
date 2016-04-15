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
 * Data type representing the getValue of the {@code pause} attribute of the
 * {@code bosh} element.
 */
final class AttrPause extends AbstractIntegerAttr {
    
    /**
     * Creates a new attribute object.
     * 
     * @param val attribute getValue
     * @throws BOSHException on parse or validation failure
     */
    private AttrPause(final String val) throws BOSHException {
        super(val);
        checkMinValue(1);
    }

    /**
     * Creates a new attribute instance from the provided String.
     * 
     * @param str string representation of the attribute
     * @return attribute instance or {@code null} if provided string is
     *  {@code null}
     * @throws BOSHException on parse or validation failure
     */
    static AttrPause createFromString(final String str)
    throws BOSHException {
        if (str == null) {
            return null;
        } else {
            return new AttrPause(str);
        }
    }
    
    /**
     * Get the pause time in milliseconds.
     *
     * @return pause tme in milliseconds
     */
    public int getInMilliseconds() {
        return (int) TimeUnit.MILLISECONDS.convert(
                intValue(), TimeUnit.SECONDS);
    }

}
