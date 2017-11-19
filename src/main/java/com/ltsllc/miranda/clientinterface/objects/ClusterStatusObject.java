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

package com.ltsllc.miranda.clientinterface.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStatusObject {
    private List<NodeStatus> nodes = new ArrayList<NodeStatus>();

    public List<NodeStatus> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeStatus> nodes) {
        this.nodes = nodes;
    }

    public ClusterStatusObject(List<NodeStatus> nodes) {
        this.nodes = new ArrayList<NodeStatus>(nodes);
    }
}
