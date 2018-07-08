package com.ltsllc.miranda.operations.subscriptions;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.session.Session;

public class CreateSubscriptionOperationStartState extends State {
    private Session session;
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public CreateSubscriptionOperationStartState(Subscription subscription, Session session,
                                                 CreateSubscriptionOperation createSubscriptionOperation) {

        super(createSubscriptionOperation);
        setSession(session);
        setSubscription(subscription);
    }

    public CreateSubscriptionOperation getSubscriptionOperation () {
        return (CreateSubscriptionOperation) getContainer();
    }

    public State start () {
        State nextState = null;
        try {
            Miranda.getInstance().getSubscriptionManager().sendCreateSubscriptionMessage(
                    getSubscriptionOperation().getQueue(), getSubscriptionOperation(),
                    getSession(), getSubscription());
            Miranda.getInstance().getTopicManager().sendSubscribe(getSubscriptionOperation().getQueue(),
                    getSubscriptionOperation(), getSubscription());

            nextState = new CreateSubscriptionOperationReadyState(getSubscriptionOperation());


        } catch (MirandaException e) {
            Panic panic = new Panic("Exception trying to create subscription", e,
                    Panic.Reasons.ExceptionCreatingSubscription);
            Miranda.panicMiranda(panic);
        }

        return nextState;
    }
}
