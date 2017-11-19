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

package com.ltsllc.miranda.test;

import com.google.gson.Gson;
import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 3/14/2017.
 */
public class NodeElementFileCreator implements FileCreator {

    private ImprovedRandom random;
    private int maxNumberOfNodes;

    public ImprovedRandom getRandom() {
        return random;
    }

    public int getMaxNumberOfNodes() {
        return maxNumberOfNodes;
    }

    public NodeElementFileCreator(ImprovedRandom random, int maxNumberOfNodes) {
        this.random = random;
        this.maxNumberOfNodes = maxNumberOfNodes;
    }

    public boolean createFile(File file) {
        int numberOfNodes = 1 + getRandom().nextInt(getMaxNumberOfNodes());
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        for (int i = 0; i < numberOfNodes; i++) {
            nodeElementList.add(NodeElement.random(random));
        }

        Gson gson = new Gson();
        String json = gson.toJson(nodeElementList);
        FileWriter out = null;
        boolean result;

        try {
            out = new FileWriter(file);
            out.write(json);

            result = true;
        } catch (IOException e) {
            result = false;
        } finally {
            Utils.closeIgnoreExceptions(out);
        }

        return result;
    }

}
