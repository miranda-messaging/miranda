package com.ltsllc.miranda.util;

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
