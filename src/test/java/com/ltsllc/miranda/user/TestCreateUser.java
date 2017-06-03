package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.PrivateKey;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

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

        createPublicKeyFile(PUBLIC_KEY_FILE, publicKey);
        PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
        createPrivateKeyFile(PRIVATE_KEY_FILE, privateKey);

    }
}
