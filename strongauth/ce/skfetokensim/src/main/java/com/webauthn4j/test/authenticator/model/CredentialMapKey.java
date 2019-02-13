/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webauthn4j.test.authenticator.model;

import java.util.Arrays;
import java.util.Objects;

public class CredentialMapKey {

    private String rpId;
    private byte[] userHandle;

    public CredentialMapKey(String rpId, byte[] userHandle) {
        this.rpId = rpId;
        this.userHandle = userHandle;
    }

    public String getRpId() {
        return rpId;
    }

    public byte[] getUserHandle() {
        return userHandle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialMapKey that = (CredentialMapKey) o;
        return Objects.equals(rpId, that.rpId) &&
                Arrays.equals(userHandle, that.userHandle);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(rpId);
        result = 31 * result + Arrays.hashCode(userHandle);
        return result;
    }
}
