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
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestMirandaTimerReadyState extends TestCase {
    private MirandaTimerReadyState readyState;

    public MirandaTimerReadyState getReadyState() {
        return readyState;
    }

    public void reset () throws Exception {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();
        setupTimer();

        readyState = new MirandaTimerReadyState(getMockTimer());
    }

    @Test
    public void testProcessScheduleOnce () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        TimeoutMessage timeoutMessage = new TimeoutMessage(null, this);
        ScheduleOnceMessage scheduleOnceMessage = new ScheduleOnceMessage(null, this, 250, timeoutMessage, queue);

        State nextState = getReadyState().processMessage(scheduleOnceMessage);

        assert (nextState instanceof MirandaTimerReadyState);

        pause(500);

        assert (contains(Message.Subjects.Timeout, queue));
    }

    @Test
    public void testProcessSchedulePeriodic () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        TimeoutMessage timeoutMessage = new TimeoutMessage(null, this);
        SchedulePeriodicMessage schedulePeriodicMessage = new SchedulePeriodicMessage(null, this, 250, timeoutMessage, queue);

        State nextState = getReadyState().processMessage(schedulePeriodicMessage);

        assert (nextState instanceof MirandaTimerReadyState);

        pause (500);

        assert (contains(Message.Subjects.Timeout, queue));

        List<Message> list = new ArrayList<Message>();
        queue.drainTo(list);

        pause(500);

        assert (contains(Message.Subjects.Timeout, queue));
    }
}
