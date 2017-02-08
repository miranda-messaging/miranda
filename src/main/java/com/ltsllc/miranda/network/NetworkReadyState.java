package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/29/2017.
 */
public class NetworkReadyState extends State {
    private static Logger logger = Logger.getLogger (NetworkReadyState.class);

    private Network network;

    public NetworkReadyState(Network network) {
        super(null);
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }

    public State processMessage (Message m)
    {
        State nextState = this;

        switch (m.getSubject())
        {
            case ConnectTo: {
                ConnectToMessage connectToMessage = (ConnectToMessage) m;
                nextState = processConnectToMessage(connectToMessage);
                break;
            }
            case Listen: {
                ListenMessage listenMessage = (ListenMessage) m;
                nextState = processListenMessage(listenMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }



    private State processListenMessage (ListenMessage listenMessage) {
        getNetwork().listen(listenMessage.getPort());

        return this;
    }


    private State processConnectToMessage (ConnectToMessage connectToMessage) {
        State nextState = this;

        logger.info ("Conecting to " + connectToMessage.getHost() + ":" + connectToMessage.getPort());

        getNetwork().connectTo(connectToMessage.getSender(), connectToMessage.getHost(), connectToMessage.getPort());

        return nextState;
    }
}
