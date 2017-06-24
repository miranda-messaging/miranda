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

package com.ltsllc.miranda.test;

/**
 * Created by Clark on 2/24/2017.
 */

import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.common.util.Utils;

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
