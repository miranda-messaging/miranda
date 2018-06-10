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

package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.timer.messages.TimeoutMessage;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestMirandaTimer extends TestCase {
    private MirandaTimer timer;

    public MirandaTimer getTimer() {
        return timer;
    }

    public void reset () throws Exception {
        super.reset();

        timer = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();
        timer = new MirandaTimer();
    }

    @Test
    public void testConstructor () {
        assert (getTimer().getCurrentState() instanceof MirandaTimerReadyState);
    }

    @Test
    public void testSendScheduleOnce () {
        TimeoutMessage timeoutMessage = new TimeoutMessage(null,this);

        getTimer().sendScheduleOnce(1500, null, timeoutMessage);

        assert (contains(Message.Subjects.ScheduleOnce, getTimer().getQueue()));
    }

    @Test
    public void testSendSchedulePeriodic () {
        TimeoutMessage timeoutMessage = new TimeoutMessage(null, this);

        getTimer().sendSchedulePeriodic(0, 1500, null, timeoutMessage);

        assert (contains(Message.Subjects.SchedulePeriodic, getTimer().getQueue()));
    }
}
