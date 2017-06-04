package com.ltsllc.miranda;

/**
 * Created by Clark on 6/2/2017.
 */
public class EncryptedMessage {
    private String key;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
