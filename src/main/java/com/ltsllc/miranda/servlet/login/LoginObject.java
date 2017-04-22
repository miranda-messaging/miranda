package com.ltsllc.miranda.servlet.login;

/**
 * Created by Clark on 3/31/2017.
 */
public class LoginObject {
    private String name;
    private String password;
    private String sha1;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName () {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public LoginObject () {}

    public LoginObject (String name) {
        this.name = name;
    }
}
