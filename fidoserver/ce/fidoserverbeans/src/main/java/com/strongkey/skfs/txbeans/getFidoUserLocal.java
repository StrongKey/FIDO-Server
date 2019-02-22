/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.txbeans;

import com.strongkey.skfs.entitybeans.FidoUsers;
import com.strongkey.skfs.utilities.SKFEException;
import javax.ejb.Local;

/**
 *
 * @author pmarathe
 */
@Local
public interface getFidoUserLocal {
    FidoUsers GetByUsername(Long did, String username) throws SKFEException;
}
