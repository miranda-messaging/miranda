/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.clcl;

import com.ltsllc.clcl.test.EncryptionTestCase;
import org.junit.Before;
import org.junit.Test;

public class TestLDAPName extends EncryptionTestCase {
    private LDAPName ldapName;

    public LDAPName getLdapName() {
        return ldapName;
    }

    public void setLdapName(LDAPName ldapName) {
        this.ldapName = ldapName;
    }

    @Before
    public void setup () {
        this.ldapName = new LDAPName("cn=John Doe");
    }

    @Test
    public void testEquals () {
        LDAPName other = new LDAPName("cn=John Doe");
        LDAPName different = new LDAPName("c=US");
        assert (getLdapName().equals(other));
        assert (!getLdapName().equals(different));
    }
}
