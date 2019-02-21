/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.strongkey.fido2mds.structures;

import com.strongkey.appliance.objects.JWT;

/**
 *
 * @author dpatterson
 */
public class MetadataTOC {
    
    JWT jwt;
    MetadataTOCPayload payload;

    public MetadataTOC() {
    }

    public void setJWT(JWT jwt) {
        this.jwt = jwt;
    }

    public void setPayload(MetadataTOCPayload payload) {
        this.payload = payload;
    }

    public JWT getJwt() {
        return jwt;
    }

    public MetadataTOCPayload getPayload() {
        return payload;
    }
    
}
