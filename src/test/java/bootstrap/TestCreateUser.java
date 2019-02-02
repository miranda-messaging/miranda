package bootstrap;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.BootstrapUsersFile;
import org.junit.Test;

/**
 * Created by clarkhobbie on 6/16/17.
 */
public class TestCreateUser extends TestCase {
    @Test
    public void testBootstrap () throws Exception {
        String usersFilename = "data/users.json";
        setupMiranda();
        createKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD, TEMP_ALIAS);
        BootstrapUsersFile bootstrapUsersFile = new BootstrapUsersFile(usersFilename, TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD, TEMP_ALIAS);
        bootstrapUsersFile.createUser("admin", "the admin user");
        bootstrapUsersFile.write();
    }
}
