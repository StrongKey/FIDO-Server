/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skce.utilities;

/**
 * This class represents the exception thrown due to application-level errors.
 */
public class SKCEException extends Exception{
    
    /**
     * Different types of constructors
     */
    public SKCEException() {
        super();
    }

    public SKCEException(String message) {
        super(message);
    }

    public SKCEException(Exception e) {
        super(e);
    }
}
