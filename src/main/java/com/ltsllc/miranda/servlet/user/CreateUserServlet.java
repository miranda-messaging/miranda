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

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.UserRequest;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateUserServlet extends MirandaServlet {
    public ResultObject createResultObject() {
        return new ResultObject();
    }

    public ResultObject basicService(HttpServletRequest req, HttpServletResponse resp, UserObjectRequest requestObject)
            throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();

        try {
            User user = requestObject.getUser();
            User newUser = new User(user.getName(), user.getCategory(), user.getDescription(), user.getPublicKeyPem());
            Results result = UserHolder.getInstance().createUser(newUser);
            resultObject.setResult(result);
        } catch (EncryptionException e) {
            resultObject.setResult(Results.Exception);
        }

        return resultObject;
    }
}
