package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.servlet.user.UserObject;
import com.ltsllc.miranda.util.Utils;

import java.io.*;

/**
 * Created by Clark on 1/5/2017.
 */
public class User extends StatusObject<User> implements Perishable, Serializable {
    public enum UserTypes {
        Publisher,
        Subscriber,
        Admin,
        Nobody
    }

    private static Gson ourGson = new Gson();

    private String name;
    private UserTypes category;
    private String description;
    private String publicKeyPem;
    private PublicKey publicKey;


    public String getPublicKeyPem() {
        if (null == publicKeyPem)
            createPublicKeyPem();

        return publicKeyPem;
    }

    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;

        if (this.publicKeyPem != null)
            publicKey = null;
    }

    public UserTypes getCategory() {
        return category;
    }

    public void setCategory(UserTypes category) {
        this.category = category;
    }

    public void setCategory (String categoryString) {
        UserTypes category = UserTypes.valueOf(categoryString);
        this.category = category;
    }

    public PublicKey getPublicKey() {
        if (null == publicKey)
            createPublicKey();

        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;

        if (this.publicKey != null)
            publicKeyPem = null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User (String name, String description) {
        super(Status.New);

        this.name = name;
        this.description = description;
    }

    public User (String name, UserTypes category, String description, String publicKeyPem) {
        super(Status.New);

        this.name = name;
        this.category = category;
        this.description = description;
        this.publicKeyPem = publicKeyPem;
    }

    public User (String name, String categoryString, String description, String publicKeyPem) {
        super(Status.New);

        this.name = name;

        UserTypes category = UserTypes.valueOf(categoryString);
        this.category = category;

        this.description = description;
        this.publicKeyPem = publicKeyPem;
    }


    public User (String name, UserTypes category, String description) {
        super(Status.New);

        this.name = name;
        this.category = category;
        this.description = description;
    }

    public User (String name, UserTypes category, String description, PublicKey publicKey) {
        super(Status.New);

        this.name = name;
        this.category = category;
        this.description = description;
        this.publicKey = publicKey;
    }

    public void createPublicKey () {
        java.security.PublicKey jaPublicKey = Utils.pemStringToPublicKey(publicKeyPem);
        publicKey = new PublicKey(jaPublicKey);
    }

    public void createPublicKeyPem () {
        publicKeyPem = Utils.publicKeyToPemString(publicKey.getSecurityPublicKey());
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
        userObject.setCategory(getCategory().toString());
        userObject.setPublicKeyPem(getPublicKeyPem());

        return userObject;
    }

    public void updateFrom (UserObject userObject) throws MirandaException {
        setPublicKeyPem(userObject.getPublicKeyPem());
        setCategory(userObject.getCategory());
        setDescription(userObject.getDescription());
    }

    public void updateFrom (User other) {
        super.updateFrom(other);

        setPublicKey(other.getPublicKey());
        setDescription(other.getDescription());
    }

    public boolean matches (User other) {
        if (!super.matches(other))
            return false;

        return getName().equals(other.getName());
    }
}
