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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.messages.DeleteTopicResponseMessage;

/**
 * Created by Clark on 4/23/2017.
 */
public class DeleteTopicOperationReadyState extends State {
    public DeleteTopicOperation getDeleteTopicOperation() {
        return (DeleteTopicOperation) getContainer();
    }

    public DeleteTopicOperationReadyState(DeleteTopicOperation deleteTopicOperation) throws MirandaException {
        super(deleteTopicOperation);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getDeleteTopicOperation().getCurrentState();

        switch (message.getSubject()) {
            case DeleteTopicResponse: {
                DeleteTopicResponseMessage deleteTopicResponseMessage = (DeleteTopicResponseMessage) message;
                nextState = processDeleteTopicResponseMessage(deleteTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processDeleteTopicResponseMessage(DeleteTopicResponseMessage deleteTopicResponseMessage) {
        DeleteTopicResponseMessage response = new DeleteTopicResponseMessage(getDeleteTopicOperation().getQueue(),
                this, deleteTopicResponseMessage.getResult());

        send(getDeleteTopicOperation().getRequester(), response);

        return StopState.getInstance();
    }

    public State start() {
        super.start();

        Miranda.getInstance().getTopicManager().sendDeleteTopicMessage(getDeleteTopicOperation().getQueue(), this,
                getDeleteTopicOperation().getTopicName());

        return getDeleteTopicOperation().getCurrentState();

    }
}
