/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.fido2;

import java.util.ArrayList;
import java.util.Map;

public class NoneAttestationStatement implements FIDO2AttestationStatement {

    @Override
    public void decodeAttestationStatement(Object attStmt) {
        Map<String, Object> attStmtObjectMap = (Map<String, Object>) attStmt;
        
        if(!attStmtObjectMap.isEmpty()){
            throw new IllegalArgumentException("None attestation contains data");
        }
    }

    @Override
    public Boolean verifySignature(String browserDataBase64, FIDO2AuthenticatorData authData) {
        return true;
    }

    @Override
    public ArrayList getX5c() {
        return null;
    }

    @Override
    public String getAttestationType() {
        return "none";
    }
}
