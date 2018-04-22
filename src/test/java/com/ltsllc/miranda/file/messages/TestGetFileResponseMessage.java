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

package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestGetFileResponseMessage extends TestCase {
    private GetFileResponseMessage getFileResponseMessage;

    public GetFileResponseMessage getGetFileResponseMessage() {
        return getFileResponseMessage;
    }

    public void reset () throws Exception {
        super.reset();

        getFileResponseMessage = null;
    }

    @Before
    public void setup () throws Exception {
        super.setup();

        setuplog4j();

        getFileResponseMessage = new GetFileResponseMessage(null, this, "me");
    }

    public static boolean bytesAreEqual (byte[] a1, byte[] a2)
    {
        if (a1 == a2)
            return true;

        if (a1.length != a2.length)
            return false;

        if (a1 == null || a2 == null)
            return false;

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i])
                return false;
        }

        return true;
    }


    @Test
    public void testConstructors () throws IOException {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "me");
        assert (getFileResponseMessage.getRequester().equals("me"));

        getFileResponseMessage = new GetFileResponseMessage(null, this, "me", "01020304");
        assert (getFileResponseMessage.getContents().equals("01020304"));

        byte[] buffer = {01, 02, 03, 04};
        getFileResponseMessage = new GetFileResponseMessage(null, this, "me", buffer);
        assert (bytesAreEqual(getFileResponseMessage.getContentAsBytes(), buffer));
    }
}
