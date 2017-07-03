package com.ltsllc.miranda.user;


import com.google.gson.Gson;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.clientinterface.basicclasses.PrivateKey;
import com.ltsllc.miranda.clientinterface.basicclasses.PublicKey;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.test.TestCase;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Clark on 6/2/2017.
 */
public class TestCreateUser extends TestCase {
    public static final String USERS_FILE = "data/users.json";
    public static final String KEY_STORE_FILE = "keystore";
    public static final String KEY_STORE_PASSWORD = "whatever";

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

    public void createPublicKeyFile(String filename, PublicKey publicKey) throws IOException {
        java.security.PublicKey jsPublicKey = publicKey.getSecurityPublicKey();
        Gson gson = new Gson();
        String json = gson.toJson(jsPublicKey);
        createTextFile(filename, json);
    }

    public static final String TEST_KEY_ALIAS = "private";
    public static final String TEST_KEY_PASSWORD = "whatever";
    public static final String TEST_KEYSTORE_FILENAME = "userKeyStore";

    public void createKeyStore(String filename, KeyPair keyPair) throws Exception {
        java.security.cert.Certificate certificate = generateCertificate(keyPair);
        java.security.cert.Certificate[] chain = { certificate };

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry(TEST_KEY_ALIAS, keyPair.getPrivate(), TEST_KEY_PASSWORD.toCharArray(), chain);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            keyStore.store(fileOutputStream, TEST_KEY_PASSWORD.toCharArray());
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public static java.security.cert.Certificate generateCertificate(KeyPair keyPair){
        try {
            Provider BC = new org.bouncycastle.jce.provider.BouncyCastleProvider();
            Security.addProvider(BC);
            /*
            KeyPairGenerator kpGen=KeyPairGenerator.getInstance("RSA","BC");
            kpGen.initialize(1024,new SecureRandom());
            KeyPair pair=kpGen.generateKeyPair();
            */
            X500NameBuilder builder=new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.OU, "Unknown");
            builder.addRDN(BCStyle.O, "Unknown");
            builder.addRDN(BCStyle.CN, "Unknown");

            Date startDate = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            Date endDate = calendar.getTime();

            BigInteger serial=BigInteger.valueOf(System.currentTimeMillis());
            // X509v3CertificateBuilder certGen=new JcaX509v3CertificateBuilder(builder.build(),serial,notBefore,notAfter,builder.build(),pair.getPublic());
            X509v3CertificateBuilder certGen=new JcaX509v3CertificateBuilder(builder.build(),serial,startDate,endDate,builder.build(),keyPair.getPublic());
            ContentSigner sigGen=new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(BC).build(keyPair.getPrivate());
            X509Certificate cert=new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));
            cert.checkValidity(new Date());
            cert.verify(cert.getPublicKey());

            return cert;
        }
        catch (  Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Failed to generate self-signed certificate!",t);
        }
    }


    
    public void testCreateUser() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = new PublicKey(keyPair.getPublic());
        User user = new User("admin", User.UserTypes.Admin, "the admin user", publicKey);
        BootstrapUsersFile bootstrapUsersFile = new BootstrapUsersFile(USERS_FILE, KEY_STORE_FILE, KEY_STORE_PASSWORD);
        bootstrapUsersFile.create(user);
        bootstrapUsersFile.write();

        createKeyStore(TEST_KEYSTORE_FILENAME, keyPair);
    }

    
    public void load () {
        Exception exception = null;

        try {
            KeyStore keyStore = Utils.loadKeyStore("userKeyStore", "whatever");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert(null == exception);
    }
}
