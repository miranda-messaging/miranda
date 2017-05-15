package com.ltsllc.miranda.servlet.user;

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
    private String category;
    private String description;
    private String publicKeyPem;

    public String getPublicKeyPem() {
        return publicKeyPem;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
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

    public UserObject() {
        super(New);
    }

    public UserObject(String name, String category, String description, String publicKeyPem) {
        super(Status.New);

        this.name = name;
        this.category = category;
        this.description = description;
        this.publicKeyPem = publicKeyPem;
    }

    public User asUser() {
        User.UserTypes category = User.UserTypes.valueOf(getCategory());
        User user = new User(getName(), category, getDescription(), getPublicKeyPem());

        user.setStatus(getStatus());

        return user;
    }

    public boolean isValid () {
        if (name != null)
            name = name.trim();

        if (name == null || name.equals(""))
            return false;

        if (category == null || category.equals("Nobody"))
            return false;

        if (description != null)
            description = description.trim();

        if (null != publicKeyPem)
            publicKeyPem = publicKeyPem.trim();

        if (null == publicKeyPem || publicKeyPem.equals(""))
            return false;

        return true;
    }
}
