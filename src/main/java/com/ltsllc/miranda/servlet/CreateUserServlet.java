package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.servlet.holder.CreateUserHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateUserServlet extends MirandaServlet {
    private static Gson gson = new Gson();


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserObject user = fromJson(req.getInputStream(), UserObject.class);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            byte[] data = Base64.getDecoder().decode(user.getPublicKey());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);

            PublicKey publicKey = keyFactory.generatePublic(spec);
            com.ltsllc.miranda.PublicKey mirandaKey = new com.ltsllc.miranda.PublicKey(publicKey);

            User newUser = new User(user.getName(), user.getDescription(), mirandaKey);
            boolean userCreated = CreateUserHolder.getInstance().createUser(newUser);

            ResultObject resultObject = new ResultObject();

            if (userCreated) {
                resultObject.setResult("success");
            } else {
                resultObject.setResult("failure");
                resultObject.setAdditionalInfo("duplicate user");
            }

            String json = gson.toJson(resultObject);
            resp.getOutputStream().println(json);
            resp.setStatus(200);
        } catch (GeneralSecurityException | IOException | MirandaException e) {
            ResultObject resultObject = new ResultObject();
            resultObject.setResult("error");
            String json = gson.toJson(resultObject);
            resp.getOutputStream().println(json);
            resp.setStatus(500);
        }
    }
}
