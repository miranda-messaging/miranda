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

package com.ltsllc.miranda.util;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;

/**
 * Created by Clark on 3/25/2017.
 */
public class TestImprovedRandom extends TestCase {
    private ImprovedRandom improvedRandom;

    public ImprovedRandom getImprovedRandom() {
        return improvedRandom;
    }

    public void reset () {
        super.reset();

        improvedRandom = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        SecureRandom secureRandom = new SecureRandom();
        improvedRandom = new ImprovedRandom(secureRandom);
    }

    @Test
    public void testNextIndexLength () {
        int index = getImprovedRandom().nextIndex(11);

        assert (index < 11 && index >= 0);
    }

    @Test
    public void testNextIndexArray () {
        Integer[] testArray = new Integer[11];

        int index = getImprovedRandom().nextIndex(testArray);

        assert (index >= 0 && index < 11);
    }

    @Test
    public void testNextByte () {
        int i = getImprovedRandom().nextByte();

        assert (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE);
    }

    @Test
    public void testNextNonNegativeLong () {
        long value = 0;

        while (!getImprovedRandom().getWasNegative()) {
            value = getImprovedRandom().nextNonNegativeLong();
        }

        assert (value > 0);
    }
}
