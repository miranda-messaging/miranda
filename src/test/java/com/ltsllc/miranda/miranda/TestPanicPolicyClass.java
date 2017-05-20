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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestPanicPolicyClass extends TestCase {
    private PanicPolicyClass panicPolicyClass;

    public PanicPolicyClass getPanicPolicyClass() {
        return panicPolicyClass;
    }

    public void reset () {
        super.reset();

        panicPolicyClass = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setupMockMiranda();
        setuplog4j();
        panicPolicyClass = new MirandaPanicPolicy(10, 10000, getMockMiranda(), getMockTimer());
        panicPolicyClass.setTestMode(true);
    }

    @Test
    public void testDecrementPanicCount () {
        getPanicPolicyClass().setPanicCount(5);

        getPanicPolicyClass().decrementPanicCount();

        assert (getPanicPolicyClass().getPanicCount() == 4);

        getPanicPolicyClass().setPanicCount(0);

        getPanicPolicyClass().decrementPanicCount();

        assert (getPanicPolicyClass().getPanicCount() == 0);
    }

    @Test
    public void testStart () {
        getPanicPolicyClass().start();

        verify(getMockTimer(), atLeastOnce()).sendSchedulePeriodic(Matchers.anyLong(), Matchers.any(BlockingQueue.class),
                Matchers.any(Message.class));
    }

    @Test
    public void testHandleCountablePanicBelowLimit () {
        getPanicPolicyClass().setMaxPanicCount(10);

        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstand);

        getPanicPolicyClass().handleCountablePanic(panic);

        verify(getMockMiranda(), never()).shutdown();
    }

    @Test
    public void testHandleCountableAtLimit () {
        getPanicPolicyClass().setMaxPanicCount(1);

        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstand);

        getPanicPolicyClass().handleCountablePanic(panic);

        verify(getMockMiranda(), atLeastOnce()).shutdown();
    }

    @Test
    public void testHandleCountableAboveLimit () {
        getPanicPolicyClass().setMaxPanicCount(1);
        getPanicPolicyClass().setPanicCount(1);

        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstand);

        getPanicPolicyClass().handleCountablePanic(panic);

        verify(getMockMiranda(), atLeastOnce()).shutdown();
    }

    @Test
    public void testHandleIgnorablePanic () {
        PanicPolicyClass.setLogger(getMockLogger());

        Panic panic = new Panic("a test", new Exception(), Panic.Reasons.DoesNotUnderstandNetworkMessage);

        getPanicPolicyClass().handleIgnorablePanic(panic);

        verify(getMockLogger(), atLeastOnce()).error(Matchers.anyString(), Matchers.any(Throwable.class));
    }
}
