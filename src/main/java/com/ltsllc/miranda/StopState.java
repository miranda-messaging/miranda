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

/**
 * A stop state for a Miranda Subsystem.
 *
 * This class represents that the owner has reached a point where it
 * should be shut down.
 *
 * Created by Clark on 12/31/2016.
 */
public class StopState extends State {
    public static void initializeClass () throws MirandaException {
        ourInstance = new StopState();
    }

    private static StopState ourInstance;

    public static StopState getInstance () {
        return ourInstance;
    }

    public StopState() throws MirandaException {
        super(null);
    }

    public State processMessage () {
        throw new IllegalStateException();
    }
}
