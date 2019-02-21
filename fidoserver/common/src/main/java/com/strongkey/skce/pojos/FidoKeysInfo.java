/*
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
 */
package com.strongkey.skce.pojos;

import com.strongkey.skfe.entitybeans.FidoKeys;
import java.util.Date;

public class FidoKeysInfo {

    private Date creationdate;
    private FidoKeys fk;

    public FidoKeysInfo(){
        
    }
    
    public FidoKeysInfo(FidoKeys fk) {
        this.fk = fk;
        this.creationdate = new Date();
    }
    
    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public FidoKeys getFk() {
        return fk;
    }

    public void setFk(FidoKeys fk) {
        this.fk = fk;
    }

    public long getFidoKeysInfoAge() {
        Date rightnow = new Date();
        long age = (rightnow.getTime() / 1000) - (creationdate.getTime() / 1000);
        return age;
    }
}
