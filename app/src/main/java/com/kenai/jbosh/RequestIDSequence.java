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

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Request ID sequence generator.  This generator generates a random first
 * RID and then manages the sequence from there on out.
 */
final class RequestIDSequence {

    /**
     * Maximum number of bits available for representing request IDs, according
     * to the XEP-0124 spec.s
     */
    private static final int MAX_BITS = 53;

    /**
     * Bits devoted to incremented values.
     */
    private static final int INCREMENT_BITS = 32;

    /**
     * Minimum number of times the initial RID can be incremented before
     * exceeding the maximum.
     */
    private static final long MIN_INCREMENTS = 1L << INCREMENT_BITS;

    /**
     * Max initial value. 
     */
    private static final long MAX_INITIAL = (1L << MAX_BITS) - MIN_INCREMENTS;

    /**
     * Max bits mask.
     */
    private static final long MASK = ~(Long.MAX_VALUE << MAX_BITS);

    /**
     * Random number generator.
     */
    private static final SecureRandom RAND = new SecureRandom();

    /**
     * Internal lock.
     */
    private static final Lock LOCK = new ReentrantLock();

    /**
     * The last reqest ID used, or &lt;= 0 if a new request ID needs to be
     * generated.
     */
    private AtomicLong nextRequestID = new AtomicLong();

    ///////////////////////////////////////////////////////////////////////////
    // Constructors:

    /**
     * Prevent direct construction.
     */
    RequestIDSequence() {
        nextRequestID = new AtomicLong(generateInitialValue());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public methods:

    /**
     * Calculates the next request ID value to use.  This number must be
     * initialized such that it is unlikely to ever exceed 2 ^ 53, according
     * to XEP-0124.
     *
     * @return next request ID value
     */
    public long getNextRID() {
        return nextRequestID.getAndIncrement();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private methods:

    /**
     * Generates an initial RID value by generating numbers until a number is
     * found which is smaller than the maximum allowed value and greater
     * than zero.
     *
     * @return random initial value
     */
    private long generateInitialValue() {
        long result;
        LOCK.lock();
        try {
            do {
                result = RAND.nextLong() & MASK;
            } while (result > MAX_INITIAL);
        } finally {
            LOCK.unlock();
        }
        return result;
    }

}
