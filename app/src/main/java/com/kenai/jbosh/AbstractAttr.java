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
 * Abstract base class for creating BOSH attribute classes.  Concrete
 * implementations of this class will naturally inherit the underlying
 * type's behavior for {@code equals()}, {@code hashCode()},
 * {@code toString()}, and {@code compareTo()}, allowing for the easy
 * creation of objects which extend existing trivial types.  This was done
 * to comply with the prefactoring rule declaring, "when you are being
 * abstract, be abstract all the way".
 *
 * @param <T> type of the extension object
 */
abstract class AbstractAttr<T extends Comparable>
    implements Comparable {

    /**
     * Captured value.
     */
    private final T value;

    /**
     * Creates a new encapsulated object instance.
     *
     * @param aValue encapsulated getValue
     */
    protected AbstractAttr(final T aValue) {
        value = aValue;
    }

    /**
     * Gets the encapsulated data value.
     *
     * @return data value
     */
    public final T getValue() {
        return value;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Object method overrides:

    /**
     * {@inheritDoc}
     *
     * @param otherObj object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) {
            return false;
        } else if (otherObj instanceof AbstractAttr) {
            AbstractAttr other =
                    (AbstractAttr) otherObj;
            return value.equals(other.value);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return hashCode of the encapsulated object
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @return string representation of the encapsulated object
     */
    @Override
    public String toString() {
        return value.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Comparable interface:

    /**
     * {@inheritDoc}
     *
     * @param otherObj object to compare to
     * @return -1, 0, or 1
     */
    @SuppressWarnings("unchecked")
    public int compareTo(final Object otherObj) {
        if (otherObj == null) {
            return 1;
        } else {
            return value.compareTo(otherObj);
        }
    }

}
