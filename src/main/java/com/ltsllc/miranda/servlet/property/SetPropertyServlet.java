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

package com.ltsllc.miranda.servlet.property;

import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Clark on 3/13/2017.
 */
public class SetPropertyServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(SetPropertyServlet.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, String> newValues = new HashMap<String, String>();

        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String newValue = request.getParameter(name);
            String oldValue = Miranda.properties.getProperty(name);

            if (!newValue.equals(oldValue)) {
                newValues.put(name, newValue);
            }
        }

        logger.info("new values:");
        for (String name : newValues.keySet()) {
            String value = newValues.get(name);
            logger.info(name + " = " + value);
        }

        if (newValues.size() > 0) {
            for (String name : newValues.keySet()) {
                String value = newValues.get(name);
                Miranda.properties.setProperty(name, value);
            }

            Miranda.properties.write();
        }
    }

}
