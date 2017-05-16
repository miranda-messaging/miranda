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

/**
 * Created by Clark on 2/19/2017.
 */

import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Updateable;

/**
 * Objects like users and topics are not deleted. Instead, they are marked as deleted and garbage collected.
 */
public class StatusObject<E extends StatusObject> implements Matchable<E>, Updateable<E> {
    public enum Status {
        New,
        Deleted
    }

    private Status status;
    private long modified;

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public StatusObject (Status status) {
        this.status = status;
    }

    public StatusObject(Status status, long modified) {
        this.status = status;
        this.modified = modified;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public boolean expired() {
        return this.status == Status.Deleted;
    }

    public boolean expired(long time) {
        return expired();
    }

    public void updateFrom (StatusObject other) {
        setStatus(other.getStatus());
    }

    public boolean matches (E other) {
        return getStatus() == other.getStatus() && getModified() == other.getModified();
    }
}
