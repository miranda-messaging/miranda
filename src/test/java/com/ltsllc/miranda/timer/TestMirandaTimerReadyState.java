package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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

    public void reset () {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupTimer();

        readyState = new MirandaTimerReadyState(getMockTimer());
    }

    @Test
    public void testProcessScheduleOnce () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        TimeoutMessage timeoutMessage = new TimeoutMessage(null, this);
        ScheduleOnceMessage scheduleOnceMessage = new ScheduleOnceMessage(null, this, 250, timeoutMessage, queue);

        State nextState = getReadyState().processMessage(scheduleOnceMessage);

        assert (nextState instanceof MirandaTimerReadyState);

        pause(500);

        assert (contains(Message.Subjects.Timeout, queue));
    }

    @Test
    public void testProcessSchedulePeriodic () {
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
