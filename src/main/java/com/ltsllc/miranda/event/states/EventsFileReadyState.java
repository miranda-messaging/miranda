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

package com.ltsllc.miranda.event.states;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.event.EventsFile;
import com.ltsllc.miranda.file.states.SingleFileReadyState;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/19/2017.
 */
public class EventsFileReadyState extends SingleFileReadyState {
    public static final String NAME = "events file";

    public Type getListType() {
        return new TypeToken<List<Event>>() {
        }.getType();
    }

    public String getName() {
        return NAME;
    }

    public EventsFileReadyState(EventsFile eventsFile) throws MirandaException {
        super(eventsFile);
    }
}
