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
 * **********************************************
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
 * **********************************************
 *
 * The FIDOReturnObject (FIDORO) is the generic object returned by all 
 * web-services provided by the fido's rest/soap and web-socket based interfaces.  
 *
 */
package com.strongauth.skfe.pojos;

import com.strongauth.skfe.utilities.skfeConstants;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * POJO to bind the fido web services' response being sent back to the calling
 * application.
 */
public class FIDOReturnObject {
      
    /**
     * Local variables
     */
    private String response = "";
       
    /**
     * Constructor of this class.
     * 
     * @param response
     */
    public FIDOReturnObject(String response) {
        
        if (response != null) {
            this.response = response;
        }
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
            
    /**
     * Over-ridden toString method to print the object content in a readable 
     * manner
     * @return  String with object content laid in a readable manner. 
     */
    @Override
    public String toString() {
        return "\n\tresponse    = " + this.response;
    }
    
    /**
     * Constructs this class object as a Json to be passed back to the client.
     * 
     * @return  - String object of the Json representation of this object
     */
    public String toJsonString() {
        
        if ( response == null ) {
            response = "";
        }
        
        // Build the output json object
        JsonObject responseJSON = null;
        
        responseJSON = Json.createObjectBuilder()
        .add(skfeConstants.JSON_KEY_SERVLET_RETURN_RESPONSE, response)
        .build();   
        
        if ( responseJSON != null )
            return responseJSON.toString();
        else
            return null;
    }
}
