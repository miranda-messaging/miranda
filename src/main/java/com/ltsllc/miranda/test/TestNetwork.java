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

package com.ltsllc.miranda.test;

import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.ConnectToMessage;

/**
 * A class intended to test the {@link Network} class.
 */
public class TestNetwork extends Network {
    private Handle testHandle;
    private Throwable throwable;
    private NetworkException basicConnectException;

    public Handle getTestHandle() {
        return testHandle;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setTestHandle(Handle testHandle) {
        this.testHandle = testHandle;
    }

    public NetworkException getBasicConnectException() {
        return basicConnectException;
    }

    public void setBasicConnectException(NetworkException basicConnectException) {
        this.basicConnectException = basicConnectException;
    }

    public Handle createHandle (Object o) {
        return getTestHandle();
    }

    public Handle basicConnectTo (ConnectToMessage connectToMessage) throws NetworkException {
        Exception e = new Exception();
        e.fillInStackTrace();
        setThrowable(e);

        if (null != getBasicConnectException())
            throw getBasicConnectException();
        else
            return getTestHandle();
    }

    public boolean verifyCall () {
        return null != getThrowable();
    }

}
