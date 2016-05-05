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
 * Interface for parser implementations to implement in order to abstract the
 * business of XML parsing out of the Body class.  This allows us to leverage
 * a variety of parser implementations to gain performance advantages.
 */
interface BodyParser {

    /**
     * Parses the XML message, extracting the useful data from the initial
     * body element and returning it in a results object.
     *
     * @param xml XML to parse
     * @return useful data parsed out of the XML
     * @throws BOSHException on parse error
     */
    BodyParserResults parse(String xml) throws BOSHException;

}
