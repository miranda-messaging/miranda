/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.common.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Clark on 2/24/2017.
 */
public class ImprovedRandom {
    private boolean wasNegative;
    private Random random;

    public Random getRandom() {
        return random;
    }

    public boolean getWasNegative () {
        return wasNegative;
    }

    public void setWasNegative(boolean wasNegative) {
        this.wasNegative = wasNegative;
    }

    public ImprovedRandom () {
        this.random = new SecureRandom();
    }

    public ImprovedRandom (Random random) {
        this.random = random;
    }

    public int nextIndex(int length) {
        int index = random.nextInt(length);

        if (index < 0)
            index = -1 * index;

        index = index % length;

        return index;
    }

    public int nextIndex(Object[] array) {
        return nextIndex(array.length);
    }

    public void nextBytes(byte[] array) {
        random.nextBytes(array);
    }

    public int nextInt (int atLeastAndIncluding, int upToAndIncluding)
    {
        int interval = upToAndIncluding - atLeastAndIncluding + 1;
        if (interval < 0) {
            throw new IllegalArgumentException("negative interval");
        }

        return atLeastAndIncluding + random.nextInt(interval);
    }

    public byte nextByte () {
        byte[] buffer = new byte[1];
        random.nextBytes(buffer);
        return buffer[0];
    }

    public int nextInt (int lessThan) {
        return random.nextInt(lessThan);
    }

    public long nextNonNegativeLong () {
        setWasNegative(false);
        long value = getRandom().nextLong();

        if (value < 0) {
            value = -1 * value;
            setWasNegative(true);
        }

        return value;
    }
}
