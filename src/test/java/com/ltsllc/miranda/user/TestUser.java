package com.ltsllc.miranda.user;

import com.ltsllc.miranda.servlet.user.UserObject;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Test;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestUser extends TestCase {
    private User user;

    public User getUser() {
        return user;
    }

    public static final String TEST_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMOinA1ha2eTP/9KwszAhYfbNJiapjz8/3mgTnglRxi7Hi1cJSTODks7SKzzkDdM+GsQctOTMYMA3hittfuU3PiCv0hmDotwpdjvW+5r2xJ+DuFV7dSZOEVMeMJlO2MJEPFS0KPI/DUdy8+A//yu4qPzzC5A6U1zJ1jcQNzl/WUwIDAQAB";

    @Test
    public void testAsUser () {
        try {
            User user = new User("whatever", "Publisher", "another whatever", TEST_KEY);
            UserObject userObject = user.asUserObject();

            assert (userObject.getName().equals("whatever"));
            assert (userObject.getDescription().equals("another whatever"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
