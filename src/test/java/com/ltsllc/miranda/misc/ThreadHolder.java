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

package com.ltsllc.miranda.misc;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 5/15/2017.
 */
abstract public class ThreadHolder extends Consumer {
    abstract MirandaThread createThread ();

    private MirandaThread mirandaThread;

    public ThreadHolder (String name) {
        super(name);
    }

    public MirandaThread getMirandaThread() {
        return mirandaThread;
    }

    public void setMirandaThread(MirandaThread mirandaThread) throws MirandaException {
        this.mirandaThread = mirandaThread;

        ThreadHolderReadyState threadHolderReadyState = new ThreadHolderReadyState(this);
        setCurrentState(threadHolderReadyState);
    }

    public void start () {
        mirandaThread = createThread();
        mirandaThread.start();
    }

    public void stop () {
        getMirandaThread().stop();

        super.stop();
    }
}
