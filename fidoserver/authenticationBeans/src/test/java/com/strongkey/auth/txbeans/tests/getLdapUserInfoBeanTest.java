/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License, as published by the Free Software Foundation and
 * available at http://www.fsf.org/licensing/licenses/lgpl.html,
 * version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date: $
 * $Revision: $
 * $Author: $
 * $URL: $
 *
 * *********************************************
 *                    888
 *                    888
 *                    888
 *  88888b.   .d88b.  888888  .d88b.  .d8888b
 *  888 "88b d88""88b 888    d8P  Y8b 88K
 *  888  888 888  888 888    88888888 "Y8888b.
 *  888  888 Y88..88P Y88b.  Y8b.          X88
 *  888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 *
 */

package com.strongkey.auth.txbeans.tests;

import com.strongauth.appliance.entitybeans.Domains;
import com.strongauth.appliance.utilities.applianceMaps;
import com.strongauth.skce.utilities.SKCEException;
import com.strongauth.skce.utilities.skceCommon;
import com.strongkey.auth.txbeans.getLdapUserInfoBean;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnitParamsRunner.class)
@PrepareForTest({skceCommon.class})
public class getLdapUserInfoBeanTest {

    private static InitialDirContext ctx = mock(InitialDirContext.class);
    private static LdapContext lc = mock(LdapContext.class);
    
    @InjectMocks
    private getLdapUserInfoBean getLdapUserInfo = new getLdapUserInfoBean() {
        @Override
        protected Context getcontext(String module, String ldapurl, String principal, String password) throws NamingException {
            ctx.addToEnvironment("java.naming.provider.url", ldapurl);
            ctx.addToEnvironment("java.naming.security.principal", principal);
            ctx.addToEnvironment("java.naming.security.credentials", password);
            return ctx;
        }
    };

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
        Domains domain = new Domains();
        domain.setDid(1L);
        domain.setName("SAKA Domain");
        domain.setStatus("Active");
        applianceMaps.putDomain(1L, domain);
    }

    @BeforeClass
    public static void setDefaultMock() throws NamingException {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        PowerMockito.spy(skceCommon.class);
    }

    @After
    public void tearDown() {
    }
    
     private static final Object[] setValues() {
        return new Object[]{
            //test did combinations
            new Object[]{"Null Did", null, "dc=strongauth,dc=com", "cn", "skceping", SKCEException.class, "APPL-ERR-1003: NULL argument: did"},
            new Object[]{"Zero Did", 0L, "dc=strongauth,dc=com", "cn", "skceping", SKCEException.class, "APPL-ERR-1002: Invalid argument: did"},
            new Object[]{"Greater than upper limit Did", Long.MAX_VALUE + 1, "dc=strongauth,dc=com", "cn", "skceping", SKCEException.class, "APPL-ERR-1002: Invalid argument: did"},
            new Object[]{"Invalid Did", 200L, "dc=strongauth,dc=com", "cn", "skceping", SKCEException.class, "APPL-ERR-1011: Inactive domain: 200"},
            
            //valid test
//            new Object[]{"Valid", 1L, "skceping", "Abcd1234!", skceConstants.LDAP_ROLE_ADM, null, true}
        };
    }

    @Test
    @Parameters(method = "setValues")
    @TestCaseName("{0}")
    public void testExecute(String testName, Long did, String basedn, String searchkey, String searchvalue,
            Class exceptionClass, Object result) throws Exception {
        if (exceptionClass != null) {
            expectedEx.expect(exceptionClass);
            expectedEx.expectMessage((String) result);
        }
        getLdapUserInfo.execute(did, basedn, searchkey, searchvalue);
        if (exceptionClass == null) {
           
        }
    }

}