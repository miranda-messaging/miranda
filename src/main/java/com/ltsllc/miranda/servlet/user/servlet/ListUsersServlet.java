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

package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.ListObject;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.requests.UserRequest;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.session.SessionServlet;
import com.ltsllc.miranda.user.messages.ListUsersMessage;
import com.ltsllc.miranda.user.messages.ListUsersResponseMessage;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class ListUsersServlet extends SessionServlet {
    public ResultObject createResultObject() {
        return new UserListResultObject();
    }


    @Override
    public Class<? extends Request> getRequestClass() {
        return BasicRequest.class;
    }

    @Override
    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, Request requestObject) throws ServletException, IOException, TimeoutException {
        Miranda.getInstance().getUserManager().sendGetUsers(getQueue(), this);
        ListUsersResponseMessage listUsersResponseMessage = (ListUsersResponseMessage) waitForReply(1000, ListUsersResponseMessage.class);

        UserListResultObject userListResultObject = new UserListResultObject();

        userListResultObject.setResult(Results.Success);
        userListResultObject.setUserList(listUsersResponseMessage.getUsers());

        return userListResultObject;
    }
}
