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

package com.ltsllc.miranda.node.networkMessages;

/**
 * Created by Clark on 3/11/2017.
 */
public class JoinResponseWireMessage extends WireMessage {
    public enum Responses {
        Success,
        Failure
    }

    private Responses result;

    public Responses getResult() {
        return result;
    }

    public JoinResponseWireMessage(Responses result) {
        super(WireSubjects.JoinResponse);

        this.result = result;
    }

    public boolean equals(Object o) {
        if (null == o || !(o instanceof JoinResponseWireMessage))
            return false;

        JoinResponseWireMessage other = (JoinResponseWireMessage) o;
        return getResult().equals(other.getResult());
    }

    public String toString() {
        String s = "joinResponse{" + getResult() + "}";
        return s;
    }
}
