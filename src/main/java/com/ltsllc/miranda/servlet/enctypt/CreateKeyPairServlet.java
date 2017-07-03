/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.servlet.enctypt;

import com.ltsllc.miranda.clientinterface.Results;
import com.ltsllc.miranda.clientinterface.objects.KeyPairObject;
import com.ltsllc.miranda.clientinterface.objects.KeyPairResultObject;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.openssl.PEMWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateKeyPairServlet extends MirandaServlet {
    public RSAPrivateCrtKeyParameters toBouncyCastle(KeyPair keyPair) {
        byte data[] = keyPair.getPrivate().getEncoded();
        RSAPrivateKey rsa = RSAPrivateKey.getInstance(data);
        return new RSAPrivateCrtKeyParameters(rsa.getModulus(), rsa.getPublicExponent(),
                rsa.getPrivateExponent(), rsa.getPrime1(), rsa.getPrime2(), rsa.getExponent1(),
                rsa.getExponent2(), rsa.getCoefficient());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        KeyPairResultObject keyPairResult = new KeyPairResultObject();
        keyPairResult.setResult(Results.Unknown);

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            KeyPairObject keyPairObject = new KeyPairObject();
            keyPairObject.setPrivateKey(keyPair.getPrivate());

            keyPairObject.setPublicKey(keyPair.getPublic());

            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(keyPair.getPrivate());
            pemWriter.close();
            keyPairResult.setPrivateKey(stringWriter.toString());

            stringWriter = new StringWriter();
            pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(keyPair.getPublic());
            pemWriter.close();
            keyPairResult.setPublicKey(stringWriter.toString());

            keyPairResult.setResult(Results.Success);
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Exception trying to generate key pair", e);
        }

        resp.setHeader("Access-Control-Allow-Origin", "*");
        respond(resp.getOutputStream(), keyPairResult);
        resp.setStatus(200);
    }
}
