package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/5/2017.
 */
public class TestStartup extends TestCase {
    private Startup startup;

    private BlockingQueue<Message> cluserFile;
    private BlockingQueue<Message> usersFile;
    private BlockingQueue<Message> topicFile;
    private BlockingQueue<Message> subscriptionsFile;
    private BlockingQueue<Message> messages;
    private BlockingQueue<Message> deliveries;
    private BlockingQueue<Message> startupQueue;
    private Miranda miranda;

    public Miranda getMiranda() {
        return miranda;
    }

    public Startup getStartup() {
        return startup;
    }

    public void reset () {
        super.reset();

        if (null != miranda)
            miranda.stop();

        this.startup = null;
    }

    public BlockingQueue<Message> getCluserFile() {
        return cluserFile;
    }

    public BlockingQueue<Message> getUsersFile() {
        return usersFile;
    }

    public BlockingQueue<Message> getTopicFile() {
        return topicFile;
    }

    public BlockingQueue<Message> getSubscriptionsFile() {
        return subscriptionsFile;
    }

    public BlockingQueue<Message> getMessages() {
        return messages;
    }

    public BlockingQueue<Message> getDeliveries() {
        return deliveries;
    }

    public BlockingQueue<Message> getStartupQueue() {
        return startupQueue;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        String[] empty = new String[0];
        this.miranda = new Miranda(empty);
        this.startup = new Startup(miranda, empty);
    }

    @Test
    public void testStart () {
        setuplog4j();
        long then = System.currentTimeMillis();
        getMiranda().start();

        pause(2000);

        //
        // test that local variables got set
        //
        assert (getMiranda().getCurrentState() instanceof ReadyState);
        assert (getMiranda().getCluster() != null);
        assert (getMiranda().getUsers() != null);
        assert (getMiranda().getPanicPolicy() != null);
        assert (getMiranda().getEvents() != null);
        assert (getMiranda().getDeliveries() != null);
        assert (getMiranda().getTopics() != null);
        assert (getMiranda().getHttp() != null);
        assert (getMiranda().getSubscriptions() != null);

        //
        // test that static variables got set
        //
        assert (getMiranda().getLogger() != null);
        assert (Miranda.timer != null);
        assert (Miranda.fileWatcher != null);
        assert (Miranda.properties != null);
        assert (Miranda.commandLine != null);

        //
        // test that initial garbage collection got done
        //
        assert (UsersFile.getInstance().getLastCollection() > then);
        assert (TopicsFile.getInstance().getLastCollection() > then);
        assert (SubscriptionsFile.getInstance().getLastCollection() > then);
    }

    public boolean containsRootUser (UsersFile usersFile) {
        User root = new User("root", "System admin");
        for (User user : usersFile.getData()) {
            if (user.equals(root))
                return true;
        }

        return false;
    }

    @Test
    public void testSetupRootUser () {
        getMiranda().start();

        pause(1000);

        assert (containsRootUser(UsersFile.getInstance()));
    }
}
