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

package com.ltsllc.miranda.servlet.user.servlet;

import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.ReadObject;
import com.ltsllc.miranda.clientinterface.requests.UserRequest;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.servlet.user.UserHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/10/2017.
 */
public class ReadUserServlet extends UserServlet {
    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public ReadObject basicService(HttpServletRequest request, HttpServletResponse response,
                                   UserRequest requestObject) throws ServletException, IOException, TimeoutException {
        UserHolder.getInstance().getUser(requestObject.getUser());
        ReadObject<User> readObject = new ReadObject<User>();
        readObject.setResult(UserHolder.getInstance().getGetUserResults());
        readObject.setObject(UserHolder.getInstance().getUser());

        return readObject;
    }
}
