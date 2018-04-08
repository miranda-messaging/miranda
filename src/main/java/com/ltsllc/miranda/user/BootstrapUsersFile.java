package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.clcl.EncryptedMessage;
import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.clcl.PrivateKey;
import com.ltsllc.clcl.PublicKey;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.clientinterface.basicclasses.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
    private PrivateKey privateKey;
    private String filename;
    private List<User> userList;

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public BootstrapUsersFile(String usersFilename, String keyStoreFilename, String password) throws EncryptionException, GeneralSecurityException {
        initialize(usersFilename, keyStoreFilename, password);
    }

    public void initialize(String usersFilename, String keyStoreFilename, String password) throws GeneralSecurityException,EncryptionException {
        KeyStore keyStore = Utils.loadKeyStore(keyStoreFilename, password);

        java.security.PrivateKey jsPrivateKey = (java.security.PrivateKey) keyStore.getKey("private", password.toCharArray());
        PrivateKey privateKey = new PrivateKey(jsPrivateKey);
        setPrivateKey(privateKey);

        Certificate certificate = keyStore.getCertificate("private");
        java.security.PublicKey jsPublicKey = certificate.getPublicKey();
        PublicKey publicKey = new PublicKey(jsPublicKey);
        setPublicKey(publicKey);

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

    public void create(User user) {
        userList.add(user);
    }

    public void read() throws IOException, EncryptionException {
        File file = new File(getFilename());
        if (!file.exists())
            return;

        FileReader fileReader = null;

        try {
            Gson gson = new Gson();
            fileReader = new FileReader(getFilename());
            EncryptedMessage encryptedMessage = gson.fromJson(fileReader, EncryptedMessage.class);
            byte[] plainText = getPrivateKey().decryptFromMessage(encryptedMessage);
            String json = new String(plainText);
            Type t = new TypeToken<List<User>>() {
            }.getType();
            setUserList(gson.fromJson(json, t));
        } finally {
            Utils.closeIgnoreExceptions(fileReader);
        }
    }

    public void write() throws IOException, EncryptionException {
        FileWriter fileWriter = null;

        try {
            String json = gson.toJson(userList);
            fileWriter = new FileWriter(getFilename());
            EncryptedMessage encryptedMessage = getPublicKey().encryptToMessage(json.getBytes());
            json = gson.toJson(encryptedMessage);
            fileWriter.write(json);
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    public void createUser(String name, String description) throws GeneralSecurityException, IOException, EncryptionException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = new PublicKey(keyPair.getPublic());
        User user = new User(name, User.UserTypes.Admin, description, publicKey);
        String publicKeyFilename = name + ".public.pem.txt";
        String privateKeyFilename = name + ".private.pem.txt";
        PublicKey.writePemFile(publicKeyFilename, keyPair.getPublic());
        PrivateKey.writePemFile(privateKeyFilename, keyPair.getPrivate());
        userList.add(user);
    }
}
