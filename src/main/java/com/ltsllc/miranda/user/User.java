package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by Clark on 1/5/2017.
 */
public class User extends StatusObject implements Perishable, Serializable {
    private static Gson ourGson = new Gson();

    private String name;
    private String description;
    private PublicKey publicKey;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User (String name, String description) {
        super(Status.New);

        this.name = name;
        this.description = description;
    }

    public User (String name, String description, String base64) throws MirandaException  {
        super(Status.New);

        byte[] data = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        java.security.PublicKey publicKey = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(spec);
        } catch (GeneralSecurityException e) {
            throw new MirandaException("Exception trying to create public key", e);
        }

        this.name = name;
        this.description = description;
        this.publicKey = new PublicKey(publicKey);
    }

    public User (String name, String description, byte[] bytes) throws MirandaException {
        this(name, description, toPublicKey(bytes));
    }

    public User (String name, String description, PublicKey publicKey) {
        super(Status.New);

        this.name = name;
        this.description = description;
        this.publicKey = publicKey;
    }

    public User () {
        super(Status.New);
    }

    public boolean equals (Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof User))
            return false;

        User other = (User) o;

        return getName().equals(other.getName());
    }

    public String toJson() {
        return ourGson.toJson(this);
    }

    public static PublicKey toPublicKey (byte[] bytes) throws MirandaException {
        ObjectInputStream objectInputStream = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            java.security.PublicKey securityPublicKey = (java.security.PublicKey) objectInputStream.readObject();
            return new PublicKey(securityPublicKey);
        } catch (IOException | ClassNotFoundException e) {
            throw new MirandaException("Exception trying to deserialize public key", e);
        } finally {
            Utils.closeIgnoreExceptions(objectInputStream);
        }
    }

    public UserObject asUserObject () {
        UserObject userObject = new UserObject();

        userObject.setName(getName());
        userObject.setDescription(getDescription());

        byte[] data = Base64.getEncoder().encode(getPublicKey().getSecurityPublicKey().getEncoded());
        String encoded = new String(data);
        userObject.setPublicKey(encoded);

        return userObject;
    }

    public void rectify () throws MirandaException {
    }
}
