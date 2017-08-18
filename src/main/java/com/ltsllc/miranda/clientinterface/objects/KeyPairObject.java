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

package com.ltsllc.miranda.clientinterface.objects;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by Clark on 4/7/2017.
 */
public class KeyPairObject {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String insertLineBreaks (String s) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] input = s.toCharArray();
        for (int i = 0; i < input.length; i++) {
            stringBuffer.append(input[i]);
            if ((i % 75) == 0 && i != 0) {
                stringBuffer.append("\r\n");
            }
        }

        return stringBuffer.toString();
    }

    public String asJson () {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("{ \"publicKey\" :");
            stringBuffer.append(" \"");
            byte[] data = Base64.getEncoder().encode(getPublicKey().getEncoded());
            String s = new String(data);
            stringBuffer.append(s);
            stringBuffer.append("\", \"privateKey\" : \"");
            data = Base64.getEncoder().encode(getPrivateKey().getEncoded());
            s = new String(data);
            stringBuffer.append(s);
            stringBuffer.append("\" }");

            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
