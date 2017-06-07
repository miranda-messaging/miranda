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

package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.ReadObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.topics.Topic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class GetTopicServlet extends TopicServlet {
    public ResultObject createResultObject () {
        return new TopicResultObject();
    }

    public ResultObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, TopicRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        ReadObject readObject = new ReadObject();

        Topic topic = TopicHolder.getInstance().getTopic(requestObject.getTopic().getName());
        if (topic == null) {
            readObject.setResult(Results.TopicNotFound);
        } else {
            readObject.setResult(Results.Success);
            readObject.setObject(topic);
        }

        return readObject;
    }
}
