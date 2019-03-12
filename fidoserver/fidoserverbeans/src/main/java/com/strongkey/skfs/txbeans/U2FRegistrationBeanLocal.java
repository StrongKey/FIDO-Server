/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.txbeans;

import javax.ejb.Local;

@Local
public interface U2FRegistrationBeanLocal {

    public String execute(Long did, String registrationresponse, String registrationmetadata, String protocol);
}
