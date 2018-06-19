package com.ltsllc.miranda.servlet.login;

/**
 * What the login servlet sends the user when they try to login.
 */
public class Challenge {
    private String encryptedSession;

    public String getEncryptedSession() {
        return encryptedSession;
    }

    public void setEncryptedSession(String encryptedSession) {
        this.encryptedSession = encryptedSession;
    }
}
