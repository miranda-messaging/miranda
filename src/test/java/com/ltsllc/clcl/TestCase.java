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

import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;

/**
 * A con
 */
public class TestCase {
    public static Certificate createCertificate () throws Exception {
        KeyPair keyPair = KeyPair.createInstance();
        DistinguishedName distinguishedName = new DistinguishedName("c=US,st=Colorado,l=Denver,cn=Whatever,o=Long Term Software,ou=Devlopment");
        keyPair.getPublicKey().setDn(distinguishedName);
        keyPair.getPrivateKey().setDn(distinguishedName);
        CertificateSigningRequest certificateSigningRequest = keyPair.getPublicKey().createCertificateSigningRequest(keyPair.getPrivateKey());
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date aYearFromNow = calendar.getTime();
        return keyPair.getPrivateKey().sign(certificateSigningRequest, now, aYearFromNow);
    }

    public static void delete (String filename) {
    }

    public DistinguishedName createDn () {
        return new DistinguishedName("c=US,st=Colorado,l=Denver,cn=Whatever,o=Long Term Software,ou=Devlopment");
    }
}
