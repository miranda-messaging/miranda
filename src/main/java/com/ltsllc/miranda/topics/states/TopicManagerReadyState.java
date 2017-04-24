package com.ltsllc.miranda.topics.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryMessage;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryResponseMessage;
import com.ltsllc.miranda.topics.DuplicateTopicException;
import com.ltsllc.miranda.topics.TopicNotFoundException;
import com.ltsllc.miranda.topics.messages.*;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.TopicManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicManagerReadyState extends State {
    public TopicManager getTopicManager() {
        return (TopicManager) getContainer();
    }

    public TopicManagerReadyState(TopicManager topicManager) {
        super(topicManager);
    }

    public State processMessage(Message message) {
        State nextState = getTopicManager().getCurrentState();

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case CreateTopic: {
                CreateTopicMessage createTopicMessage = (CreateTopicMessage) message;
                nextState = processCreateTopicMessage(createTopicMessage);
                break;
            }

            case UpdateTopic: {
                UpdateTopicMessage updateTopicMessage = (UpdateTopicMessage) message;
                nextState = processUpdateTopicMessage(updateTopicMessage);
                break;
            }

            case DeleteTopic: {
                DeleteTopicMessage deleteTopicMessage = (DeleteTopicMessage) message;
                nextState = processDeleteTopicMessage(deleteTopicMessage);
                break;
            }

            case GetTopic: {
                GetTopicMessage getTopicMessage = (GetTopicMessage) message;
                nextState = processGetTopicMessage(getTopicMessage);
                break;
            }

            case GetTopics: {
                GetTopicsMessage getTopicsMessage = (GetTopicsMessage) message;
                nextState = processGetTopicsMessage(getTopicsMessage);
                break;
            }

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage(fileLoadedMessage);
                break;
            }

            case NewTopic: {
                NewTopicMessage newTopicMessage = (NewTopicMessage) message;
                nextState = processNewTopicMessage(newTopicMessage);
                break;
            }

            case OwnerQuery: {
                OwnerQueryMessage ownerQueryMessage = (OwnerQueryMessage) message;
                nextState = processOwnerQueryMessage (ownerQueryMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getTopicManager().performGarbageCollection();

        getTopicManager().getTopicsFile().sendGarbageCollectionMessage(getTopicManager().getQueue(), this);

        return getTopicManager().getCurrentState();
    }

    public State processGetTopicMessage (GetTopicMessage getTopicMessage) {
        Topic topic = getTopicManager().getTopic(getTopicMessage.getName());

        GetTopicResponseMessage getTopicResponseMessage = new GetTopicResponseMessage(getTopicManager().getQueue(),
                this, topic);

        getTopicMessage.reply(getTopicResponseMessage);

        return getTopicManager().getCurrentState();
    }

    public State processGetTopicsMessage (GetTopicsMessage getTopicsMessage) {
        List<Topic> topics = new ArrayList<Topic>(getTopicManager().getTopics());

        GetTopicsResponseMessage getTopicsResponseMessage = new GetTopicsResponseMessage(getTopicManager().getQueue(),
                this, topics);

        getTopicsMessage.reply(getTopicsResponseMessage);

        return getTopicManager().getCurrentState();
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage)
    {
        List<Topic> topics = (List<Topic>) fileLoadedMessage.getData();
        getTopicManager().setTopics(topics);

        return getTopicManager().getCurrentState();
    }

    public State processNewTopicMessage (NewTopicMessage newTopicMessage) {
        Message reply = null;

        try {
            getTopicManager().addTopic(newTopicMessage.getTopic());

            reply = new NewTopicResponseMessage(getTopicManager().getQueue(), this, true);
        } catch (DuplicateTopicException e) {
            reply = new NewTopicResponseMessage(getTopicManager().getQueue(), this, false);
        }

        newTopicMessage.reply(reply);

        return getTopicManager().getCurrentState();
    }

    public State processDeleteTopicMessage (DeleteTopicMessage deleteTopicMessage) {
        getTopicManager().deleteTopic(deleteTopicMessage.getTopicName());
        DeleteTopicResponseMessage deleteTopicResponseMessage = new DeleteTopicResponseMessage(getTopicManager().getQueue(),
                this, Results.Success);

        return getTopicManager().getCurrentState();
    }

    public State processCreateTopicMessage (CreateTopicMessage createTopicMessage) {
        Results result = Results.Unknown;

        try {
            getTopicManager().addTopic(createTopicMessage.getTopic());
            result = Results.Success;
        } catch (DuplicateTopicException e) {
            result = Results.Duplicate;
        }

        CreateTopicResponseMessage response = new CreateTopicResponseMessage(getTopicManager().getQueue(), this,
                result);
        createTopicMessage.reply(response);

        return getTopicManager().getCurrentState();
    }

    public State processUpdateTopicMessage (UpdateTopicMessage updateTopicMessage) {
        Results result = Results.Unknown;

        try {
            getTopicManager().updateTopic(updateTopicMessage.getTopic());
            result = Results.Success;
        } catch (TopicNotFoundException e) {
            result = Results.TopicNotFound;
        }

        UpdateTopicResponseMessage response = new UpdateTopicResponseMessage(getTopicManager().getQueue(), this,
                result);

        updateTopicMessage.reply(response);

        if (result == Results.Success) {
            getTopicManager().getTopicsFile().sendUpdateObjectsMessage(getTopicManager().getQueue(), this,
                    updateTopicMessage.getTopic());
        }

        return getTopicManager().getCurrentState();
    }

    public State processOwnerQueryMessage (OwnerQueryMessage ownerQueryMessage) {
        List<String> property = getTopicManager().getPropertyOf (ownerQueryMessage.getName());
        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(getTopicManager().getQueue(),
                this, ownerQueryMessage.getName(), property, "TopicManager");

        ownerQueryMessage.reply(ownerQueryResponseMessage);

        return getTopicManager().getCurrentState();
    }
}