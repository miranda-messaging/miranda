package com.ltsllc.miranda.servlet.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.messages.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.objects.StatusObject;
import com.ltsllc.miranda.servlet.StatusServlet;

/**
 * Created by Clark on 3/9/2017.
 */
public class MirandaStatusReadyState extends State {
    private StatusServlet statusServlet;

    public StatusServlet getStatusServlet() {
        return statusServlet;
    }

    public void setStatusServlet(StatusServlet statusServlet) {
        this.statusServlet = statusServlet;
    }

    public MirandaStatusReadyState (MirandaStatus mirandaStatus) {
        super(mirandaStatus);
    }

    public MirandaStatus getMirandaStatus () {
        return (MirandaStatus) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetStatusResponse: {
                GetStatusResponseMessage getStatusResponseMessage = (GetStatusResponseMessage) message;
                nextState = processGetStatusResponseMessage(getStatusResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    private State processGetStatusResponseMessage (GetStatusResponseMessage getStatusResponseMessage) {
        StatusObject statusObject = (StatusObject) getStatusResponseMessage.getStatusObject();
        getMirandaStatus().receivedStatus(statusObject);

        return this;
    }
}
