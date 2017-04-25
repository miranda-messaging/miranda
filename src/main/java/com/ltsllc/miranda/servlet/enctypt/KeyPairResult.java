package com.ltsllc.miranda.servlet.enctypt;

import com.ltsllc.miranda.servlet.objects.ResultObject;

/**
 * Created by Clark on 4/25/2017.
 */
public class KeyPairResult extends ResultObject {
    private String publicKey;
    private String privateKey;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {

        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
