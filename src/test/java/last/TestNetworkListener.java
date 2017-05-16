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

package last;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.network.NetworkListenerReadyState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/20/2017.
 */
public class TestNetworkListener extends TestCase {
    public static class TestNewConnectionLoop implements Runnable {
        private NetworkListener networkListener;
        private BlockingQueue<Handle> handleQueue;

        public BlockingQueue<Handle> getHandleQueue() {
            return handleQueue;
        }

        public NetworkListener getNetworkListener() {
            return networkListener;
        }

        public TestNewConnectionLoop (NetworkListener networkListener, BlockingQueue<Handle> handleQueue) {
            this.networkListener = networkListener;
            this.handleQueue = handleQueue;
        }

        public void run () {
            getNetworkListener().newConnectionLoop(getHandleQueue());
        }
    }

    public static class TestGetConnections implements Runnable {
        private NetworkListener networkListener;

        public NetworkListener getNetworkListener() {
            return networkListener;
        }

        public TestGetConnections (NetworkListener networkListener) {
            this.networkListener = networkListener;
        }

        public void run () {
            getNetworkListener().getConnections();
        }
    }

    @Mock
    private Handle mockHandle;

    private com.ltsllc.miranda.test.TestNetworkListener testNetworkListener;
    private Thread thread;

    public com.ltsllc.miranda.test.TestNetworkListener getTestNetworkListener() {
        return testNetworkListener;
    }

    public Handle getMockHandle() {
        return mockHandle;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void reset () {
        super.reset();

        mockHandle = null;
        testNetworkListener = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockHandle = mock(Handle.class);
        testNetworkListener = new com.ltsllc.miranda.test.TestNetworkListener(6789);
    }

    @Test
    public void testConstructor () {
        assert (getTestNetworkListener().getCurrentState() instanceof NetworkListenerReadyState);
    }

    @Test
    public void testPerformStartupSuccess () {
        BlockingQueue<Handle> handleQueue = new LinkedBlockingQueue<Handle>();

        getTestNetworkListener().performStartup(handleQueue);

        assert (getTestNetworkListener().startupCalled());
    }

    @Test
    public void testPerformStartupExceptionDontPanic () {
        setupMockMiranda();
        NetworkException networkException = new NetworkException("Test", NetworkException.Errors.Test);
        getTestNetworkListener().setNetworkException(networkException);

        BlockingQueue<Handle> handleQueue = new LinkedBlockingQueue<Handle>();
        when(getMockMiranda().panic(Matchers.any(Panic.class))).thenReturn(false);

        getTestNetworkListener().performStartup(handleQueue);

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
        assert (!(getTestNetworkListener().getCurrentState() instanceof StopState));
    }

    @Test
    public void testPerformStartupExceptionPanic () {
        setupMockMiranda();
        NetworkException networkException = new NetworkException("Test", NetworkException.Errors.Test);
        getTestNetworkListener().setNetworkException(networkException);

        BlockingQueue<Handle> handleQueue = new LinkedBlockingQueue<Handle>();

        when(getMockMiranda().panic(Matchers.any(Panic.class))).thenReturn(true);

        getTestNetworkListener().performStartup(handleQueue);

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
        assert (getTestNetworkListener().getCurrentState() instanceof StopState);
    }

    public void startNewConnectionLoop (BlockingQueue<Handle> handleQueue) {
        TestNewConnectionLoop testNewConnectionLoop = new TestNewConnectionLoop(getTestNetworkListener(), handleQueue);
        Thread thread = new Thread(testNewConnectionLoop);
        thread.start();

        setThread(thread);
    }

    public void putHandleOnQueue (BlockingQueue<Handle> handleQueue, Handle handle) {
        try {
            handleQueue.put(handle);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Test
    public void testNewConnnectionLoop () {
        setupMockNetwork();
        BlockingQueue<Handle> handleQueue = new LinkedBlockingQueue<Handle>();

        startNewConnectionLoop(handleQueue);

        putHandleOnQueue(handleQueue, getMockHandle());

        pause(50);

        assert (getTestNetworkListener().getConnectionCount() == 1);

        getTestNetworkListener().setKeepGoing(false);
        putHandleOnQueue(handleQueue, getMockHandle());

        pause(50);

        assert (!getThread().isAlive());
    }

    public void startGetConnections () {
        TestGetConnections testGetConnections = new TestGetConnections(getTestNetworkListener());
        Thread thread = new Thread (testGetConnections);
        thread.start();

        setThread(thread);
    }

    @Test
    public void testGetConnections () {
        setupMockNetwork();

        startGetConnections();

        getTestNetworkListener().putHandle(getMockHandle());

        pause(50);

        assert (getTestNetworkListener().getConnectionCount() == 1);

        getTestNetworkListener().setKeepGoing(false);
        getTestNetworkListener().putHandle(getMockHandle());

        pause(50);

        assert (!(getThread().isAlive()));
    }
}
