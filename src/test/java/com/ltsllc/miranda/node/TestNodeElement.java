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

package com.ltsllc.miranda.node;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.ImprovedRandom;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;

/**
 * Created by Clark on 4/15/2017.
 */
public class TestNodeElement extends TestCase {
    private NodeElement nodeElement;

    public NodeElement getNodeElement() {
        return nodeElement;
    }

    public void reset () {
        super.reset();

        nodeElement = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        nodeElement = new NodeElement("whatever.com", "192.168.1.1", 6789, "a test node");
    }

    @Test
    public void testEquivalent () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        NodeElement other = NodeElement.random(improvedRandom);

        assert (getNodeElement().equivalent(getNodeElement()));
        assert (!getNodeElement().equivalent(other));
    }

    @Test
    public void testUpdateFrom () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        NodeElement other = NodeElement.random(improvedRandom);

        getNodeElement().updateFrom(other);

        assert (getNodeElement().getIp().equals(other.getIp()));
        assert (getNodeElement().getDescription().equals(other.getDescription()));
        assert (getNodeElement().getLastConnected() == other.getLastConnected());
    }

    @Test
    public void testMatches () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        NodeElement other = NodeElement.random(improvedRandom);

        assert (getNodeElement().matches(getNodeElement()));
        assert (!getNodeElement().matches(other));
    }
}
