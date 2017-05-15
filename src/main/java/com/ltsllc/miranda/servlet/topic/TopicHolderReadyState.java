package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.ServletHolderReadyState;
import com.ltsllc.miranda.topics.messages.*;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicHolderReadyState extends ServletHolderReadyState {
    public TopicHolder getTopicHolder() {
        return (TopicHolder) getContainer();
    }

    public TopicHolderReadyState(TopicHolder topicsHolder) {
        super(topicsHolder);
    }

    public State processMessage (Message message) {
        State nextState = getTopicHolder().getCurrentState();

        switch (message.getSubject()) {
            case GetTopicResponse: {
                GetTopicResponseMessage getTopicResponseMessage = (GetTopicResponseMessage) message;
                nextState = processGetTopicResponseMessage (getTopicResponseMessage);
                break;
            }

            case GetTopicsResponse: {
                GetTopicsResponseMessage getTopicsResponseMessage = (GetTopicsResponseMessage) message;
                nextState = processGetTopicsResponseMessage(getTopicsResponseMessage);
                break;
            }

            case UpdateTopicResponse: {
                UpdateTopicResponseMessage updateTopicResponseMessage = (UpdateTopicResponseMessage) message;
                nextState = processUpdateTopicResponseMessage(updateTopicResponseMessage);
                break;
            }

            case DeleteTopicResponse: {
                DeleteTopicResponseMessage deleteTopicResponseMessage = (DeleteTopicResponseMessage) message;
                nextState = processDeleteTopicResponseMessage (deleteTopicResponseMessage);
                break;
            }
            case CreateTopicResponse: {
                CreateTopicResponseMessage createTopicResponseMessage = (CreateTopicResponseMessage) message;
                nextState = processCreateTopicResponseMessage (createTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGetTopicsResponseMessage (GetTopicsResponseMessage getTopicsResponseMessage) {
        getTopicHolder().setTopicsAndAwaken(getTopicsResponseMessage.getTopics());

        return getTopicHolder().getCurrentState();
    }


    public State processCreateTopicResponseMessage (CreateTopicResponseMessage createTopicResponseMessage) {
        getTopicHolder().setCreateResultAndAwaken(createTopicResponseMessage.getResult());

        return getTopicHolder().getCurrentState();
    }

    public State processUpdateTopicResponseMessage (UpdateTopicResponseMessage updateTopicResponseMessage) {
        getTopicHolder().setUpdateResultAndAwaken(updateTopicResponseMessage.getResult());

        return getTopicHolder().getCurrentState();
    }

    public State processDeleteTopicResponseMessage (DeleteTopicResponseMessage deleteTopicResponseMessage) {
        getTopicHolder().setDeleteResultAndAwaken(deleteTopicResponseMessage.getResult());

        return getTopicHolder().getCurrentState();
    }

    public State processGetTopicResponseMessage (GetTopicResponseMessage getTopicResponseMessage) {
        getTopicHolder().setTopicAndAwaken(getTopicResponseMessage.getTopic());

        return getTopicHolder().getCurrentState();
    }
}
