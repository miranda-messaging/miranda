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

package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.clientinterface.test.TestCase;
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
