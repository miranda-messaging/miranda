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

import com.google.gson.Gson;

/**
 * Created by Clark on 1/29/2017.
 */
public class WireMessage {
    private static Gson ourGson = new Gson();

    public enum WireSubjects {
        ClusterFile,
        DeleteSubscription,
        DeleteTopic,
        DeleteUser,
        ExpiredSessions,
        GetClusterFile,
        GetFile,
        GetFileResponse,
        GetMessages,
        GetDeliveries,
        GetSubscriptionsFile,
        GetTopicsFile,
        GetUsersFile,
        GetVersions,
        Join,
        JoinResponse,
        Misc,
        NewEvent,
        NewEventResponse,
        NewSession,
        NewSubscription,
        NewTopic,
        NewUser,
        ShuttingDown,
        Stop,
        Stopping,
        StopResponse,
        UpdateSubscription,
        UpdateTopic,
        UpdateUser,
        Versions,
        Version
    }

    private WireSubjects subject;
    private String className;

    public WireSubjects getWireSubject() {
        return subject;
    }

    public String getClassName() {
        return className;
    }

    public WireMessage (WireSubjects subject) {
        this.subject = subject;
        this.className = getClass().getCanonicalName();
    }

    public String getJson () {
        return ourGson.toJson(this);
    }

    public String toJson () {
        return getJson();
    }

    public boolean equals (Object o) {
        if (o == null || !(o instanceof WireMessage))
            return false;

        WireMessage other = (WireMessage) o;
        return getWireSubject().equals(other.getWireSubject())
                && getClassName().equals(other.getClassName());
    }
}
