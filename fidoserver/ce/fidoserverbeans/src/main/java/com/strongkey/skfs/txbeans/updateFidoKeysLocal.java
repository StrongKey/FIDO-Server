/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.txbeans;

import javax.ejb.Local;

@Local
public interface updateFidoKeysLocal {

    public String execute(Short sid,Long did,
            String username,Long fkid,
            Integer newCounter,
            String modify_location);
}
