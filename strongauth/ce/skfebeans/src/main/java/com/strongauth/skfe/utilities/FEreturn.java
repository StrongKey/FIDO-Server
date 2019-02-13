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
 * Fido engine return values
 *
 */
package com.strongauth.skfe.utilities;

import java.io.Serializable;
import java.io.StringReader;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

public class FEreturn implements Serializable {

    private String jsonResponse = null;
    private Object response = null;
    private String logmsg = "";
    
    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getLogmsg() {
        return logmsg;
    }

    public void append(String msg) {

        if (logmsg == null) {
            logmsg = "\n" + msg + "\n";
        } else {
            logmsg += (msg + "\n");
        }

    }

    /**
     * Clean-up method to make sure RV's are correct
     */
    public void cleanUp()
    {
        this.logmsg = "";
        this.jsonResponse = null;
        this.response = null;
    }
    
    /**
     * Returns a pretty Json
     * @param Input
     * @return 
     */
    public String returnJSON(String Input) {
        String JSON_FORMAT_STRING = "%-10s"; 
        JsonParserFactory factory = Json.createParserFactory(null);
        
        StringBuilder sb;
        try (JsonParser parser = factory.createParser(new StringReader(Input))) {
            sb = new StringBuilder();
            sb.append("\n\t{\n");
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();
                
                switch (event) {
                    case KEY_NAME: {
                        sb.append("\t\t\"");
                        sb.append(String.format(JSON_FORMAT_STRING, parser.getString()));
                        sb.append("\" : \"");
                        break;
                    }
                    case VALUE_STRING: {
                        sb.append(parser.getString()).append("\",\n");
                        break;
                    }
                }
            }   sb.append("\t}");
        }

        return sb.toString();
    }
}
