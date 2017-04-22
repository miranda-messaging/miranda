package com.ltsllc.miranda.server;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.subsciptions.Subscription;
import com.ltsllc.miranda.http.HttpPostMessage;
import com.ltsllc.miranda.subsciptions.NewSubscriptionHandler;
import com.ltsllc.miranda.subsciptions.NewSubscriptionHandlerReadyState;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.test.TestCase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.lang.reflect.Type;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/20/2017.
 */
public class TestNewObjectHandlerReadyState extends TestCase {
    @Mock
    private SubscriptionsFile mockSubscriptionsFile;

    @Mock
    private NewSubscriptionHandler mockNewSubscriptionHandler;

    @Mock
    private Consumer mockConsumer;

    @Mock
    private ChannelHandlerContext mockChannelHandlerContext;

    @Mock
    private HttpRequest mockHttpRequest;

    private NewSubscriptionHandlerReadyState readyState;

    public NewSubscriptionHandlerReadyState getReadyState() {
        return readyState;
    }

    public SubscriptionsFile getMockSubscriptionsFile() {
        return mockSubscriptionsFile;
    }

    public NewSubscriptionHandler getMockNewSubscriptionHandler() {
        return mockNewSubscriptionHandler;
    }

    public Consumer getMockConsumer() {
        return mockConsumer;
    }

    public ChannelHandlerContext getMockChannelHandlerContext() {
        return mockChannelHandlerContext;
    }

    public HttpRequest getMockHttpRequest() {
        return mockHttpRequest;
    }

    public void reset () {
        super.reset();

        mockConsumer = null;
        mockNewSubscriptionHandler = null;
        mockSubscriptionsFile = null;
        mockChannelHandlerContext = null;
        mockHttpRequest = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockConsumer = mock(Consumer.class);
        mockNewSubscriptionHandler = mock(NewSubscriptionHandler.class);
        mockSubscriptionsFile = mock(SubscriptionsFile.class);
        mockChannelHandlerContext = mock(ChannelHandlerContext.class);
        mockHttpRequest = mock(HttpRequest.class);
        readyState = new NewSubscriptionHandlerReadyState(mockConsumer, mockSubscriptionsFile, mockNewSubscriptionHandler);
    }

    public static final String TEST_JSON = "{"
            + "\"name\" : \"whatever\" "
            + "}";

    /*
    @Test
    public void testProcessHttpPostMessage () {
        Subscription subscription = new Subscription("whatever");
        Type type = Subscription.class;

        HttpPostMessage httpPostMessage = new HttpPostMessage(null, this, getMockHttpRequest(), TEST_JSON, getMockChannelHandlerContext());

        when(getMockNewSubscriptionHandler().decodeContent(type, TEST_JSON)).thenReturn(subscription);
        when(getMockSubscriptionsFile().contains(subscription)).thenReturn(false);

        getReadyState().processHttpPostMessage(httpPostMessage);

        verify(getMockChannelHandlerContext(), atLeastOnce()).writeAndFlush(Matchers.any());
        verify(getMockChannelHandlerContext(), atLeastOnce()).close();
    }
    */
}
