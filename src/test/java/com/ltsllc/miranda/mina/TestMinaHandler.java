package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.test.TestCase;
import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/17/2017.
 */
public class TestMinaHandler extends TestCase {
    @Mock
    private MinaHandle mockMinaHandle;

    @Mock
    private IoSession mockSession;

    private MinaHandler minaHandler;

    public MinaHandler getMinaHandler() {
        return minaHandler;
    }

    public IoSession getMockSession() {

        return mockSession;
    }

    public MinaHandle getMockMinaHandle() {

        return mockMinaHandle;
    }

    public void reset () {
        super.reset();

        mockMinaHandle = null;
        mockSession = null;
        minaHandler = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockSession = mock(IoSession.class);
        mockMinaHandle = mock(MinaHandle.class);
        minaHandler = new MinaHandler();
    }

    @Test
    public void testSesssionCreated () {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("foo.com", 1234);
        when(getMockSession().getRemoteAddress()).thenReturn(inetSocketAddress);
        try {
            getMinaHandler().sessionCreated(getMockSession());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert (getMinaHandler().getSession() == getMockSession());
    }


    public static final String TEST_MESSAGE = "{ \"wireSubject\" : \"Join\", \"dns\" : \"foo.com\", \"ip\" : \"192.168.1.1\", "
                + "\"port\" : 6789, \"description\" : \"a node\", \"className\" : \"com.ltsllc.miranda.node.networkMessages.JoinWireMessage\"}";
    @Test
    public void testMessageReceived () {
        String message = TEST_MESSAGE;

        getMinaHandler().setMinaHandle(getMockMinaHandle());
        try {
            getMinaHandler().messageReceived(getMockSession(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(getMockMinaHandle(), atLeastOnce()).deliver(Matchers.any(WireMessage.class));
    }

    @Test
    public void testSendOnWire () {
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        getMinaHandler().setSession(getMockSession());

        getMinaHandler().sendOnWire(joinWireMessage);

        verify(getMockSession(), atLeastOnce()).write(Matchers.any());
    }

}
