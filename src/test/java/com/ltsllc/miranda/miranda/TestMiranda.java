package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 3/4/2017.
 */
public class TestMiranda extends TestCase {
    private Miranda miranda;

    public Miranda getMiranda() {
        return miranda;
    }

    public void reset () {
        super.reset();

        this.miranda = null;
    }

    @Before
    public void setup () {
        super.setup();

        String[] empty = new String[0];

        miranda = new Miranda(empty);
    }

    @Test
    public void testReset () {
        setuplog4j();
        
        getMiranda().reset();

        assert (Miranda.properties == null);
        assert (Miranda.fileWatcher == null);
        assert (Miranda.timer == null);
    }

    @Test
    public void testStop () {
        getMiranda().stop();

        assert (getMiranda().getCurrentState() instanceof StopState);
    }

    @Test
    public void testSendNewSessionMessage () {
        Session session = new Session("whatever",123, 456);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getMiranda().setQueue(queue);

        getMiranda().sendAddSessionMessage(null, this, session);

        assert (contains(Message.Subjects.AddSession, queue));
    }

    @Test
    public void testSendExpiredSessions () {
        Session session = new Session("whatever",123, 456);
        List<Session> expiredSessions = new ArrayList<Session>();
        expiredSessions.add(session);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getMiranda().setQueue(queue);

        getMiranda().sendSessionsExpiredMessage(null, this, expiredSessions);

        assert (contains(Message.Subjects.SessionsExpired, queue));
    }
}
