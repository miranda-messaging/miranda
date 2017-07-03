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

import com.ltsllc.miranda.node.Node;

/**
 * Created by Clark on 2/6/2017.
 */
public class JoinWireMessage extends WireMessage {
    private String dns;
    private int port;
    private String description;

    public JoinWireMessage (String dns, String ip, int port, String description) {
        super(WireSubjects.Join);

        this.dns = dns;
        this.port = port;
        this.description = description;
    }

    public JoinWireMessage (Node node) {
        super(WireSubjects.Join);

        this.dns = node.getDns();
        this.port = node.getPort();
        this.description = node.getDescription();
    }

    public String getDns() {
        return dns;
    }

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }

    public boolean equals (Object o) {
        if (!super.equals(o) || !(o instanceof JoinWireMessage))
            return false;

        JoinWireMessage other = (JoinWireMessage) o;

        return getDns().equals(other.getDns())
                && getPort() == other.getPort()
                && getDescription() == other.getDescription() || getDescription().equals(other.getDescription());
    }
}
