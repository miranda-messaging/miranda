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

package com.ltsllc.miranda;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 3/28/2017.
 */
public class TestSubsystem extends TestCase {
    private Subsystem subsystem;

    public Subsystem getSubsystem() {
        return subsystem;
    }

    public void reset () throws Exception {
        super.reset();

        subsystem = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        subsystem = new Consumer("test");
    }

    @Test
    public void testConstructor () {
        assert (getSubsystem().getQueue() != null);
    }
}
