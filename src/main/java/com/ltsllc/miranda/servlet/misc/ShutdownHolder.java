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

package com.ltsllc.miranda.servlet.misc;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;

import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 5/1/2017.
 */
public class ShutdownHolder extends ServletHolder {
    private static ShutdownHolder ourInstance;

    public static synchronized void initialize(long timeout) throws MirandaException {
        ourInstance = new ShutdownHolder(timeout);
    }

    public static ShutdownHolder getInstance() {
        return ourInstance;
    }

    public ShutdownHolder(long timeout) throws MirandaException {
        super("shutdown holder", timeout);
    }

    public void shutdownMirada() throws TimeoutException {
        Miranda.getInstance().sendShutdown(getQueue(), this);

        sleep();
    }
}
