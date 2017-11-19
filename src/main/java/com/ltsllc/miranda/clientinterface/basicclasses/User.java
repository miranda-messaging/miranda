/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.clcl.PublicKey;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.MirandaUncheckedException;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.objects.UserObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A user of the Miranda system.
 * <p>
 * <h3>Attributes</h3>
 * <table border="1">
 * <th>
 * <td>Name</td>
 * <td>Type</td>
 * <td>Description</td>
 * </th>
 * <tr>
 * <td>name</td>
 * <td>String</td>
 * <td>
 * The name of the user.
 * <p>
 * <p>
 * This must be unique across all Users in the sytem.
 * </p>
 * </td>
 * </tr>
 * <tr>
 * <td>category</td>
 * <td>enum</td>
 * <td>
 * The category of the user.
 * <p>
 * <p>
 * Allowable values are Publisher, Subscriber and Admin.
 * Unknown should not be used but is there for completeness.
 * </p>
 * <p>
 * <p>
 * Publishers can create topics and Events.
 * </p>
 * <p>
 * <p>
 * Subscribers can create Subscriptions.
 * </p>
 * <p>
 * <p>
 * Admins can do anything.
 * </p>
 * <p>
 * <p>
 * Users with a category of Unknown cannot do anything.
 * </p>
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>description</td>
 * <td>String</td>
 * <td>A user friendly description of the User.</td>
 * </tr>
 * <p>
 * <tr>
 * <td>publicKey</td>
 * <td>PublicKey</td>
 * <td>
 * The public key associated with the user.
 * <p>
 * <p>
 * The public key is used during logins to encrypt the session ID sent back to the user.
 * The user uses their private key in all their requests.
 * </p>
 * </td>
 * </tr>
 * </table>
 */
public class User extends MirandaObject {
    public enum UserTypes {
        Publisher,
        Subscriber,
        Admin,
        Unknown;

        public static boolean isValid(UserTypes type) {
            return type != Unknown;
        }
    }

    private String name;
    private UserTypes category;
    private String description;
    private String publicKeyPem;
    private PublicKey publicKey;

    @Override
    public void copyFrom(Mergeable mergeable) {
        User other = (User) mergeable;

        this.name = other.name;
        this.category = other.category;
        this.description = other.description;
        this.publicKey = new PublicKey(other.publicKey);
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof User))
            return false;

        User other = (User) o;

        return stringsAreEqual(name, other.name);
    }

    public String getPublicKeyPem() throws EncryptionException {
        if (null == publicKeyPem && null != publicKey)
            publicKeyPem = publicKey.toPem();

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

    public void setCategory(String categoryString) {
        UserTypes category = UserTypes.valueOf(categoryString);
        this.category = category;
    }

    public PublicKey getPublicKey() throws IOException {
        if (null == publicKey && null != publicKeyPem)
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public User(String name, UserTypes category, String description, String publicKeyPem) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.publicKeyPem = publicKeyPem;
    }

    public User(String name, String categoryString, String description, String publicKeyPem) {
        this.name = name;

        UserTypes category = UserTypes.valueOf(categoryString);
        this.category = category;

        this.description = description;
        this.publicKeyPem = publicKeyPem;
    }


    public User(String name, UserTypes category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public User(String name, UserTypes category, String description, PublicKey publicKey) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.publicKey = publicKey;
    }

    public void createPublicKey() throws IOException {
        java.security.PublicKey jaPublicKey = Utils.pemStringToPublicKey(publicKeyPem);
        publicKey = new PublicKey(jaPublicKey);
    }

    public User() {
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof User))
            return false;

        if (!(super.equals(o)))
            return false;

        User other = (User) o;

        if (!stringsAreEqual(getName(), other.getName()))
            return false;

        if (getCategory() != other.getCategory())
            return false;

        if (!stringsAreEqual(getDescription(), other.getDescription()))
            return false;

        return true;
    }


    public UserObject asUserObject() throws EncryptionException {
        UserObject userObject = new UserObject();

        userObject.setName(getName());
        userObject.setDescription(getDescription());
        userObject.setCategory(getCategory().toString());
        userObject.setPublicKeyPem(getPublicKeyPem());

        return userObject;
    }

    @Override
    public String toString() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("User {");
            stringBuilder.append(getName());
            stringBuilder.append(", ");
            stringBuilder.append(getCategory());
            stringBuilder.append(", ");
            stringBuilder.append(getDescription());
            stringBuilder.append(", ");
            stringBuilder.append(getPublicKey());
            stringBuilder.append("}");

            return stringBuilder.toString();
        } catch (IOException e) {
            throw new MirandaUncheckedException("Exception in toString", e);
        }
    }
}
