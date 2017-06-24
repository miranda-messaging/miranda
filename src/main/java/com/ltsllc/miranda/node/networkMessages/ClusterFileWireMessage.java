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
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.node.NodeElement;

import java.io.IOException;
import java.util.List;

/**
 * Created by Clark on 2/8/2017.
 */
public class ClusterFileWireMessage extends WireMessage {
    private static Gson ourGson = new Gson();

    private String content;
    private Version version;

    public String getContent() {
        return content;
    }

    public Version getVersion() {
        return version;
    }

    public ClusterFileWireMessage (byte[] file, Version version) {
        super(WireSubjects.ClusterFile);

        this.content = Utils.bytesToString(file);
        this.version = version;
    }

    public ClusterFileWireMessage (List<NodeElement> file, Version version) {
        super(WireSubjects.ClusterFile);

        String json = ourGson.toJson(file);
        byte[] buffer = json.getBytes();

        this.content = Utils.bytesToString(buffer);
        this.version = version;
    }

    public byte[] getContentAsBytes() throws IOException {
        return Utils.hexStringToBytes(content);
    }
}
