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
