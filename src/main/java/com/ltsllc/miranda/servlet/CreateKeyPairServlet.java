package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.servlet.objects.KeyPairObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateKeyPairServlet extends HttpServlet {
    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            KeyPairObject keyPairObject = new KeyPairObject();
            keyPairObject.setPrivateKey(keyPair.getPrivate());
            keyPairObject.setPublicKey(keyPair.getPublic());
            String json = keyPairObject.asJson();
            resp.getOutputStream().println(json);
            resp.setStatus(200);
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException ("Exception trying to generate key pair", e);
        }
    }
}
