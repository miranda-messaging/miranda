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

package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.servlet.ServletHolderReadyState;
import com.ltsllc.miranda.topics.messages.*;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicHolderReadyState extends ServletHolderReadyState {
    public TopicHolder getTopicHolder() {
        return (TopicHolder) getContainer();
    }

    public TopicHolderReadyState(TopicHolder topicsHolder) throws MirandaException {
        super(topicsHolder);
    }

    public State processMessage (Message message) throws MirandaException {
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
