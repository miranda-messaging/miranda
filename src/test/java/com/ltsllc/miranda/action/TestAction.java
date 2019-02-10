package com.ltsllc.miranda.action;


import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.actions.Action;
import org.junit.Before;
import org.junit.Test;

public class TestAction extends TestCase {
    private Action action;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Before
    public void setup () throws Exception {
        reset();
        super.setup();

        action = new Action("test");
    }

    public void reset () {
        action = null;
    }

    @Test
    public void testRegister () {
        assert (Action.find(getAction()) == false);
        Action.register(getAction());
        assert (Action.find(getAction()));
    }

}
