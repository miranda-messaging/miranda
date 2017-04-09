package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.objects.LoginObject;
import com.ltsllc.miranda.servlet.states.LoginHolderReadyState;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginHolder extends ServletHolder {
    private static LoginHolder ourInstance;
    private static Logger logger = Logger.getLogger(LoginHolder.class);

    private long timeoutPeriod;
    private long newSession;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getNewSession() {
        return newSession;
    }

    public void setNewSession(long newSession) {
        this.newSession = newSession;
    }

    public long getTimeoutPeriod() {
        return timeoutPeriod;
    }

    public void setTimeoutPeriod(long timeoutPeriod) {
        this.timeoutPeriod = timeoutPeriod;
    }

    public static LoginHolder getInstnace() {
        return ourInstance;
    }

    public static synchronized void initialize(long timeout) {
        if (null == ourInstance) {
            ourInstance = new LoginHolder(timeout);
            long timeoutPeriod = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_SESSION_LOGIN_TIMEOUT, MirandaProperties.DEFAULT_SESSION_LOGIN_TIMEOUT);
            ourInstance.setTimeoutPeriod(timeoutPeriod);
        }
    }

    public LoginHolder (long timeout) {
        super("login", timeout);

        LoginHolderReadyState readyState = new LoginHolderReadyState(this);
        setCurrentState(readyState);
    }

    public long login(LoginObject loginObject) {
        String sha1 = Utils.calculateSha1LogExceptions(loginObject.getPassword());
        loginObject.setSha1(sha1);
        loginObject.setPassword(null);
        setNewSession(-1);

        Miranda.getInstance().getSessionManager().sendCreateSession(getQueue(), this, loginObject.getName());
        Miranda.getInstance().getUserManager().sendGetUser(getQueue(), this, loginObject.getName());

        waitFor(getTimeoutPeriod());

        return getNewSession();
    }

    public void setSessionAndWakeup (long session) {
        setNewSession(session);
        wake();
    }
}
