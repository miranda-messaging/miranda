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

package com.ltsllc.miranda.servlet.status;

import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.servlet.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 3/4/2017.
 */
public class StatusObject {
    private List<NodeElement> cluster = new ArrayList<NodeElement>();
    private List<Property> properties = new ArrayList<Property>();
    private NodeElement local;

    public List<NodeElement> getCluster() {
        return cluster;
    }

    public void setCluster(List<NodeElement> cluster) {
        this.cluster = cluster;
    }

    public NodeElement getLocal() {
        return local;
    }

    public StatusObject (NodeElement local, List<Property> properties, List<NodeElement> cluster) {
        this.local = local;
        this.properties = properties;
        this.cluster = cluster;
    }
}
