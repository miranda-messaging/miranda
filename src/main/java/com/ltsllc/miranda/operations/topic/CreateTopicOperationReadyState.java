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

package com.ltsllc.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.messages.CreateTopicResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateTopicOperationReadyState extends State {
    public CreateTopicOperation getCreateTopicOperation () {
        return (CreateTopicOperation) getContainer();
    }

    public CreateTopicOperationReadyState (CreateTopicOperation createTopicOperation) throws MirandaException {
        super(createTopicOperation);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getCreateTopicOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage (getUserResponseMessage);
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

    public State processCreateTopicResponseMessage (CreateTopicResponseMessage message) {
        CreateTopicResponseMessage createTopicResponseMessage = new CreateTopicResponseMessage(
                getCreateTopicOperation().getQueue(), this, message.getResult());

        send (getCreateTopicOperation().getRequester(), createTopicResponseMessage);

        return StopState.getInstance();
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() != Results.Success) {
            CreateTopicResponseMessage createTopicResponseMessage = new CreateTopicResponseMessage(getCreateTopicOperation().getQueue(),
                    this, getUserResponseMessage.getResult());

            send(getCreateTopicOperation().getRequester(), createTopicResponseMessage);
        }

        Miranda.getInstance().getTopicManager().sendCreateTopicMessage(getCreateTopicOperation().getQueue(), this,
                getCreateTopicOperation().getTopic());

        return getCreateTopicOperation().getCurrentState();
    }
}
