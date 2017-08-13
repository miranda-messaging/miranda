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

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JCERSAPublicKey;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Signature;

public class CertificateSigningRequest {
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";

    private PKCS10 pkcs10;

    public CertificateSigningRequest(PublicKey publicKey, PrivateKey privateKey) throws EncryptionException {
        initialize(publicKey, privateKey);
    }

    public CertificateSigningRequest (PKCS10 pkcs10) {
        this.pkcs10 = pkcs10;
    }

    public void initialize(PublicKey publicKey, PrivateKey privateKey) throws EncryptionException {
        try {
            X500Name x500Name = new X500Name(publicKey.getDn().toString());

            String signatureAlgorithmName = "SHA1WithRSA";
            Signature signature = Signature.getInstance(signatureAlgorithmName);
            signature.initSign(privateKey.getSecurityPrivateKey());

            this.pkcs10 = new PKCS10(publicKey.getSecurityPublicKey());
            this.pkcs10.encodeAndSign(x500Name, signature);
        } catch (IOException | GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to initialize", e);
        }
    }

    public PKCS10 getPkcs10() {
        return pkcs10;
    }

    public String toPem() throws EncryptionException {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            byte[] bytes = getPkcs10().getEncoded();
            PemObject pemObject = new PemObject("CERTIFICATE REQUEST", bytes);

            pemWriter.writeObject(pemObject);
            pemWriter.close();

            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to convert a CSR to a pem", e);
        }
    }

    public DistinguishedName getSubjectDn() {
        DistinguishedName dn = new DistinguishedName(getPkcs10().getSubjectName());
        return dn;
    }

    public static CertificateSigningRequest fromPem(String pem) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser pemParser = new PEMParser(stringReader);
            PKCS10CertificationRequest pkcs10CertificationRequest = (PKCS10CertificationRequest) pemParser.readObject();
            byte[] encoded = pkcs10CertificationRequest.getEncoded();
            PKCS10 pkcs10 = new PKCS10(encoded);
            return new CertificateSigningRequest(pkcs10);
        } catch (IOException|GeneralSecurityException e) {
            throw new EncryptionException("Exception tryin to read from a PEM", e);
        }
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof CertificateSigningRequest))
            return false;

        CertificateSigningRequest other = (CertificateSigningRequest) o;
        return getPkcs10().equals(other.getPkcs10());
    }
}
