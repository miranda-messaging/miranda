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
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.DoneSynchronizingMessage;
import com.ltsllc.miranda.miranda.messages.GetVersionsMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */

/**
 * Represents that Miranda is trying to create a versions wire message.
 */
public class GettingVersionsState extends State {
    private static Logger logger = Logger.getLogger(GettingVersionsState.class);

    private Miranda miranda;
    private Set<String> filesOutstanding = new HashSet<String>();
    private BlockingQueue<Message> node;
    private Map<String, Version> versions = new HashMap<String, Version>();

    public GettingVersionsState(Miranda miranda, BlockingQueue<Message> node) throws MirandaException {
        super(miranda);

        getFilesOutstanding().add("cluster");
        getFilesOutstanding().add("users");
        getFilesOutstanding().add("topics");
        getFilesOutstanding().add("subscriptions");

        this.miranda = miranda;
        this.node = node;
    }

    public Miranda getMiranda() {
        return miranda;
    }

    public Set<String> getFilesOutstanding() {
        return filesOutstanding;
    }

    public BlockingQueue<Message> getNode() {
        return node;
    }

    public Map<String, Version> getVersions() {
        return versions;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case DoneSynchronizing: {
                DoneSynchronizingMessage doneSynchronizingMessage = (DoneSynchronizingMessage) message;
                nextState = processDoneSynchronizingMessage(doneSynchronizingMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case GetVersions: {
                GetVersionsMessage getVersionsMessage = (GetVersionsMessage) message;
                nextState = processGetVersionsMessage(getVersionsMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
        }

        return nextState;
    }


    private State processDoneSynchronizingMessage(DoneSynchronizingMessage doneSynchronizingMessage) throws MirandaException {
        State nextState = this;

        if (doneSynchronizingMessage.getSender() == ClusterFile.getInstance().getQueue()) {
            getFilesOutstanding().remove("cluster");
        } else if (doneSynchronizingMessage.getSender() == UsersFile.getInstance().getQueue()) {
            getFilesOutstanding().remove("users");
        } else if (doneSynchronizingMessage.getSender() == TopicsFile.getInstance().getQueue()) {
            getFilesOutstanding().remove("topics");
        } else if (doneSynchronizingMessage.getSender() == SubscriptionsFile.getInstance()) {
            getFilesOutstanding().remove("subscriptions");
        }

        if (getFilesOutstanding().size() <= 0) {
            nextState = new ReadyState(getMiranda());
        }

        return nextState;
    }


    private State processVersionMessage(VersionMessage versionMessage) throws MirandaException {
        Set<String> drop = new HashSet<String>();

        for (String name : getFilesOutstanding()) {
            if (name.equalsIgnoreCase(versionMessage.getNameVersion().getName()))
                drop.add(name);
        }

        getFilesOutstanding().removeAll(drop);

        getVersions().put(versionMessage.getNameVersion().getName(), versionMessage.getNameVersion().getVersion());

        if (getFilesOutstanding().size() <= 0) {
            List versions = mapToList(getVersions());
            VersionsMessage versionsMessage = new VersionsMessage(getMiranda().getQueue(), this, versions);
            send(getNode(), versionsMessage);
            ReadyState nodeReadyState = new ReadyState(getMiranda());

            return nodeReadyState;
        }

        return this;
    }


    private List<NameVersion> mapToList(Map<String, Version> map) {
        List<NameVersion> list = new ArrayList<NameVersion>();

        for (String name : map.keySet()) {
            Version version = map.get(name);
            NameVersion nameVersion = new NameVersion(name, version);
            list.add(nameVersion);
        }

        return list;
    }


    private State processGetVersionsMessage(GetVersionsMessage getVersionsMessage) throws MirandaException {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this, getNode());

        send(ClusterFile.getInstance().getQueue(), getVersionMessage);
        send(UsersFile.getInstance().getQueue(), getVersionMessage);
        send(TopicsFile.getInstance().getQueue(), getVersionMessage);
        send(SubscriptionsFile.getInstance().getQueue(), getVersionMessage);

        GettingVersionsState gettingVersionsState = new GettingVersionsState(getMiranda(), getVersionMessage.getSender());
        return gettingVersionsState;
    }
}
