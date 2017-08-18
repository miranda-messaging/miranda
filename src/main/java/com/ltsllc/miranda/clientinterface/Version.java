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

package com.ltsllc.miranda.clientinterface;

/**
 * Created by Clark on 2/6/2017.
 */

import com.ltsllc.common.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * This represents a version of a file.  It is used to determine if something
 * is out of date and needs to be updated.
 *
 * <P>
 *     If the sha1 values do not match then the two objects are not equal ---
 *     The lastChange determines which object is out of date.
 * </P>
 */
public class Version  {
    private String sha1;

    public Version () {}

    public Version (String content) throws NoSuchAlgorithmException {
        this.sha1 = Utils.calculateSha1(content);
    }

    public Version (byte[] data) throws NoSuchAlgorithmException {
        this.sha1 = Utils.calculateSha1(data);
    }

    public static Version createWithSha1 (String sha1) {
        Version version = new Version ();
        version.sha1 = sha1;

        return version;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1 (String sha1) {
        this.sha1 = sha1;
    }

    public boolean equals (Object o) {
        if (null == o || !(o instanceof Version))
            return false;

        Version other = (Version) o;

        if ((other.sha1 == null && sha1 != null) || (null != other.getSha1() && null == sha1))
            return false;

        return sha1.equals(other.getSha1());
    }

}
