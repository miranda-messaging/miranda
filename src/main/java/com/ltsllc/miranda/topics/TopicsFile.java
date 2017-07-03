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

package com.ltsllc.miranda.topics;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.topics.states.TopicsFileStartingState;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/5/2017.
 */
public class TopicsFile extends SingleFile<Topic> {
    public static final String FILE_NAME = "topics";

    private static Logger logger = Logger.getLogger(TopicsFile.class);

    private static TopicsFile ourInstance;

    public static TopicsFile getInstance() {
        return ourInstance;
    }

    public static synchronized void initialize (String filename, Reader reader, Writer writer) throws IOException {
        if (null == ourInstance) {
            ourInstance = new TopicsFile(filename, reader, writer);
            ourInstance.start();
            ourInstance.load();
        }
    }

    public static void setInstance (TopicsFile topicsFile) {
        ourInstance = topicsFile;
    }

    public TopicsFile(String filename, Reader reader, Writer writer) throws IOException {
        super(filename, reader, writer);

        TopicsFileStartingState topicsFileStartingState = new TopicsFileStartingState(this);
        setCurrentState(topicsFileStartingState);
    }

    public TopicsFile () throws IOException {
        super(null, null, null);
    }

    public Type getBasicType ()
    {
        return new TypeToken<ArrayList<User>>() {}.getType();
    }

    public List buildEmptyList () {
        return new ArrayList<Topic> ();
    }

    public Type getListType() {
        return new TypeToken<ArrayList<Topic>>(){}.getType();
    }

    public void checkForDuplicates () {
        List<Topic> topicList = new ArrayList<Topic>(getData());
        List<Topic> duplicates = new ArrayList<Topic>();

        for (Topic current : topicList) {
            for (Topic topic : getData())
            {
                if (current.getName().equals(topic.getName()) && current != topic)
                {
                    logger.info ("Removing duplicate topic named " + topic.getName());
                    duplicates.add(current);
                }
            }
        }

        getData().removeAll(duplicates);
    }

    public Topic find (Topic topic) {
        for (Topic candidate : getData()) {
            if (candidate.getName().equals(topic.getName()))
                return candidate;
        }

        return null;
    }
}
