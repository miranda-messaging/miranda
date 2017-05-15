package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestMirandaPanicPolicy extends TestCase {
    private MirandaPanicPolicy mirandaPanicPolicy;

    public MirandaPanicPolicy getMirandaPanicPolicy() {
        return mirandaPanicPolicy;
    }

    public void reset () {
        super.reset();

        mirandaPanicPolicy = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        setupMockMiranda();
        setupMockTimer();
        mirandaPanicPolicy = new MirandaPanicPolicy(10, 10000, getMockMiranda(), getMockTimer());
        mirandaPanicPolicy.setTestMode(true);
    }

    @Test
    public void testPanicStartupPanic () {
        StartupPanic startupPanic = new StartupPanic("a test", null, StartupPanic.StartupReasons.Test);

        boolean result = getMirandaPanicPolicy().panic(startupPanic);

        assert (result);
    }

    @Test
    public void testPanicDoesNotUnderstand () {
        assert (getMirandaPanicPolicy().getPanicCount() == 0);

        Panic panic = new Panic ("a test", null, Panic.Reasons.DoesNotUnderstand);

        boolean result = getMirandaPanicPolicy().panic(panic);

        assert (!result);
        assert (getMirandaPanicPolicy().getPanicCount() == 1);
    }

    @Test
    public void testPanicDoesNotUnderstandNetworkMessage () {
        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstandNetworkMessage);

        int before = getMirandaPanicPolicy().getPanicCount();

        boolean result = getMirandaPanicPolicy().panic(panic);

        assert (!result);
        assert (before == getMirandaPanicPolicy().getPanicCount());
    }

}
