package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.PrivateKey;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.X509V3CertificateGenerator;
import org.junit.Test;
import sun.security.x509.*;

import javax.crypto.KeyGenerator;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Clark on 6/2/2017.
 */
public class TestCreateUser extends TestCase {
    public static final String USERS_FILE = "data/users.json";
    public static final String KEY_STORE_FILE = "keystore";
    public static final String KEY_STORE_PASSWORD = "whatever";
    public static final String PUBLIC_KEY_FILE = "public.json";
    public static final String PRIVATE_KEY_FILE = "private.json";

    public void createTextFile(String filename, String text) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(text);
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }


    public void createPrivateKeyFile(String filename, PrivateKey privateKey) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(privateKey.getSecurityPrivateKey());
        createTextFile(filename, json);
    }

    public void createPublicKeyFile (String filename, PublicKey publicKey) throws IOException {
        java.security.PublicKey jsPublicKey = publicKey.getSecurityPublicKey();
        Gson gson = new Gson();
        String json = gson.toJson(jsPublicKey);
        createTextFile(filename, json);
    }

    public static final String TEST_KEY_ALIAS = "private";
    public static final String TEST_KEY_PASSWORD = "whatever";
    public static final String TEST_KEYSTORE_FILENAME = "userKeyStore";

    public void createKeyStore (String filename, KeyPair keyPair) throws Exception {
        Certificate certificate = createCertificate(keyPair);
        Certificate[] chain = { certificate };

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.setKeyEntry(TEST_KEY_ALIAS, keyPair.getPrivate(), TEST_KEY_PASSWORD.toCharArray(), chain);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            keyStore.store(fileOutputStream, TEST_KEY_PASSWORD.toCharArray());
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public Certificate createCertificate (KeyPair pair) throws Exception {
            java.security.PrivateKey privkey = pair.getPrivate();
            X509CertInfo info = new X509CertInfo();
            Date from = new Date();
            Date to = new Date(from.getTime() + 365 * 86400000l);
            CertificateValidity interval = new CertificateValidity(from, to);
            BigInteger sn = new BigInteger(64, new SecureRandom());
            X500Name owner = new X500Name("cn=Tim Jones,o=ibm,c=us");

            info.set(X509CertInfo.VALIDITY, interval);
            info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
            info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
            info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
            info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
            info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
            info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

            String algorithm = "RSA";

            // Sign the cert to identify the algorithm that's used.
            X509CertImpl cert = new X509CertImpl(info);
            cert.sign(privkey, algorithm);

            // Update the algorith, and resign.
            algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
            info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
            cert = new X509CertImpl(info);
            cert.sign(privkey, algorithm);
            return cert;
    }

    @Test
    public void testCreateUser() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = new PublicKey(keyPair.getPublic());
        User user = new User("admin", User.UserTypes.Admin, "the admin user", publicKey);
        BootstrapUsersFile bootstrapUsersFile = new BootstrapUsersFile(USERS_FILE, KEY_STORE_FILE, KEY_STORE_PASSWORD);
        bootstrapUsersFile.create(user);
        bootstrapUsersFile.write();

        // createKeyStore(TEST_KEYSTORE_FILENAME, keyPair);
    }
}
