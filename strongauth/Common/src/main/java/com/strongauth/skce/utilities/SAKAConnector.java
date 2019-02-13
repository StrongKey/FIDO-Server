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
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 *
 * ************************************************
 *
 *  888b    888          888
 *  8888b   888          888
 *  88888b  888          888
 *  888Y88b 888  .d88b.  888888  .d88b.  .d8888b
 *  888 Y88b888 d88""88b 888    d8P  Y8b 88K
 *  888  Y88888 888  888 888    88888888 "Y8888b.
 *  888   Y8888 Y88..88P Y88b.  Y8b.          X88
 *  888    Y888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * ************************************************
 *
 * This class defines an interface to fetch the port object for a specific saka
 * host url that is a part of a saka cluster.
 * 
 * This class is a singleton class which when invoked for the first time will 
 * create port objects for all saka host urls in all saka clusters specified in 
 * the properties file.
 * 
 * These pre-created port objects are stored in hash maps and are returned whenever
 * requested. With this paradigm, port objects are re-used and hence need not be
 * created every time. 
 * 
 */
package com.strongauth.skce.utilities;

//import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.saka.web.Encryption;

public class SAKAConnector implements Runnable {
    private static volatile SAKAConnector sakaconn = null;
    
    protected SAKAConnector()
    {
        createPorts();   
    }
      
    public static SAKAConnector getSAKAConn() {

        if (sakaconn == null) {
            synchronized (SAKAConnector.class) {
                if (sakaconn == null) {
                    sakaconn = new SAKAConnector();
                }
            }
        }
        return sakaconn;
    }
      
    /**
     * Creates and stores SAKA port objects that are used for future when 
     * connection needs to be made to SAKA.
     */
    private static void createPorts() {
       
    }
    
    /**
     * Fetches a SAKA port (SOAP binding object needed to make a web-service request)
     * for the specified host url which is a part of a SAKA cluster specified by
     * clusterid.
     * @param clusterid saka cluster id
     * @param hosturl   saka host url for which the port object is request for.
     * @return The Encryption class object, using which web-service calls can
     *          be made.
     */
    public Encryption getSAKAPort(int clusterid, String hosturl) {
        return null;
    }
    
    /**
     * Overridden toString method used for debugging.
     * This method actually prints out all SAKA clusters that are read from the 
     * properties file and all SOAP port objects related to each saka cluster.
     * 
     * @return String containing the local map information that contains port 
     *          objects.
     */
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}


