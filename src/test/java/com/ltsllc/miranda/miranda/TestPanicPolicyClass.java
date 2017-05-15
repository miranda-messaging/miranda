package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestPanicPolicyClass extends TestCase {
    private PanicPolicyClass panicPolicyClass;

    public PanicPolicyClass getPanicPolicyClass() {
        return panicPolicyClass;
    }

    public void reset () {
        super.reset();

        panicPolicyClass = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setupMockMiranda();
        setuplog4j();
        panicPolicyClass = new MirandaPanicPolicy(10, 10000, getMockMiranda(), getMockTimer());
        panicPolicyClass.setTestMode(true);
    }

    @Test
    public void testDecrementPanicCount () {
        getPanicPolicyClass().setPanicCount(5);

        getPanicPolicyClass().decrementPanicCount();

        assert (getPanicPolicyClass().getPanicCount() == 4);

        getPanicPolicyClass().setPanicCount(0);

        getPanicPolicyClass().decrementPanicCount();

        assert (getPanicPolicyClass().getPanicCount() == 0);
    }

    @Test
    public void testStart () {
        getPanicPolicyClass().start();

        verify(getMockTimer(), atLeastOnce()).sendSchedulePeriodic(Matchers.anyLong(), Matchers.any(BlockingQueue.class),
                Matchers.any(Message.class));
    }

    @Test
    public void testHandleCountablePanicBelowLimit () {
        getPanicPolicyClass().setMaxPanicCount(10);

        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstand);

        getPanicPolicyClass().handleCountablePanic(panic);

        verify(getMockMiranda(), never()).shutdown();
    }

    @Test
    public void testHandleCountableAtLimit () {
        getPanicPolicyClass().setMaxPanicCount(1);

        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstand);

        getPanicPolicyClass().handleCountablePanic(panic);

        verify(getMockMiranda(), atLeastOnce()).shutdown();
    }

    @Test
    public void testHandleCountableAboveLimit () {
        getPanicPolicyClass().setMaxPanicCount(1);
        getPanicPolicyClass().setPanicCount(1);

        Panic panic = new Panic("a test", null, Panic.Reasons.DoesNotUnderstand);

        getPanicPolicyClass().handleCountablePanic(panic);

        verify(getMockMiranda(), atLeastOnce()).shutdown();
    }

    @Test
    public void testHandleIgnorablePanic () {
        PanicPolicyClass.setLogger(getMockLogger());

        Panic panic = new Panic("a test", new Exception(), Panic.Reasons.DoesNotUnderstandNetworkMessage);

        getPanicPolicyClass().handleIgnorablePanic(panic);

        verify(getMockLogger(), atLeastOnce()).error(Matchers.anyString(), Matchers.any(Throwable.class));
    }
}
