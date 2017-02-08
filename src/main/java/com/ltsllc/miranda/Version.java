package com.ltsllc.miranda;

/**
 * Created by Clark on 2/6/2017.
 */

import com.ltsllc.miranda.file.SingleFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    private static Logger logger = Logger.getLogger(Version.class);

    private String sha1;
    private long lastChange;

    public Version (String sha1, long lastChange) {
        this.sha1 = sha1;
        this.lastChange = lastChange;
    }

    /**
     * When we are getting the version of an existing file.
     * @param singleFile
     */
    public Version (SingleFile singleFile) {
        FileInputStream fileInputStream = null;

        try {
            File file = new File(singleFile.getFilename());
            fileInputStream = new FileInputStream(file);
            byte[] sha1Bytes = Utils.calculateSha1(fileInputStream);

            this.sha1 = Utils.bytesToString(sha1Bytes);
            this.lastChange = file.lastModified();
        } catch (FileNotFoundException e) {
            logger.fatal ("Exception trying to determine version", e);
            System.exit(1);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }
    }

    public long getLastChange() {
        return lastChange;
    }

    public String getSha1() {
        return sha1;
    }

    public boolean differ (Version other) {
        return !sha1.equals(other.getSha1());
    }

    public boolean isMoreRecent (Version other) {
        return lastChange > other.getLastChange();
    }
}
