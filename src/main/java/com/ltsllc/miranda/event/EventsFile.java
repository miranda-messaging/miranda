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

package com.ltsllc.miranda.event;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/19/2017.
 */
public class EventsFile extends SingleFile<Event> {
    public EventsFile (String filename, Reader reader, Writer writer) throws IOException, MirandaException {
        super(filename, reader, writer);

        setCurrentState(new EventsFileLoadingState(this));
    }

    public Type getListType() {
        return new TypeToken<List<Event>>(){}.getType();
    }

    public List buildEmptyList() {
        return new ArrayList<Event>();
    }

    public void checkForDuplicates () {}

}
