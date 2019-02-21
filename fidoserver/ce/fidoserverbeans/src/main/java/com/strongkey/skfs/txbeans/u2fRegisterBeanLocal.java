/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.txbeans;

import com.strongkey.skfs.utilities.SKFEException;
import com.strongkey.skfs.utilities.FEreturn;
import javax.ejb.Local;

@Local
public interface u2fRegisterBeanLocal {
    
    /**
     * Method that builds a u2f registration response object and processes the 
     * same.
     * @param did       - FIDO domain id
     * @param protocol  - U2F protocol version to comply with.
     * @param regresponseJson  - U2F Reg Response parameters as a json string
     * @return          - FEreturn object with result
     * @throws SKFEException 
     *                          - In case of any error
     */
    FEreturn execute(String did, 
                    String protocol,
                    String regresponseJson) throws SKFEException;
}
