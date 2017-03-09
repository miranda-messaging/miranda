package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 3/9/2017.
 */
public class HttpReadyState extends State {
    public HttpReadyState (HttpServer httpServer) {
        super (httpServer);
    }

    public HttpServer getHttpServer () {
        return (HttpServer) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case SetupServlets: {
                SetupServletsMessage setupServletsMessage = (SetupServletsMessage) message;
                nextState = processSetupServletsMessage(setupServletsMessage);
                break;
            }
            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    private State processSetupServletsMessage (SetupServletsMessage message) {
        getHttpServer().addServlets(message.getMappings());

        return this;
    }
}
