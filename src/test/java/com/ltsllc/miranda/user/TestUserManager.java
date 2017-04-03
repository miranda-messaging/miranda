package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Subscriber;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.states.UserManagerReadyState;
import com.ltsllc.miranda.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/2/2017.
 */
public class TestUserManager extends TestCase {
    private UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    public void reset () {
        super.reset();

        userManager = null;
    }

    public static final String TEST_FILENAME = "testfile";
    public static final String[] TEST_FILE_CONTENTS = {
            "[",
            "{",
            "    \"name\" : \"what\",",
            "    \"description\" : \"a test user\",",
            "    \"expires\" : " + Long.MAX_VALUE + ",",
            "    \"publicKeyString\" : \"ACED0005737200146A6176612E73656375726974792E4B6579526570BDF94FB3889AA5430200044C0009616C676F726974686D7400124C6A6176612F6C616E672F537472696E673B5B0007656E636F6465647400025B424C0006666F726D617471007E00014C00047479706574001B4C6A6176612F73656375726974792F4B657952657024547970653B7870740003525341757200025B42ACF317F8060854E002000078700000012630820122300D06092A864886F70D01010105000382010F003082010A0282010100AAC60BD916BCE303B7A36E3D050CAF8BF5BFD1695CA2EDBABD2E27A1CD2BAE8E6AA8F51D56A6DFE7321299DEA071040E81C4C6F960601562BFC79891985FC24C4B498CB38A37D316EF6572F7B5C3ABB627E227DC616105BC4718ABF3E9ABFF6D4691AF23FD0562357DD93395BBF6C194DFDC00ABB913A90FC09A5D9A791DEBE616DB47A3A093A8977D0D0D7B59C2D93C1E6CE7FD18C2FCF5FDE395BDC825A60718E3E9B363FB782FE0998301E34AE098E264AFC81A2CD63CEECF1F3CE39B673A5BC3A6177D2B0F2C7A3D5A83E681AB52FF2DE539CA8B3379AFAF6BAB8A338E9173B9352FC6D90A72A1F49C8F43949840D67E2B938A12088102458F5B04E9526D0203010001740005582E3530397E7200196A6176612E73656375726974792E4B6579526570245479706500000000000000001200007872000E6A6176612E6C616E672E456E756D000000000000000012000078707400065055424C4943\"",
            "}",
            "]"
    };

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        setupMockMiranda();

        when (getMockMiranda().getWriter()).thenReturn(getMockWriter());
        createFile(TEST_FILENAME, TEST_FILE_CONTENTS);

        userManager = new UserManager(TEST_FILENAME);
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILENAME);
    }

    public void testConstructor_original () {
        try {
            Key key = Utils.loadKey("root.keystore", "whatever", "root");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(key);
            objectOutputStream.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String hexString = Utils.bytesToString(bytes);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor () {
        assert (getUserManager().getCurrentState() instanceof UserManagerReadyState);
        assert (getUserManager().getUsers() != null);
        assert (getUserManager().getUsersFile() != null);
    }

    public boolean isSubscriber (BlockingQueue<Message> queue, UsersFile usersFile) {
        for (Subscriber subscriber : usersFile.getSubscribers()) {
            if (subscriber.getQueue() == queue)
                return true;
        }

        return false;
    }

    @Test
    public void testStart() {
        getUserManager().start();

        pause(50);

        assert (isSubscriber(getUserManager().getQueue(), getUserManager().getUsersFile()));
    }

    public static final String[] EXPIRED_CONTENTS = {
            "[ ",
            "{",
            "    \"name\" : \"what\",",
            "    \"description\" : \"an expired user\", ",
            "    \"status\" : \"Deleted\"",
            "}",
            "]"
    };

    @Test
    public void testPerformGarbageCollection () {
        getUserManager().start();

        setupMockMiranda();

        when(getMockMiranda().getWriter()).thenReturn(getMockWriter());

        createFile(TEST_FILENAME, EXPIRED_CONTENTS);
        this.userManager = new UserManager(TEST_FILENAME);
        this.userManager.start();

        pause(50);

        assert(getUserManager().getUsers().size() > 0);

        getUserManager().performGarbageCollection();

        assert (getUserManager().getUsers().size() < 1);
    }

    @Test
    public void testContains () {
        User shouldContain = new User ("what", "an expired user");
        shouldContain.setStatus(StatusObject.Status.Deleted);
        User shouldNotContain = new User ("not here", "absent");

        getUserManager().start();

        pause(50);

        assert (getUserManager().contains(shouldContain));
        assert (!getUserManager().contains(shouldNotContain));
    }

    @Test
    public void testAddUser () {
        User newUser = new User("what", "ever");

        assert (!getUserManager().contains(newUser));

        getUserManager().addUser(newUser);

        assert (getUserManager().contains(newUser));
    }

    @Test
    public void testGetUser () {
        User newUser = new User("what", "ever");

        getUserManager().contains(newUser);

        getUserManager().addUser(newUser);
        User user = getUserManager().getUser("what");

        assert (null != user);
        assert (user.equals(newUser));
    }
}
