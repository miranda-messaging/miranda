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

package com.ltsllc.miranda.event;

import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * Created by Clark on 2/19/2017.
 */

public class TestEvent extends TestCase {
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void reset () {
        this.event = null;
    }

    @Before
    public void setup () {
        try {
            reset();

            super.setup();

            this.event = new Event(Event.Methods.POST, "010203");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructors () throws IOException {
        String junk = "010203";
        assert (getEvent().getContentAsHexString().equals(junk));
        assert (getEvent().getMethod() == Event.Methods.POST);

        Event event = new Event(Event.Methods.PUT, junk);

        assert (event.getMethod() == Event.Methods.PUT);

        byte[] whatever = Utils.hexStringToBytes(junk);
        assert (arraysAreEquivalent(event.getContent(), whatever));
    }

    @Test
    public void testUpdateFrom () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        Event event = Event.createRandom(improvedRandom, 1024);
        IllegalStateException illegalStateException = null;

        try {
            event.updateFrom(event);
        } catch (IllegalStateException e) {
            illegalStateException = e;
        }

        assert (illegalStateException != null);
    }

    @Test
    public void testMatch () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        Event event = Event.createRandom(improvedRandom, 1024);
        Event other = Event.createRandom(improvedRandom, 1024);

        assert (event.matches(event));
        assert (!event.matches(other));
    }
}
