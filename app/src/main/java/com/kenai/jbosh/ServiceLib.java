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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility library for use in loading services using the Jar Service
 * Provider Interface (Jar SPI).  This can be replaced once the minimum
 * java rev moves beyond Java 5.
 */
final class ServiceLib {

    /**
     * Logger.
     */
    private static final Logger LOG =
            Logger.getLogger(ServiceLib.class.getName());

    ///////////////////////////////////////////////////////////////////////////
    // Package-private methods:

    /**
     * Prevent construction.
     */
    private ServiceLib() {
        // Empty
    }

    ///////////////////////////////////////////////////////////////////////////
    // Package-private methods:

    /**
     * Probe for and select an implementation of the specified service
     * type by using the a modified Jar SPI mechanism.  Modified in that
     * the system properties will be checked to see if there is a value
     * set for the naem of the class to be loaded.  If so, that value is
     * treated as the class name of the first implementation class to be
     * attempted to be loaded.  This provides a (unsupported) mechanism
     * to insert other implementations.  Note that the supported mechanism
     * is by properly ordering the classpath.
     *
     * @return service instance
     * @throws IllegalStateException is no service implementations could be
     *  instantiated
     */
    static <T> T loadService(Class<T> ofType) {
        List<String> implClasses = loadServicesImplementations(ofType);
        for (String implClass : implClasses) {
            T result = attemptLoad(ofType, implClass);
            if (result != null) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Selected " + ofType.getSimpleName()
                            + " implementation: "
                            + result.getClass().getName());
                }
                return result;
            }
        }
        throw(new IllegalStateException(
                "Could not load " + ofType.getName() + " implementation"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private methods:

    /**
     * Generates a list of implementation class names by using
     * the Jar SPI technique.  The order in which the class names occur
     * in the service manifest is significant.
     *
     * @return list of all declared implementation class names
     */
    private static List<String> loadServicesImplementations(
            final Class ofClass) {
        List<String> result = new ArrayList<String>();

        // Allow a sysprop to specify the first candidate
        String override = System.getProperty(ofClass.getName());
        if (override != null) {
            result.add(override);
        }

        ClassLoader loader = ServiceLib.class.getClassLoader();
        URL url = loader.getResource("META-INF/services/" + ofClass.getName());
        InputStream inStream = null;
        InputStreamReader reader = null;
        BufferedReader bReader = null;
        try {
            inStream = url.openStream();
            reader = new InputStreamReader(inStream);
            bReader = new BufferedReader(reader);
            String line;
            while ((line = bReader.readLine()) != null) {
                if (!line.matches("\\s*(#.*)?")) {
                    // not a comment or blank line
                    result.add(line.trim());
                }
            }
        } catch (IOException iox) {
            LOG.log(Level.WARNING,
                    "Could not load services descriptor: " + url.toString(),
                    iox);
        } finally {
            finalClose(bReader);
            finalClose(reader);
            finalClose(inStream);
        }
        return result;
    }

    /**
     * Attempts to load the specified implementation class.
     * Attempts will fail if - for example - the implementation depends
     * on a class not found on the classpath.
     *
     * @param className implementation class to attempt to load
     * @return service instance, or {@code null} if the instance could not be
     *  loaded
     */
    private static <T> T attemptLoad(
            final Class<T> ofClass,
            final String className) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Attempting service load: " + className);
        }
        Level level;
        Exception thrown;
        try {
            Class clazz = Class.forName(className);
            if (!ofClass.isAssignableFrom(clazz)) {
                if (LOG.isLoggable(Level.WARNING)) {
                    LOG.warning(clazz.getName() + " is not assignable to "
                            + ofClass.getName());
                }
                return null;
            }
            return ofClass.cast(clazz.newInstance());
        } catch (ClassNotFoundException ex) {
            level = Level.FINEST;
            thrown = ex;
        } catch (InstantiationException ex) {
            level = Level.WARNING;
            thrown = ex;
        } catch (IllegalAccessException ex) {
            level = Level.WARNING;
            thrown = ex;
        }
        LOG.log(level,
                "Could not load " + ofClass.getSimpleName()
                + " instance: " + className,
                thrown);
        return null;
    }

    /**
     * Check and close a closeable object, trapping and ignoring any
     * exception that might result.
     *
     * @param closeMe the thing to close
     */
    private static void finalClose(final Closeable closeMe) {
        if (closeMe != null) {
            try {
                closeMe.close();
            } catch (IOException iox) {
                LOG.log(Level.FINEST, "Could not close: " + closeMe, iox);
            }
        }
    }

}
