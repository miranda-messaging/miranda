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

package com.ltsllc.miranda.servlet.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.objects.StatusObject;
import com.ltsllc.miranda.miranda.Miranda;

/**
 * Created by Clark on 3/9/2017.
 */
public class MirandaStatus extends Consumer {
    private static MirandaStatus ourInstance;

    private StatusObject statusObject;

    public StatusObject getStatusObject() {
        return statusObject;
    }

    public void setStatusObject(StatusObject statusObject) {
        this.statusObject = statusObject;
    }

    public static synchronized void initialize() throws MirandaException {
        if (null == ourInstance) {
            ourInstance = new MirandaStatus();
        }
    }

    public static MirandaStatus getInstance() {
        return ourInstance;
    }

    private MirandaStatus() throws MirandaException {
        super("miranda status");

        MirandaStatusReadyState mirandaStatusReadyState = new MirandaStatusReadyState(this);
        setCurrentState(mirandaStatusReadyState);
    }

    public void receivedStatus(StatusObject statusObject) {
        setStatusObject(statusObject);

        synchronized (this) {
            notifyAll();
        }
    }

    public StatusObject getStatus() {
        StatusObject statusObject = null;

        try {
            //
            // so that we get a fresh status
            //
            setStatusObject(null);
            Miranda.getInstance().getStatus(getQueue());

            synchronized (this) {
                wait(1000);
            }

            statusObject = getStatusObject();
        } catch (InterruptedException e) {
            //
            // Ignore if we are interrupted
            //
        }

        return statusObject;
    }
}
