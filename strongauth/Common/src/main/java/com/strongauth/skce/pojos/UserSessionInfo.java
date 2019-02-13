/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, as published by the Free
 * Software Foundation and available at
 * http://www.fsf.org/licensing/licenses/lgpl.html, version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$ $Revision$
 * $Author$ $URL:
 * https://svn.strongauth.com/repos/jade/trunk/skce/skcebeans/src/main/java/com/strongauth/skce/pojos/UserSessionInfo.java
 * $
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
 * POJO to define a fido user session.
 *
 */
package com.strongauth.skce.pojos;

import java.io.Serializable;
import java.util.Date;

public class UserSessionInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username = null;
    private String displayName = null;
    private String rpName = null;
    private String nonce = null;
    private String userId = null;
    private String appid = null;
    private String sessiontype = null;
    private Date creationdate = null;
    private String userPublicKey = null;
    //fido key id
    private long fkid = 0;
    //server id where the key was originally registered
    private Short skid = 0;
    //server id where the prereg/preauth has been originated
    private Short sid = 0;
    private String sessionid = null;
    
    private String userIcon = null;
    private String userVerificationReq = null;
    private String attestationPreferance = null;
    private String policyMapKey = null;
    
    private String mapkey;

    /**
     * Constructor of this class.
     *
     * @param username - name of the user for this associated with this session
     * @param nonce - nonce generated for session (registration or auth)
     * @param appid - appid associated with the user session
     * @param sessiontype - type of the session (register or auth)
     * @param userPublicKey - use public key
     * @param sessionID
     */
    public UserSessionInfo(String username, String nonce, String appid, String sessiontype, String userPublicKey, String sessionID) {
        this.username = username;
        this.nonce = nonce;
        this.appid = appid;
        this.sessiontype = sessiontype;
        this.creationdate = new Date();
        this.userPublicKey = userPublicKey;
        this.sessionid = sessionID;
    }
    
    //Empty Constructor
    public UserSessionInfo(){
        this.creationdate = new Date();
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSessionid() {
        return sessionid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSessiontype() {
        return sessiontype;
    }

    public void setSessiontype(String sessiontype) {
        this.sessiontype = sessiontype;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public long getSessionAge() {
        Date rightnow = new Date();
        long age = (rightnow.getTime() / 1000) - (creationdate.getTime() / 1000);
        return age;
    }

    public String getUserPublicKey() {
        return userPublicKey;
    }

    public void setUserPublicKey(String userPublicKey) {
        this.userPublicKey = userPublicKey;
    }

    public long getFkid() {
        return fkid;
    }

    public void setFkid(long fkid) {
        this.fkid = fkid;
    }

    public Short getSid() {
        return sid;
    }

    public void setSid(Short sid) {
        this.sid = sid;
    }

    public Short getSkid() {
        return skid;
    }

    public void setSkid(Short skid) {
        this.skid = skid;
    }

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String mapkey) {
        this.mapkey = mapkey;
    }
    
    public String getRpName() {
        return rpName;
    }

    public void setRpName(String rpName) {
        this.rpName = rpName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }
    
    public String getUserVerificationReq(){
        return userVerificationReq;
    }
    
    public void setuserVerificationReq(String userVerificationReq){
        this.userVerificationReq = userVerificationReq;
    }

    public String getAttestationPreferance() {
        return attestationPreferance;
    }

    public void setAttestationPreferance(String attestationPreferance) {
        this.attestationPreferance = attestationPreferance;
    }
    
    public String getPolicyMapKey() {
        return policyMapKey;
    }

    public void setPolicyMapKey(String policyMapKey) {
        this.policyMapKey = policyMapKey;
    }
    /**
     * Over-ridden toString method to print the object content in a readable
     * manner
     *
     * @return String with object content laid in a readable manner.
     */
    @Override
    public String toString() {
        return    "\n    username       = " + this.username
                + "\n    challenge      = " + this.nonce
                + "\n    appid          = " + this.appid
                + "\n    sessiontype    = " + this.sessiontype
                + "\n    sessioni       = " + this.sessionid
                + "\n    UPK            = " + this.userPublicKey
                + "\n    age            = " + getSessionAge() + " seconds"
                + "\n    userId         = " + this.userId
                + "\n    userIcon       = " + this.userIcon
                + "\n    policyMapKey   = " + this.policyMapKey;
    }
}
