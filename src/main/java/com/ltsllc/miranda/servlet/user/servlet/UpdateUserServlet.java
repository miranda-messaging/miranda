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

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.MergeException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.session.SessionServlet;
import com.ltsllc.miranda.servlet.user.request.UpdateUserRequest;
import com.ltsllc.miranda.user.UnknownUserException;
import com.ltsllc.miranda.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class UpdateUserServlet extends SessionServlet {


    @Override
    public Class<? extends Request> getRequestClass() {
        return UpdateUserRequest.class;
    }

    @Override
    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, Request requestObject) throws ServletException, IOException, TimeoutException {
        ResultObject resultObject = new ResultObject();
        try {
            Miranda miranda = Miranda.getInstance();
            UserManager userManager = miranda.getUserManager();

            UpdateUserRequest updateUserRequest = (UpdateUserRequest) requestObject;
            if (updateUserRequest.getUser().getName() == null || updateUserRequest.getUser().getName().trim().isEmpty()) {
                resultObject.setResult(Results.MissingData);
            }

            if (updateUserRequest.getUser().getPublicKeyPem() == null || updateUserRequest.getUser().getPublicKeyPem().trim().isEmpty()) {
                resultObject.setResult(Results.MissingData);
            }

            userManager.updateUser(updateUserRequest.getUser());
        } catch (UnknownUserException e) {
            resultObject.setResult(Results.UserNotFound);
        } catch (MergeException|EncryptionException e) {
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        }

        return resultObject;
    }

    @Override
    public ResultObject createResultObject() {
        return new ResultObject();
    }
}
