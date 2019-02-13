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
package com.strongauth.skfe.txbeans;

import com.strongauth.skfe.utilities.skfeCommon;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

@Stateless
public class originVerfierBean implements originVerfierBeanLocal, originVerifierBeanRemote {

    @Override
    public boolean execute(String appid, String origin) {
        try {
            URL originUrl = new URL(origin);
            String originFQDN = originUrl.getHost();
            URL appidUrl = new URL(appid);
            String appidFQDN = appidUrl.getHost();

            //section 3.1.2.1 - appid not https URL
            if (appid.startsWith("http://")) {
                if (originFQDN.equalsIgnoreCase(appidFQDN)) {
                    return true;
                }
            } else {
                //section 3.1.2.3 - facet/origin and appid is same
                if (originFQDN.equalsIgnoreCase(appidFQDN)) {
                    return true;
                }

                //section 3.1.2.4 - fetch trusted facets
                String domain = appidFQDN.startsWith("www.") ? appidFQDN.substring(4) : appidFQDN;
                String allowedtld = domain;

                allowedtld = skfeCommon.getTLdplusone(domain);

                JsonArray resJsonObj = null;
                JsonReader rdr = null;
                List<String> allowedfacets = new ArrayList<>();
                try {
                    InputStream is = appidUrl.openStream();
                    rdr = Json.createReader(is);

                    JsonObject obj = rdr.readObject();
                    JsonArray results = obj.getJsonArray("trustedFacets");
                    for (JsonObject result : results.getValuesAs(JsonObject.class)) {
                        resJsonObj = result.getJsonArray("ids");
                    }
                } catch (Exception e) {
                    InputStream is = appidUrl.openStream();
                    rdr = Json.createReader(is);
                    JsonArray results = rdr.readArray();
                    resJsonObj = results;
                }

                //parsing facets ids and discarding invalid ones
                for (int i = 0; i < resJsonObj.size(); i++) {
                    String facet = resJsonObj.getString(i);
                    if (facet.startsWith("https")) {
                        URL u = new URL(facet);
                        if (u.getHost().endsWith(allowedtld)) {
                            allowedfacets.add(facet);
                        }
                    }

                }
                if (allowedfacets.contains(origin)) {
                    return true;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean remoteexecute(String appid, String origin) {
        return execute(appid, origin);
    }
}
