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

package com.ltsllc.miranda.miranda.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.miranda.messages.DecrementPanicCountMessage;

/**
 * Created by Clark on 3/8/2017.
 */
public class PanicPolicyReadyState extends State {
    public PanicPolicyReadyState(MirandaPanicPolicy panicPolicy) throws MirandaException {
        super(panicPolicy);
    }

    public MirandaPanicPolicy getMirandaPanicPolicy() {
        return (MirandaPanicPolicy) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case DecrementPanicCount: {
                DecrementPanicCountMessage decrementPanicCountMessage = (DecrementPanicCountMessage) message;
                nextState = processDecrementPanicCountMessage(decrementPanicCountMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processDecrementPanicCountMessage(DecrementPanicCountMessage message) {
        getMirandaPanicPolicy().decrementPanicCount();

        return this;
    }
}
