package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class TestGetFileResponseMessage extends TestCase {
    private GetFileResponseMessage getFileResponseMessage;

    public GetFileResponseMessage getGetFileResponseMessage() {
        return getFileResponseMessage;
    }

    public void reset () {
        super.reset();

        getFileResponseMessage = null;
    }

    @Before
    public void setup () {
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
    public void testConstructors () {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(null, this, "me");
        assert (getFileResponseMessage.getRequester().equals("me"));

        getFileResponseMessage = new GetFileResponseMessage(null, this, "me", "01020304");
        assert (getFileResponseMessage.getContents().equals("01020304"));

        byte[] buffer = {01, 02, 03, 04};
        getFileResponseMessage = new GetFileResponseMessage(null, this, "me", buffer);
        assert (bytesAreEqual(getFileResponseMessage.getContentAsBytes(), buffer));
    }
}
