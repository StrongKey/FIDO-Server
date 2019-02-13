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
import com.strongkey.auth.txbeans.authenticateLdapUserBean;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnitParamsRunner.class)
@PrepareForTest({skceCommon.class})
public class authenticateLdapUserBeanTest {

    private static InitialDirContext ctx = mock(InitialDirContext.class);
    private static LdapContext lc = mock(LdapContext.class);

    @InjectMocks
    private authenticateLdapUserBean authLdap = new authenticateLdapUserBean() {
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

    @AfterClass
    public static void tearDownClass() {
    }

    @BeforeClass
    public static void setDefaultMock() throws NamingException {
        when(ctx.lookup("cn=skceping,did=1,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com")).thenReturn(lc);
        when(ctx.lookup("cn=thisusershouldnotexistinopendj,did=1,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com")).thenThrow(new AuthenticationException());
        when(ctx.lookup("cn=correctuser,did=1,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com")).thenThrow(new AuthenticationException());
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
            new Object[]{"Null Did", null, "skceping", "Abcd1234!", SKCEException.class, "APPL-ERR-1003: NULL argument: did"},
            new Object[]{"Zero Did", 0L, "skceping", "Abcd1234!", SKCEException.class, "APPL-ERR-1002: Invalid argument: did"},
            new Object[]{"Greater than upper limit Did", Long.MAX_VALUE + 1, "skceping", "Abcd1234!", SKCEException.class, "APPL-ERR-1002: Invalid argument: did"},
            new Object[]{"Invalid Did", 200L, "skceping", "Abcd1234!", SKCEException.class, "APPL-ERR-1011: Inactive domain: 200"},
            //test username combinations
            new Object[]{"Null username", 1L, null, "Abcd1234!", SKCEException.class, "APPL-ERR-1003: NULL argument: username"},
            new Object[]{"Empty Username", 1L, "", "Abcd1234!", SKCEException.class, "APPL-ERR-1002: Invalid argument: username"},
            new Object[]{"Too Long Username", 1L, "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz", "Abcd1234!", SKCEException.class, "APPL-ERR-1005: Invalid argument - too long: username"},
            new Object[]{"Invalid username", 1L, "thisusershouldnotexistinopendj", "Abcd1234!", SKCEException.class, "SKCEWS-ERR-3055: Invalid user: thisusershouldnotexistinopendj"},
            //test password combinatios
            new Object[]{"Null password", 1L, "skceping", null, SKCEException.class, "APPL-ERR-1003: NULL argument: password"},
            new Object[]{"Empty password", 1L, "skceping", "", SKCEException.class, "APPL-ERR-1002: Invalid argument: password"},
            new Object[]{"Too Long password", 1L, "skceping", "Abcd1234!Abcd1234!Abcd1234!Abcd1234!Abcd1234!Abcd1234!Abcd1234!Abcd1234!", SKCEException.class, "APPL-ERR-1005: Invalid argument - too long: password"},
            new Object[]{"Invalid password", 1L, "correctuser", "thisusershouldnothavethispasswordinopendj", SKCEException.class, "SKCEWS-ERR-3055: Invalid user: correctuser"},
            
            //valid test
            new Object[]{"Valid", 1L, "skceping", "Abcd1234!", null, true}
        };
    }

    /**
     *
     * Test of execute method, of class authenticateLdapUserBean.
     *
     * @param testName
     * @param did
     * @param username
     * @param password
     * @param exceptionClass
     * @param result
     * @throws Exception
     */
    @Test
    @Parameters(method = "setValues")
    @TestCaseName("{0}")
    public void testExecute(String testName, Long did, String username, String password,
            Class exceptionClass, Object result) throws Exception {
        if (exceptionClass != null) {
            expectedEx.expect(exceptionClass);
            expectedEx.expectMessage((String) result);
        }
        boolean response = authLdap.execute(did, username, password);
        if (exceptionClass == null) {
            assertTrue(response);
        }
    }

    @Test
    public void testLdapDnPrefix() throws Exception {
        PowerMockito.when(skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.ldapdnprefix")).thenReturn("abc=");
        authLdap.execute(1L, "skceping", "Abcd1234!");
        verify(ctx).addToEnvironment("java.naming.security.principal", "abc=skceping,did=1,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com");
    }
    
    @Test
    public void testLdapDnSuffix() throws Exception {
        PowerMockito.when(skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.ldapdnsuffix")).thenReturn(",user=");
        authLdap.execute(1L, "skceping", "Abcd1234!");
        verify(ctx).addToEnvironment("java.naming.security.principal", "cn=skceping,did=1,user=");
    }
    
    @Test
    public void testLdapURL() throws Exception {
        PowerMockito.when(skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.ldapurl")).thenReturn("ldap://testuser:1389");
        authLdap.execute(1L, "skceping", "Abcd1234!");
        verify(ctx).addToEnvironment("java.naming.provider.url", "ldap://testuser:1389");
    }
    
    @Test
    public void testLdapSERVICE_OU_PREFIX() throws Exception {
        PowerMockito.when(skceCommon.getSERVICE_OU_PREFIX()).thenReturn(",ou=");
        authLdap.execute(1L, "skceping", "Abcd1234!");
        verify(ctx).addToEnvironment("java.naming.security.principal", "cn=skceping,ou=1,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com");
    }
}
