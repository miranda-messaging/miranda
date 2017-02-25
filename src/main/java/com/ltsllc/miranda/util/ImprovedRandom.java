package com.ltsllc.miranda.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Clark on 2/24/2017.
 */
public class ImprovedRandom {
    private Random random;

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
}
