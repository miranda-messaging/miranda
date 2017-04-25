package last;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.ReadyState;
import com.ltsllc.miranda.miranda.Startup;
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
    public static final String TEST_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1avWB4w2AtIN/DOSyyDu\n"
            + "dN7OA3XVbjyq9cKkVkLtHuKQYvq2w1sFoToeZ15R+J7WxDGFuSzdWa/RbR5LLNeM\n"
            + "BqgGZ+/jwGOipRtUMVa8467ZV5BL6vowkjAyUUevTABUxzTo+YvwrL8LPVpNOO1v\n"
            + "VmAsWOe+lTyeQkAILaSeCvyjdmDRr5O5U5UILlAcZDJ8LFOm9kNQQ4yIVUqAMbBo\n"
            + "MF+vPrmEA09tMqrmR5lb4RsmAUlDxiMWCU9AxwWfksHbd7fV8puvnxjuI1+TZ7SS\n"
            + "Fk1L/bPothhCjsWYr4RMVDluzSAgqsFbAgLXGpraDibVOOrmmBtG2ngu9NJV5fGA\n"
            + "NwIDAQAB\n"
            + "-----END PUBLIC KEY-----";


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

        setuplog4j();

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
        assert (getMiranda().getUserManager() != null);
        assert (getMiranda().getPanicPolicy() != null);
        assert (getMiranda().getEvents() != null);
        assert (getMiranda().getDeliveries() != null);
        assert (getMiranda().getTopicManager() != null);
        assert (getMiranda().getHttp() != null);
        assert (getMiranda().getSubscriptionManager() != null);
        assert (getMiranda().getSessionManager() != null);
        assert (getMiranda().getWriter() != null);

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
        assert (Miranda.getInstance().getUserManager().getUsersFile().getLastCollection() > then);
        assert (Miranda.getInstance().getSubscriptionManager().getSubscriptionsFile().getLastCollection() > then);
        assert (Miranda.getInstance().getTopicManager().getTopicsFile().getLastCollection() > then);
    }

    public boolean containsRootUser (UsersFile usersFile) {
        User root = new User("root", "System admin", "Admin", TEST_PUBLIC_KEY_PEM);
        for (User user : usersFile.getData()) {
            if (user.equals(root))
                return true;
        }

        return false;
    }
}
