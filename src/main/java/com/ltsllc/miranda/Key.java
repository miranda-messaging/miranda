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

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/2/2017.
 */
abstract public class Key implements Serializable {
    abstract EncryptedMessage encrypt (byte[] clearText) throws GeneralSecurityException;
    abstract byte[] decrypt (EncryptedMessage encryptedMessage) throws GeneralSecurityException, IOException;

    public byte[][] toBlocks (byte[] buffer, int blockSize) {
        int numBlocks = calculateNumberOfBlocks(buffer.length, blockSize);

        byte blocks[][] = new byte[numBlocks][];

        for (int i = 0; i < numBlocks; i++) {
            blocks[i] = new byte[blockSize];
        }

        for (int i = 0; i < numBlocks; i++) {
            copyBlock(i, blockSize, buffer, blocks[i]);
        }

        copyToBlocks (buffer, blocks, blockSize);

        return blocks;
    }


    public void copyBlock (int blockIndex, int blockSize, byte[] source, byte[] destination) {
        int offset = (blockIndex * blockSize);

        for (int i = 0; i < blockSize; i++) {
            int index = i + offset;
            if (index < source.length) {
                destination[i] = source[index];
            } else {
                destination[i] = 0;
            }
        }
    }

    public int calculateNumberOfBlocks (int totalSize, int blockSize) {
        int numberOfBlocks = totalSize / blockSize;
        if (numberOfBlocks < 1) {
            numberOfBlocks = 1;
        }

        return numberOfBlocks;
    }

    public byte[] toSingleBuffer (byte[][] source) {
        int totalSize = 0;
        for (int i = 0; i < source.length; i++) {
            totalSize += source[i].length;
        }

        byte[] result = new byte[totalSize];

        int offset = 0;

        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                result[j + offset] = source[i][j];
            }

            offset += source[i].length;
        }

        return result;
    }

    public void copyToBlocks (byte[] source, byte[][] destination, int blockSize) {
        int numberOfBlocks = calculateNumberOfBlocks(source.length, blockSize);

        for (int i = 0; i < numberOfBlocks; i++) {
            copyBlock(i, blockSize, source, destination[i]);
        }
    }
}
