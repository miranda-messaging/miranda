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

package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.user.User;

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
