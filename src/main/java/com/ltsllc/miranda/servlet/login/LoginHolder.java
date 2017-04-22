package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.holder.ServletHolder;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginHolder extends ServletHolder {
    public static class LoginResult {
        public Results result;
        public Session session;

        public LoginResult (Results result, Session session) {
            this.result = result;
            this.session = session;
        }

        public LoginResult () {}
    }

    private static LoginHolder ourInstance;
    private static Logger logger = Logger.getLogger(LoginHolder.class);

    public static LoginHolder getInstance () {
        return ourInstance;
    }

    public static void initialize (long timeout) {
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

    public LoginHolder (long timeout) {
        super("login", timeout);

        LoginHolderReadyState readyState = new LoginHolderReadyState(this);
        setCurrentState(readyState);
    }

    public LoginResult login(String name) throws TimeoutException {
        setResult (Results.Unknown);
        setSession(null);

        Miranda.getInstance().sendLoginMessage (getQueue(), this, name);

        sleep();

        LoginResult loginResult = new LoginResult(getResult(), getSession());
        return loginResult;
    }

    public void setResultAndWakeup (LoginResult loginResult) {
        setResult(loginResult.result);
        setSession(loginResult.session);
        wake();
    }

}
