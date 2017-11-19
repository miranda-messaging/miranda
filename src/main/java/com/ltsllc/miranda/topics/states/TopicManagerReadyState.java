/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.topics.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.StandardManagerReadyState;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryMessage;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryResponseMessage;
import com.ltsllc.miranda.topics.DuplicateTopicException;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.topics.TopicNotFoundException;
import com.ltsllc.miranda.topics.messages.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicManagerReadyState extends StandardManagerReadyState<Topic> {
    public TopicManager getTopicManager() {
        return (TopicManager) getContainer();
    }

    public TopicManagerReadyState(TopicManager topicManager) throws MirandaException {
        super(topicManager);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getTopicManager().getCurrentState();

        switch (message.getSubject()) {
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

            case ListTopics: {
                ListTopicsMessage getTopicsMessage = (ListTopicsMessage) message;
                nextState = processGetTopicsMessage(getTopicsMessage);
                break;
            }

            case NewTopic: {
                NewTopicMessage newTopicMessage = (NewTopicMessage) message;
                nextState = processNewTopicMessage(newTopicMessage);
                break;
            }

            case OwnerQuery: {
                OwnerQueryMessage ownerQueryMessage = (OwnerQueryMessage) message;
                nextState = processOwnerQueryMessage(ownerQueryMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        getTopicManager().performGarbageCollection();

        getTopicManager().getTopicsFile().sendGarbageCollectionMessage(getTopicManager().getQueue(), this);

        return getTopicManager().getCurrentState();
    }

    public State processGetTopicMessage(GetTopicMessage getTopicMessage) throws MirandaException {
        Topic topic = getTopicManager().getTopic(getTopicMessage.getName());

        GetTopicResponseMessage getTopicResponseMessage = new GetTopicResponseMessage(getTopicManager().getQueue(),
                this, Results.Success, topic);

        getTopicMessage.reply(getTopicResponseMessage);

        return getTopicManager().getCurrentState();
    }

    public State processGetTopicsMessage(ListTopicsMessage getTopicsMessage) throws MirandaException {
        List<Topic> topics = new ArrayList<Topic>(getTopicManager().getTopics());

        GetTopicsResponseMessage getTopicsResponseMessage = new GetTopicsResponseMessage(getTopicManager().getQueue(),
                this, topics);

        getTopicsMessage.reply(getTopicsResponseMessage);

        return getTopicManager().getCurrentState();
    }

    public State processFileLoadedMessage(FileLoadedMessage fileLoadedMessage) {
        List<Topic> topics = (List<Topic>) fileLoadedMessage.getData();
        getTopicManager().setTopics(topics);

        return getTopicManager().getCurrentState();
    }

    public State processNewTopicMessage(NewTopicMessage newTopicMessage) throws MirandaException {
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

    public State processDeleteTopicMessage(DeleteTopicMessage deleteTopicMessage) throws MirandaException {
        getTopicManager().deleteTopic(deleteTopicMessage.getTopicName());
        DeleteTopicResponseMessage deleteTopicResponseMessage = new DeleteTopicResponseMessage(getTopicManager().getQueue(),
                this, Results.Success);

        deleteTopicMessage.reply(deleteTopicResponseMessage);

        return getTopicManager().getCurrentState();
    }

    public State processCreateTopicMessage(CreateTopicMessage createTopicMessage) throws MirandaException {
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

    public State processUpdateTopicMessage(UpdateTopicMessage updateTopicMessage) throws MirandaException {
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

    public State processOwnerQueryMessage(OwnerQueryMessage ownerQueryMessage) throws MirandaException {
        List<String> property = getTopicManager().getPropertyOf(ownerQueryMessage.getName());
        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(getTopicManager().getQueue(),
                this, ownerQueryMessage.getName(), property, "TopicManager");

        ownerQueryMessage.reply(ownerQueryResponseMessage);

        return getTopicManager().getCurrentState();
    }
}