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

package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.session.Session;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginHolder extends ServletHolder {
    public static class LoginResult {
        public Results result;
        public Session session;

        public LoginResult(Results result, Session session) {
            this.result = result;
            this.session = session;
        }

        public LoginResult() {
        }
    }

    private static LoginHolder ourInstance;
    private static Logger logger = Logger.getLogger(LoginHolder.class);

    public static LoginHolder getInstance() {
        return ourInstance;
    }

    public static void initialize(long timeout) throws MirandaException {
        ourInstance = new LoginHolder(timeout);
    }

    private Session session;
    private Results result;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public LoginHolder(long timeout) throws MirandaException {
        super("login", timeout);

        LoginHolderReadyState readyState = new LoginHolderReadyState(this);
        setCurrentState(readyState);
    }

    public LoginResult login(String name) throws TimeoutException {
        setResult(Results.Unknown);
        setSession(null);

        Miranda.getInstance().sendLoginMessage(getQueue(), this, name);

        sleep();

        LoginResult loginResult = new LoginResult(getResult(), getSession());
        return loginResult;
    }

    public void setResultAndWakeup(LoginResult loginResult) {
        setResult(loginResult.result);
        setSession(loginResult.session);
        wake();
    }

}
