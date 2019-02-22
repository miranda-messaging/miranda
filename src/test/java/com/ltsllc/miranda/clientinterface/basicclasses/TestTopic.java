package com.ltsllc.miranda.clientinterface.basicclasses;

import org.junit.Before;
import org.junit.Test;

public class TestTopic {
    private Topic topic1;
    private Topic topic2;

    public Topic getTopic1() {
        return topic1;
    }

    public void setTopic1(Topic topic1) {
        this.topic1 = topic1;
    }

    public Topic getTopic2() {
        return topic2;
    }

    public void setTopic2(Topic topic2) {
        this.topic2 = topic2;
    }

    @Before
    public void setup () {
        Topic temp = new Topic("topic1", "me", Topic.RemotePolicies.Immediate);
        long now = System.currentTimeMillis();
        temp.setLastChange(now);
        setTopic1(temp);
        temp = new Topic("topic2", "someone else", Topic.RemotePolicies.Acknowledged);
        temp.setLastChange(now + 1);
        setTopic2(temp);
    }

    @Test
    public void testIsEquivalentTo () {
        assert (!getTopic1().isEquivalentTo(null));
        assert (!getTopic1().isEquivalentTo( new Integer(0)));
        Topic temp = new Topic(getTopic1().getName(), getTopic1().getOwner(), Topic.RemotePolicies.Immediate);
        long now = System.currentTimeMillis();
        temp.setLastChange(now);
        getTopic1().setLastChange(now);
        assert (getTopic1().isEquivalentTo(temp));
        temp.setName("topic2");
        assert (!getTopic1().isEquivalentTo(temp));
        temp.setName(getTopic1().getName());
        temp.setOwner("other");
        assert (!getTopic1().isEquivalentTo(temp));
        temp.setOwner(getTopic1().getOwner());
        temp.setRemotePolicy(Topic.RemotePolicies.Immediate);
        assert (!getTopic1().isEquivalentTo(temp));
    }

    @Test
    public void testEquals () {
        assert (getTopic1().equals(getTopic2()));
        getTopic2().setRemotePolicy(getTopic1().getRemotePolicy());
        getTopic2().setOwner(getTopic1().getOwner());
        getTopic2().setName(getTopic1().getName());
        getTopic2().setLastChange(1 + System.currentTimeMillis());
        assert (!getTopic1().equals( getTopic2()));
        getTopic2().setLastChange(getTopic1().getLastChange());
        getTopic2().setName("other");
        assert (!getTopic1().equals(getTopic2()));
        getTopic2().setName(getTopic1().getName());
        getTopic2().setOwner("other");
        assert (!getTopic1().equals(getTopic2()));
        getTopic2().setOwner(getTopic1().getOwner());
        getTopic2().setRemotePolicy(Topic.RemotePolicies.Written);
        assert(!getTopic1().equals(getTopic2()));
        getTopic2().setRemotePolicy(getTopic1().getRemotePolicy());
        assert (getTopic1().equals(getTopic2()));
    }
}
