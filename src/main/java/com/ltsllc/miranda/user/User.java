package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Created by Clark on 1/5/2017.
 */
public class User extends StatusObject implements Perishable, Serializable {
    private Gson ourGson = new Gson();

    private String name;
    private String description;
    private PublicKey publicKey;
    private String publicKeyString;

    public String getPublicKeyString() {
        return publicKeyString;
    }

    public void setPublicKeyString(String publicKeyString) {
        this.publicKeyString = publicKeyString;
    }

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

    public User (String name, String description, String hexString) throws MirandaException  {
        super(Status.New);

        byte[] data = null;
        try {
            data = Utils.hexStringToBytes(hexString);
        } catch (IOException e) {
            throw new MirandaException("Exception trying to decode string", e);
        }

        this.name = name;
        this.description = description;
        this.publicKey = toPublicKey(data);
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

    public void rectify () throws MirandaException {
        if (null != getPublicKeyString()) {
            byte[] bytes = null;
            try {
                bytes = Utils.hexStringToBytes(getPublicKeyString());
            } catch (IOException e) {
                throw new MirandaException("Excepetion trying to decode string", e);
            }
            PublicKey publicKey = toPublicKey(bytes);
            setPublicKey(publicKey);
            setPublicKeyString(null);
        }
    }
}
