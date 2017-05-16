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

package com.ltsllc.miranda.network;

/**
 * Created by Clark on 2/7/2017.
 */

import com.google.gson.Gson;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that knows how to split out Json messages.
 */
public class JsonParser {
    private static Logger logger = Logger.getLogger(JsonParser.class);
    private static Gson ourGson = new Gson();

    private List<WireMessage> messages = new ArrayList<WireMessage>();

    public List<WireMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<WireMessage> messages) {
        this.messages = messages;
    }

    public JsonParser (String json) {
        String[] fields = json.split("\n");
        parse(fields);
    }

    private void parse (String[] fields) {
        for (String s : fields) {
            WireMessage wireMessage = ourGson.fromJson(s, WireMessage.class);

            try {
                Class clazz = getClass().forName(wireMessage.getClassName());
                wireMessage = (WireMessage) ourGson.fromJson(s, clazz);
                getMessages().add(wireMessage);
            } catch (ClassNotFoundException e) {
                Panic panic = new Panic("Exception finding class", e, Panic.Reasons.ExceptionPaesingJson);
                Miranda.getInstance().panic(panic);
            }
        }
    }
}
