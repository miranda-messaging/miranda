package com.ltsllc.miranda.servlet.enctypt;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.servlet.objects.KeyPairObject;
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
        KeyPairResult keyPairResult = new KeyPairResult();
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

        respond(resp.getOutputStream(), keyPairResult);
        resp.setStatus(200);
    }
}
