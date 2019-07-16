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

package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.MirandaObject;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;

import java.io.IOException;

/**
 * Created by Clark on 5/14/2017.
 */
abstract public class StandardManager<E extends MirandaObject> extends Manager<E, E> {
    private Version version;
    private StandardManager manager;

    public StandardManager getManager() {
        return manager;
    }

    public void setManager(StandardManager manager) {
        this.manager = manager;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public StandardManager(String name, String filename) throws IOException, MirandaException {
        super(name, filename);
    }

    public E convert(E e) {
        return e;
    }

    abstract public State getReadyState () throws MirandaException;

}
