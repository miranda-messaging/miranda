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
import com.ltsllc.miranda.ShutdownException;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.clientinterface.MirandaException;
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

    public void reset () throws MirandaException {
        super.reset();

        mirandaPanicPolicy = null;
    }

    @Before
    public void setup () throws MirandaException {
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
        ShutdownException shutdownException = null;

        try {
            getMirandaPanicPolicy().panic(startupPanic);
        } catch (ShutdownException e) {
            shutdownException = e;
        }

        assert (shutdownException != null);
    }

    @Test
    public void testPanicDoesNotUnderstand () {
        ShutdownException shutdownException = null;

        assert (getMirandaPanicPolicy().getPanicCount() == 0);

        Panic panic = new Panic ("a test", null, Panic.Reasons.DoesNotUnderstand);

        getMirandaPanicPolicy().panic(panic);

        assert (getMirandaPanicPolicy().getPanicCount() == 1);
        assert (shutdownException == null);
    }

    @Test
    public void testPanicDoesNotUnderstandNetworkMessage () {
        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstandNetworkMessage);

        int before = getMirandaPanicPolicy().getPanicCount();

        getMirandaPanicPolicy().panic(panic);

        assert (before == getMirandaPanicPolicy().getPanicCount());
    }

}
