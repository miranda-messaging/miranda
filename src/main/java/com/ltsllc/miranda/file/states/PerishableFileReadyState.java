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

package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ExpiredMessage;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.PerishableFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Clark on 2/26/2017.
 */
public class PerishableFileReadyState extends State {
    private static Logger logger = Logger.getLogger(PerishableFile.class);

    public PerishableFileReadyState (PerishableFile perishableFile) {
        super(perishableFile);
    }

    public PerishableFile getFile () {
        return (PerishableFile) getContainer();
    }

    public List<Perishable> getPerishables () {
        return getFile().getData();
    }

    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        boolean changed = false;

        Set<Perishable> expired = new HashSet<Perishable>();
        long now = System.currentTimeMillis();

        for (Perishable perishable : getPerishables()) {
            if (perishable.expired(now)) {
                expired.add(perishable);
                changed = true;
            }
        }

        logger.info (getFile().getFilename() + " expiring " + expired);

        getPerishables().removeAll(expired);

        if (changed)
            notifyContainer (expired);

        return this;
    }

    private void notifyContainer(Set<Perishable> expired) {
        ExpiredMessage expiredMessage = new ExpiredMessage (getContainer().getQueue(), this, expired);
        send(Miranda.getInstance().getCluster().getQueue(), expiredMessage);
    }
}
