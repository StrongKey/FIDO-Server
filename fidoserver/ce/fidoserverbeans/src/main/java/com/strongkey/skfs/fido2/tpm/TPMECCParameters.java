/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.fido2.tpm;

/**
 *
 * @author mishimoto
 */
public class TPMECCParameters implements TPMParameters {
    private final TPMSymmetricStruct symmBits;
    
    private final TPMScheme scheme;
    
    private final short curveID;
    
    //These variables are a bit of a stub in the specification, as they do not
    //really do anything and are suppose to be hardcoded to null values
    private final TPMScheme kdfScheme;
    
    
    public TPMECCParameters(TPMSymmetricStruct symmBits, TPMScheme scheme, 
            short curveID, TPMScheme kdfScheme) {
        this.symmBits = symmBits;
        this.scheme = scheme;
        this.curveID = curveID;
        this.kdfScheme = kdfScheme;
    }

    public short getCurveID() {
        return curveID;
    }

    @Override
    public byte[] marshalData() {
        return Marshal.marshalObjects(
                symmBits,
                scheme,
                curveID,
                kdfScheme);
    }
}
