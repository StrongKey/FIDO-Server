/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License, as published by the Free Software Foundation and
 * available at http://www.fsf.org/licensing/licenses/lgpl.html,
 * version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date: $
 * $Revision: $
 * $Author: $
 * $URL: $
 *
 * *********************************************
 *                    888
 *                    888
 *                    888
 *  88888b.   .d88b.  888888  .d88b.  .d8888b
 *  888 "88b d88""88b 888    d8P  Y8b 88K
 *  888  888 888  888 888    88888888 "Y8888b.
 *  888  888 Y88..88P Y88b.  Y8b.          X88
 *  888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 *
 */
package com.strongkey.appliance.utilities;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("ClassWithMultipleLoggers")
public class strongkeyLogger {

    private static final String classname = "strongkeyLogger";

    // Logger for the application
    private static final Logger APPL_LOGGER = Logger.getLogger("APPL", "resources.appliance.appliance-messages_" + Locale.getDefault());

    static {

    }

    public strongkeyLogger() {

    }

    /**
     * Prints the source-class and method names to the application logger upon
     * entering the class method
     *
     * @param logger
     * @param sourceClass - the classname of the class that called this method
     * @param sourceMethod - the name of the method in which this method is
     * called
     */
    public static void entering(String logger, String sourceClass, String sourceMethod) {
        APPL_LOGGER.entering(sourceClass, sourceMethod);

    }

    /**
     * Prints the source-class and method names to the application logger before
     * exiting the class method
     *
     * @param logger
     * @param sourceClass - the classname of the class that called this method
     * @param sourceMethod - the name of the method in which this method is
     * called
     */
    public static void exiting(String logger, String sourceClass, String sourceMethod) {
        APPL_LOGGER.exiting(sourceClass, sourceMethod);
    }

    public static void log(String logger, java.util.logging.Level level, String key, Object param) {
        APPL_LOGGER.log(level, key, param);

    }

    public static void log(String logger, java.util.logging.Level level, String key, Object[] params) {
        APPL_LOGGER.log(level, key, params);
    }

    public static void log(String logger, java.util.logging.Level level, String key) {
        APPL_LOGGER.log(level, key);
    }

    public static void logp(String logger, java.util.logging.Level level,
            String sourceClass, String sourceMethod, String key, Object param) {
        APPL_LOGGER.logp(level, sourceClass, sourceMethod, key, param);
    }

    public static void logp(String logger, java.util.logging.Level level,
            String sourceClass, String sourceMethod, String key) {
        APPL_LOGGER.logp(level, sourceClass, sourceMethod, key);
    }

    public static void logp(String logger, java.util.logging.Level level,
            String sourceClass, String sourceMethod, String key, Object[] params) {
        APPL_LOGGER.logp(level, sourceClass, sourceMethod, key, params);
    }

    public static void printStrongAuthStackTrace(String logger, String sourceclassname, String sourcemethod, Exception ex) {
        StackTraceElement err[] = ex.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        for (StackTraceElement err1 : err) {
            if (err1.toString().contains("com.strongauth") || err1.toString().contains("Caused by")) {
                sb.append('\t').append(err1).append('\n');
            }
        }
        logp(logger, Level.SEVERE, sourceclassname, sourcemethod, "FSO-MSG-5000", sb.append('\n').toString());
    }

}
