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
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.messages.UpdateTopicResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/23/2017.
 */
public class UpdateTopicOperationReadyState extends State {
    public UpdateTopicOperation getUpdateTopicOperation() {
        return (UpdateTopicOperation) getContainer();
    }

    public UpdateTopicOperationReadyState(UpdateTopicOperation updateTopicOperation) throws MirandaException {
        super(updateTopicOperation);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getUpdateTopicOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(getUserResponseMessage);
                break;
            }

            case UpdateTopicResponse: {
                UpdateTopicResponseMessage updateTopicResponseMessage = (UpdateTopicResponseMessage) message;
                nextState = processUpdateTopicResponseMessage(updateTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    public State processGetUserResponseMessage(GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() != Results.Success) {
            UpdateTopicResponseMessage updateTopicResponseMessage = new UpdateTopicResponseMessage(getUpdateTopicOperation().getQueue(),
                    this, getUserResponseMessage.getResult());

            send(getUpdateTopicOperation().getRequester(), updateTopicResponseMessage);

            return StopState.getInstance();
        }

        if (getUpdateTopicOperation().getSession().getUser().getName().equals(getUpdateTopicOperation().getTopic().getOwner())
                || getUpdateTopicOperation().getSession().getUser().getCategory() == User.UserTypes.Admin) {
            Miranda.getInstance().getTopicManager().sendUpdateTopicMessage(getUpdateTopicOperation().getQueue(),
                    this, getUpdateTopicOperation().getTopic());
        } else {
            UpdateTopicResponseMessage updateTopicResponseMessage = new UpdateTopicResponseMessage(getUpdateTopicOperation().getQueue(),
                    this, Results.NotOwner);

            send(getUpdateTopicOperation().getRequester(), updateTopicResponseMessage);

            return StopState.getInstance();
        }

        return getUpdateTopicOperation().getCurrentState();
    }

    public State processUpdateTopicResponseMessage(UpdateTopicResponseMessage updateTopicResponseMessage) {
        UpdateTopicResponseMessage responseMessage = new UpdateTopicResponseMessage(getUpdateTopicOperation().getQueue(),
                this, updateTopicResponseMessage.getResult());

        send(getUpdateTopicOperation().getRequester(), responseMessage);

        return StopState.getInstance();
    }
}
