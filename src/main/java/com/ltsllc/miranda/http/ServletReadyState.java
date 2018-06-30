package com.ltsllc.miranda.http;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.messages.StopMessage;

/**
 * The ready state for an instance of {@link ServletContainer}.
 * <p>
 * <h3>Message</h3>
 * <ul>
 * <li>AddServelets</li>
 * <li>Start</li>
 * <li>Stop</li>
 * </ul>
 */
public class ServletReadyState extends State {
    public ServletReadyState(ServletContainer servletContainer) throws MirandaException {
        super(servletContainer);
    }

    public ServletContainer getServletContainer() {
        return (ServletContainer) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getServletContainer().getCurrentState();

        switch (message.getSubject()) {
            case AddServlets: {
                AddServletsMessage addServletsMessage = (AddServletsMessage) message;
                nextState = processAddServlets(addServletsMessage);
                break;
            }

            case Start: {
                StartMessage startMessage = (StartMessage) message;
                nextState = processStartMessage(startMessage);
                break;
            }

            case Stop: {
                StopMessage stopMessage = (StopMessage) message;
                nextState = processStopMessage(stopMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processAddServlets(AddServletsMessage addServletsMessage) {
        getServletContainer().addServlets(addServletsMessage.getServlets());

        return getServletContainer().getCurrentState();
    }

    public State processStartMessage(StartMessage startMessage) {
        getServletContainer().startContainer();

        return getServletContainer().getCurrentState();
    }

    public State processStopMessage(StopMessage stopMessage) throws MirandaException {
        getServletContainer().stopContainer();

        return super.processStopMessage(stopMessage);
    }
}
