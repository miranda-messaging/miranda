package com.ltsllc.miranda.operations;

import com.ltsllc.commons.util.ImprovedRandom;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.operations.user.CreateUserOperation;
import com.ltsllc.miranda.session.Session;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestOperation {
    @Test
    public void testConstructor () throws Exception{
        Session session = new Session();
        BlockingQueue<Message> requester = new LinkedBlockingQueue<>();
        ImprovedRandom improvedRandom = new ImprovedRandom();
        User user = User.createRandom(improvedRandom);
        Operation operation = new CreateUserOperation(requester, session, user);
        assert (requester == operation.getRequester());
        assert (session == operation.getSession());
        assert (null != operation.getUUIDString());
    }
}
