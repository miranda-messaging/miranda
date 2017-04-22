package com.ltsllc.miranda.servlet.enctypt;

import com.ltsllc.miranda.servlet.objects.KeyPairObject;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyPairGeneratorSpi;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateKeyPairServlet extends HttpServlet {

    public RSAPrivateCrtKeyParameters toBouncyCastle(KeyPair keyPair) {
        byte data[] = keyPair.getPrivate().getEncoded();
        RSAPrivateKey rsa = RSAPrivateKey.getInstance(data);
        return new RSAPrivateCrtKeyParameters(rsa.getModulus(), rsa.getPublicExponent(),
                rsa.getPrivateExponent(), rsa.getPrime1(), rsa.getPrime2(), rsa.getExponent1(),
                rsa.getExponent2(), rsa.getCoefficient());
    }


    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            KeyPairObject keyPairObject = new KeyPairObject();
            keyPairObject.setPrivateKey(keyPair.getPrivate());

            keyPairObject.setPublicKey(keyPair.getPublic());

            StringWriter stringWriter = new StringWriter();
            // RSAPrivateCrtKeyParameters rsaPrivateCrtKeyParameters = toBouncyCastle(keyPair);
            JcaMiscPEMGenerator jcaMiscPEMGenerator = new JcaMiscPEMGenerator(keyPair);
            PemObject pemObject = jcaMiscPEMGenerator.generate();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(pemObject);


            String json = keyPairObject.asJson();
            resp.getOutputStream().println(json);
            resp.setStatus(200);
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Exception trying to generate key pair", e);
        }
    }
}
