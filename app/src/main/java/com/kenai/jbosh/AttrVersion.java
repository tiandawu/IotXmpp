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
 * Data type representing the getValue of the {@code ver} attribute of the
 * {@code bosh} element.
 */
final class AttrVersion extends AbstractAttr<String> implements Comparable {

    /**
     * Default value if none is provided.
     */
    private static final AttrVersion DEFAULT;
    static {
        try {
            DEFAULT = createFromString("1.8");
        } catch (BOSHException boshx) {
            throw(new IllegalStateException(boshx));
        }
    }

    /**
     * Major portion of the version.
     */
    private final int major;
    
    /**
     * Minor portion of the version.
     */
    private final int minor;

    /**
     * Creates a new attribute object.
     * 
     * @param val attribute getValue
     * @throws BOSHException on parse or validation failure
     */
    private AttrVersion(final String val) throws BOSHException {
        super(val);

        int idx = val.indexOf('.');
        if (idx <= 0) {
            throw(new BOSHException(
                    "Illegal ver attribute value (not in major.minor form): "
                    + val));
        }

        String majorStr = val.substring(0, idx);
        try {
            major = Integer.parseInt(majorStr);
        } catch (NumberFormatException nfx) {
            throw(new BOSHException(
                    "Could not parse ver attribute value (major ver): "
                    + majorStr,
                    nfx));
        }
        if (major < 0) {
            throw(new BOSHException(
                    "Major version may not be < 0"));
        }

        String minorStr = val.substring(idx + 1);
        try {
            minor = Integer.parseInt(minorStr);
        } catch (NumberFormatException nfx) {
            throw(new BOSHException(
                    "Could not parse ver attribute value (minor ver): "
                    + minorStr,
                    nfx));
        }
        if (minor < 0) {
            throw(new BOSHException(
                    "Minor version may not be < 0"));
        }
    }
    
    /**
     * Get the version of specifcation that we support.
     *
     * @return max spec version the code supports
     */
    static AttrVersion getSupportedVersion() {
        return DEFAULT;
    }

    /**
     * Creates a new attribute instance from the provided String.
     * 
     * @param str string representation of the attribute
     * @return attribute instance or {@code null} if provided string is
     *  {@code null}
     * @throws BOSHException on parse or validation failure
     */
    static AttrVersion createFromString(final String str)
    throws BOSHException {
        if (str == null) {
            return null;
        } else {
            return new AttrVersion(str);
        }
    }

    /**
     * Returns the 'major' portion of the version number.
     *
     * @return major digits only
     */
    int getMajor() {
        return major;
    }

    /**
     * Returns the 'minor' portion of the version number.
     *
     * @return minor digits only
     */
    int getMinor() {
        return minor;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Comparable interface:

    /**
     * {@inheritDoc}
     *
     * @param otherObj object to compare to
     * @return -1, 0, or 1
     */
    @Override
    public int compareTo(final Object otherObj) {
        if (otherObj instanceof AttrVersion) {
            AttrVersion other = (AttrVersion) otherObj;
            if (major < other.major) {
                return -1;
            } else if (major > other.major) {
                return 1;
            } else if (minor < other.minor) {
                return -1;
            } else if (minor > other.minor) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

}
