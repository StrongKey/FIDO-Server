/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.fido2.tpm;

import java.util.Arrays;

/**
 *
 * @author mishimoto
 */
public class TPMRSAUnique extends TPM2B implements TPMUnique {

    public TPMRSAUnique(byte[] data) {
        super(data);
    }

    @Override
    public boolean equals(TPMUnique unique) {
        if (!(unique instanceof TPMRSAUnique)) {
            return false;
        }

        TPMRSAUnique rsaunique = (TPMRSAUnique) unique;

        return Arrays.equals(this.getData(), rsaunique.getData());
    }
    
}
