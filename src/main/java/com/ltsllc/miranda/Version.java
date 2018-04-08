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

package com.ltsllc.miranda;

/**
 * Created by Clark on 2/6/2017.
 */

import com.ltsllc.clcl.MessageDigest;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.file.SingleFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

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
    private static Logger logger = Logger.getLogger(Version.class);

    private String sha256;

    public Version() {
    }

    public Version(String content) throws GeneralSecurityException {
        this.sha256 = MessageDigest.calculate(content.getBytes());
    }

    public Version(byte[] data) throws GeneralSecurityException {
        this.sha256 = MessageDigest.calculate(data);
    }

    public static Version createWithSha256(String sha256) {
        Version version = new Version();
        version.sha256 = sha256;

        return version;
    }

    /**
     * When we are getting the version of an existing file.
     *
     * @param singleFile
     */
    public Version(SingleFile singleFile) throws GeneralSecurityException, IOException {
        FileInputStream fileInputStream = null;

        try {
            File file = new File(singleFile.getFilename());
            fileInputStream = new FileInputStream(file);
            sha256 = MessageDigest.calculate(fileInputStream);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (null == o || !(o instanceof Version))
            return false;

        Version other = (Version) o;

        if (sha256 == other.sha256)
            return true;

        if (sha256 == null || other.sha256 == null)
            return false;

        return sha256.equals(other.getSha256());
    }

}
