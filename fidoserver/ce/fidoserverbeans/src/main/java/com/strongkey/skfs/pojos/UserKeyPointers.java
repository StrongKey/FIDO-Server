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
 * POJO to store a map of randomid -> fidoregistrationkeyid. A timestamp is also
 * added along with the map to track the life time of these random ids. 
 *
 */

package com.strongkey.skfs.pojos;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * POJO to store a map of randomid -> fidoregistrationkeyid
 */
public class UserKeyPointers {
    
    /**
     * map of randomid->registrationKeyId of the database. The length of this map
     * will be equal to the number of keys successfully registered for the username.
     * 
     * IMPORTANT - there are no null checks for the input map during object construction.
     * It is the callers responsibility to ensure they pass in what they want.
     */
    private Map<String, String> userkeypointerMap = new ConcurrentSkipListMap<>();
    
    private Date creationdate = null;

    /**
     * Constructor of this class.
     * @param userkeypointerMap 
     */
    public UserKeyPointers(Map<String, String> userkeypointerMap) {
        this.userkeypointerMap = userkeypointerMap;
        this.creationdate = new Date();
    }

    /**
     * Get set methods
     * @return 
     */
    public Map<String, String> getUserKeyPointersMap() {
        return userkeypointerMap;
    }

    public void setUserKeyPointersMap(Map<String, String> userkeypointerMap) {
        this.userkeypointerMap = userkeypointerMap;
    }

    public void setUserkeypointerMap(Map<String, String> userkeypointerMap) {
        this.userkeypointerMap = userkeypointerMap;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }
    
    public long getUserKeyPointersAge() {
        Date rightnow = new Date();
        long age = (rightnow.getTime()/1000) - (creationdate.getTime()/1000);        
        return age;
    }
    
    /**
     * Over-ridden toString method to print the object content in a readable 
     * manner
     * @return  String with object content laid in a readable manner. 
     */
    @Override
    public String toString() {
        return    "\n    userkeypointerMap.length = " + this.userkeypointerMap.size() 
                + "\n    age = " + getUserKeyPointersAge() + " seconds";
    }
}
