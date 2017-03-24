package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.test.TestCase;
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

    public void reset () {
        super.reset();

        timer = null;
    }

    @Before
    public void setup () {
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

        getTimer().sendSchedulePeriodic(1500, null, timeoutMessage);

        assert (contains(Message.Subjects.SchedulePeriodic, getTimer().getQueue()));
    }
}
