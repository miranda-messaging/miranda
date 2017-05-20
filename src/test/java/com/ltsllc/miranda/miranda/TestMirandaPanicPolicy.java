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

package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestMirandaPanicPolicy extends TestCase {
    private MirandaPanicPolicy mirandaPanicPolicy;

    public MirandaPanicPolicy getMirandaPanicPolicy() {
        return mirandaPanicPolicy;
    }

    public void reset () {
        super.reset();

        mirandaPanicPolicy = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupMockMiranda();
        setupMockTimer();
        mirandaPanicPolicy = new MirandaPanicPolicy(10, 10000, getMockMiranda(), getMockTimer());
        mirandaPanicPolicy.setTestMode(true);
    }

    @Test
    public void testPanicStartupPanic () {
        StartupPanic startupPanic = new StartupPanic("a test", null, StartupPanic.StartupReasons.Test);

        boolean result = getMirandaPanicPolicy().panic(startupPanic);

        assert (result);
    }

    @Test
    public void testPanicDoesNotUnderstand () {
        assert (getMirandaPanicPolicy().getPanicCount() == 0);

        Panic panic = new Panic ("a test", null, Panic.Reasons.DoesNotUnderstand);

        boolean result = getMirandaPanicPolicy().panic(panic);

        assert (!result);
        assert (getMirandaPanicPolicy().getPanicCount() == 1);
    }

    @Test
    public void testPanicDoesNotUnderstandNetworkMessage () {
        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstandNetworkMessage);

        int before = getMirandaPanicPolicy().getPanicCount();

        boolean result = getMirandaPanicPolicy().panic(panic);

        assert (!result);
        assert (before == getMirandaPanicPolicy().getPanicCount());
    }

}
