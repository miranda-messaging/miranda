package bootstrap;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.BootstrapUsersFile;
import org.bouncycastle.openssl.PEMEncryptor;
import org.junit.Test;

/**
 * Created by clarkhobbie on 6/16/17.
 */
public class TestCreateUser extends TestCase {
    @Test
    public void testBootstrap () throws Exception {
        String usersFilename = "data/users.json";
        String keyStoreFileName = "keystore";
        String password = "whatever";
        BootstrapUsersFile bootstrapUsersFile = new BootstrapUsersFile(usersFilename, keyStoreFileName, password);
        bootstrapUsersFile.createUser("admin", "the admin user");
        bootstrapUsersFile.write();



    }
}
