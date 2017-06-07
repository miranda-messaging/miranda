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
import com.ltsllc.miranda.servlet.ListObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.topics.Topic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/9/2017.
 */
public class GetTopicsServlet extends TopicServlet {
    public boolean allowAccess() {
        return true;
    }

    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public ListObject basicPerformService(HttpServletRequest req, HttpServletResponse resp, TopicRequestObject requestObject)
            throws ServletException, IOException, TimeoutException {
        ListObject result = new ListObject();
        List<Topic> topics = TopicHolder.getInstance().getTopicList();
        result.setResult(Results.Success);
        result.setList(topics);

        return result;
    }
}
