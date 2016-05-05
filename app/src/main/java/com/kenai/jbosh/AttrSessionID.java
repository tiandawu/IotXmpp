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
 * Data type representing the getValue of the {@code sid} attribute of the
 * {@code bosh} element.
 */
final class AttrSessionID extends AbstractAttr<String> {
    
    /**
     * Creates a new attribute object.
     * 
     * @param val attribute getValue
     */
    private AttrSessionID(final String val) {
        super(val);
    }
    
    /**
     * Creates a new attribute instance from the provided String.
     *
     * @param str string representation of the attribute
     * @return attribute instance
     */
    static AttrSessionID createFromString(final String str) {
        return new AttrSessionID(str);
    }
    
}
