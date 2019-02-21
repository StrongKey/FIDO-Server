/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skce.pojos;

import java.util.Date;

public class FIDOSecretKeyInfo {

    private String secretkey = null;
    private Date creationdate = null;
    private int sid = 0;

    public FIDOSecretKeyInfo(String secretK, Integer sid) {
        this.secretkey = secretK;
        this.sid = sid;
        this.creationdate = new Date();
    }

    @Override
    public String toString() {
        return "FIDOSecretKeyInfo{" + "secretkey=" + secretkey + ", creationdate=" + creationdate + ", sid=" + sid + '}';
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }
    
    
}
