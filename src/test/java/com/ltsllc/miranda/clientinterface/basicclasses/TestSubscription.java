package com.ltsllc.miranda.clientinterface.basicclasses;

import org.junit.Before;
import org.junit.Test;

public class TestSubscription {
    private Subscription subscription1;
    private Subscription subscription2;

    public Subscription getSubscription1() {
        return subscription1;
    }

    public void setSubscription1(Subscription subscription1) {
        this.subscription1 = subscription1;
    }

    public Subscription getSubscription2() {
        return subscription2;
    }

    public void setSubscription2(Subscription subscription2) {
        this.subscription2 = subscription2;
    }

    @Before
    public void setup () {
        Subscription temp = new Subscription("test","me", "topic", "Dataurl","livelinessUrl",
                Subscription.ErrorPolicies.Drop);
        temp.setLastChange(System.currentTimeMillis());
        setSubscription1(temp);
        temp = new Subscription("another test","me", "topic1", "dataUrl", "livelinessUrl",
                Subscription.ErrorPolicies.DeadLetter);
    }

    @Test
    public void testIsEquivalentTo () {
        assert (getSubscription1().isEquivalentTo(getSubscription1()));
        assert (!getSubscription1().isEquivalentTo(null));
        assert (!getSubscription1().isEquivalentTo(new Integer(7)));
        assert (!getSubscription1().isEquivalentTo(getSubscription2()));
        Subscription temp = new Subscription ("other subscription", getSubscription1().getOwner(), getSubscription1().getTopic(),
                getSubscription1().getDataUrl(), getSubscription1().getLivelinessUrl(), getSubscription1().getErrorPolicy());
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setName(getSubscription1().getName());
        temp.setOwner("someone");
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setOwner(getSubscription1().getOwner());
        temp.setDataUrl("a different URL");
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setDataUrl(getSubscription1().getDataUrl());
        temp.setOwner("unknown");
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setOwner(getSubscription1().getOwner());
        temp.setErrorPolicy(Subscription.ErrorPolicies.DeadLetter);
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setErrorPolicy(getSubscription1().getErrorPolicy());
        temp.setName("a different name");
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setName(getSubscription1().getName());
        temp.setLivelinessUrl("a different URL");
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setLivelinessUrl(getSubscription1().getLivelinessUrl());
        temp.setTopic("a different  URL");
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setLivelinessUrl(getSubscription1().getLivelinessUrl());
        temp.setLastChange(System.currentTimeMillis() +1);
        assert (!getSubscription1().isEquivalentTo(temp));
        temp.setLastChange(getSubscription1().getLastChange());
        assert (getSubscription1().isEquivalentTo(temp));
    }

    @Test
    public void testCopyFrom () {
        Subscription temp = new Subscription ("other subscription", "other owner","other topic",
                "other data URL", "other liveliness URL", Subscription.ErrorPolicies.DeadLetter);
        long now = System.currentTimeMillis();
        temp.setLastChange(now);
        getSubscription1().copyFrom(temp);

        assert(getSubscription1().getName().equals(temp.getName()));
        assert (getSubscription1().getLastChange() == temp.getLastChange());
        assert (getSubscription1().getOwner().equals(temp.getOwner()));
        assert (getSubscription1().getErrorPolicy()) == temp.getErrorPolicy();
        assert (getSubscription1().getLivelinessUrl().equals("other liveliness URL"));
        assert (getSubscription1().getTopic().equals("other topic"));
        assert (getSubscription1().getTopic().equals("other data URL"));
    }

    @Test
    public void testUpdateFrom() {
        Subscription temp = new Subscription ("other subscription", "other owner","other topic",
                "other data URL", "other liveliness URL", Subscription.ErrorPolicies.DeadLetter);
        long now = System.currentTimeMillis();
        temp.setLastChange(now);
        getSubscription1().updateFrom(temp);

        assert(getSubscription1().getName().equals(temp.getName()));
        assert (getSubscription1().getLastChange() == temp.getLastChange());
        assert (getSubscription1().getOwner().equals(temp.getOwner()));
        assert (getSubscription1().getErrorPolicy()) == temp.getErrorPolicy();
        assert (getSubscription1().getLivelinessUrl().equals("other liveliness URL"));
        assert (getSubscription1().getTopic().equals("other topic"));
        assert (getSubscription1().getTopic().equals("other data URL"));
    }

    @Test
    public void testEquals () {
        assert (getSubscription1().equals(getSubscription1()));
        assert (!getSubscription1().equals(null));
        assert (!getSubscription1().equals(new Integer(7)));
        assert (!getSubscription1().equals(getSubscription2()));
        Subscription temp = new Subscription ("other subscription", getSubscription1().getOwner(), getSubscription1().getTopic(),
                getSubscription1().getDataUrl(), getSubscription1().getLivelinessUrl(), getSubscription1().getErrorPolicy());
        assert (!getSubscription1().equals(temp));
        temp.setName(getSubscription1().getName());
        temp.setOwner("someone");
        assert (!getSubscription1().equals(temp));
        temp.setOwner(getSubscription1().getOwner());
        temp.setDataUrl("a different URL");
        assert (!getSubscription1().equals(temp));
        temp.setDataUrl(getSubscription1().getDataUrl());
        temp.setOwner("unknown");
        assert (!getSubscription1().equals(temp));
        temp.setOwner(getSubscription1().getOwner());
        temp.setErrorPolicy(Subscription.ErrorPolicies.DeadLetter);
        assert (!getSubscription1().equals(temp));
        temp.setErrorPolicy(getSubscription1().getErrorPolicy());
        temp.setName("a different name");
        assert (!getSubscription1().equals(temp));
        temp.setName(getSubscription1().getName());
        temp.setLivelinessUrl("a different URL");
        assert (!getSubscription1().equals(temp));
        temp.setLivelinessUrl(getSubscription1().getLivelinessUrl());
        temp.setTopic("a different  URL");
        assert (!getSubscription1().equals(temp));
        temp.setLivelinessUrl(getSubscription1().getLivelinessUrl());
        temp.setLastChange(System.currentTimeMillis() +1);
        assert (!getSubscription1().equals(temp));
        temp.setLastChange(getSubscription1().getLastChange());
        assert (getSubscription1().equals(temp));

    }


}
