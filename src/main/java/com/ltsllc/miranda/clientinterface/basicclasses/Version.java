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

/**
 * Created by Clark on 2/6/2017.
 */


import com.google.gson.Gson;
import com.ltsllc.clcl.MessageDigest;
import com.ltsllc.miranda.clientinterface.MirandaException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * This represents a version of a file.  It is used to determine if something
 * is out of date and needs to be updated.
 * <p>
 * <p>
 * If the sha1 values do not match then the two objects are not equal ---
 * The lastChange determines which object is out of date.
 * </P>
 */
public class Version {
    private String sha256;
    private long timeOfLastUpdate;

    public static Gson getGson() {
        return gson;
    }

    private static Gson gson = new Gson();

    public Version() {
    }

    public static Version fromString (String json) {
        Version version = getGson().fromJson(json, Version.class);
        return version;
    }

    public long getTimeOfLastUpdate() {
        return timeOfLastUpdate;
    }

    public void setTimeOfLastUpdate(long timeOfLastUpdate) {
        this.timeOfLastUpdate = timeOfLastUpdate;
    }

    public Version(String content, long timeOfLastUpdate) throws GeneralSecurityException {
        setSha256(MessageDigest.calculate(content.getBytes()));
        setTimeOfLastUpdate(timeOfLastUpdate);
    }

    public Version(byte[] data, long timeOfLastUpdate) throws GeneralSecurityException {
        this.sha256 = MessageDigest.calculate(data);
        setTimeOfLastUpdate(timeOfLastUpdate);
    }

    public static Version createWithSha256(String sha256) {
        Version version = new Version();
        version.sha256 = sha256;

        return version;
    }

    public Version(byte[] data) {
        String string = new String(data);
    }

    public static Version fromJson (String jason) {
        return getGson().fromJson(jason, Version.class);
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public boolean before (Version version) {
        if (version.getSha256().equals(getSha256())) {
            return false;
        }

        return getTimeOfLastUpdate() <  version.getTimeOfLastUpdate();
    }

    public static char[] buffer = new char[8192];

    public static Version fromFile (File file) throws IOException, MirandaException, GeneralSecurityException {
        FileReader fileReader = new FileReader(file);
        fileReader.read(buffer);
        String s = new String(buffer);
        long l = System.currentTimeMillis();
        return new Version(s, l);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof Version))
            return false;

        Version other = (Version) o;

        if ((other.sha256 == null && sha256 != null) || (null != other.getSha256() && null == sha256))
            return false;

        return sha256.equals(other.getSha256());
    }

    public boolean isAfter(Version version) {
        return version.getTimeOfLastUpdate() > getTimeOfLastUpdate();
    }

    public boolean isNewer (Version version) {
        return version.getTimeOfLastUpdate() > version.getTimeOfLastUpdate();
    }

}
