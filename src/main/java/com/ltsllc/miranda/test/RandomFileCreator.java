package com.ltsllc.miranda.test;

/**
 * Created by Clark on 2/24/2017.
 */

import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.util.ImprovedRandom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A {@link FileCreator} that puts random junk into a file
 */
public class RandomFileCreator implements FileCreator {
    private int size;
    private ImprovedRandom random;

    public RandomFileCreator(int size, ImprovedRandom random) {
        this.size = size;
        this.random = random;
    }

    public boolean createFile(File file) {
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[size];
        random.nextBytes(buffer);

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }

        return true;
    }
}
