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

package com.ltsllc.miranda.servlet.status;

import com.ltsllc.miranda.clientinterface.objects.StatusObject;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.servlet.miranda.MirandaStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Clark on 3/4/2017.
 */
public class StatusServlet extends MirandaServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        MirandaStatus mirandaStatus = MirandaStatus.getInstance();
        StatusObject statusObject = mirandaStatus.getStatus();

        String json = getGson().toJson(statusObject);
        response.getOutputStream().print(json);
    }
}
