package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.user.User;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.ltsllc.miranda.StatusObject.Status.New;

/**
 * Created by Clark on 4/7/2017.
 */
public class UserObject extends com.ltsllc.miranda.StatusObject {
    private String name;
    private String description;
    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserObject () {
        super(New);
    }

    public UserObject (String name, String description, String publicKey) {
        super(Status.New);

        this.name = name;
        this.description = description;
        this.publicKey = publicKey;
    }


    public User asUser () throws MirandaException {
        try {
            byte[] bytes = Base64.getDecoder().decode(getPublicKey());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            java.security.PublicKey securityPublicKey = keyFactory.generatePublic(spec);

            PublicKey publicKey = new PublicKey(securityPublicKey);
            User user = new User(getName(), getDescription(), publicKey);

            user.setStatus(getStatus());

            return user;
        } catch (GeneralSecurityException e) {
            throw new MirandaException("Exception trying to convert user", e);
        }
    }
}
