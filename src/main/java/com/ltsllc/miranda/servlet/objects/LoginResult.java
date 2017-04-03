package com.ltsllc.miranda.servlet.objects;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginResult {
    public enum LoginResults {
        Success,
        Failure
    }

    private LoginResults result;
    private String session;

    public LoginResults getResult() {
        return result;
    }

    public String getSession() {
        return session;
    }

    public void setSession (String session) {
        this.session = session;
    }

    public LoginResult (LoginResults result) {
        this.result = result;
    }

    public LoginResult (LoginResults result, String session) {
        this.result = result;
        this.session = session;
    }
}
