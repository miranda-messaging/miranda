package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.EncryptedMessage;
import com.ltsllc.miranda.PrivateKey;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.util.Utils;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 6/2/2017.
 */
public class BootstrapUsersFile {
    private static Gson gson = new Gson();

    private PublicKey publicKey;
    private String filename;
    private List<User> userList;


    public BootstrapUsersFile (String usersFilename, String keyStoreFilename, String password) throws IOException, GeneralSecurityException {
        initialize(usersFilename, keyStoreFilename, password);
    }

    public void initialize (String usersFilename, String keyStoreFilename, String password) throws IOException, GeneralSecurityException {
        KeyStore keyStore = Utils.loadKeyStore(keyStoreFilename, password);
        Certificate certificate = keyStore.getCertificate("private");
        java.security.PublicKey jsPublicKey = certificate.getPublicKey();
        PublicKey publicKey = new PublicKey(jsPublicKey);
        setPublicKey (publicKey);
        this.filename = usersFilename;
        this.userList = new ArrayList<User>();
    }

    public String getFilename() {
        return filename;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void create (User user) {
        userList.add(user);
    }

    public void write () throws IOException, GeneralSecurityException {
        FileWriter fileWriter = null;

        try {
            String json = gson.toJson(userList);
            fileWriter = new FileWriter(getFilename());
            EncryptedMessage encryptedMessage = getPublicKey().encrypt(json.getBytes());
            json = gson.toJson(encryptedMessage);
            fileWriter.write(json);
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }
}
