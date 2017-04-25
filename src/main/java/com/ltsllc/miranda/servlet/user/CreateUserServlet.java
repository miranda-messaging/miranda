package com.ltsllc.miranda.servlet.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.servlet.holder.UserHolder;
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
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateUserServlet extends MirandaServlet {
    private static Gson gson = new Gson();

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultObject resultObject = new ResultObject();

        try {
            UserObject user = fromJson(req.getInputStream(), UserObject.class);
            User newUser = new User(user.getName(), user.getCategory(), user.getDescription(), user.getPublicKeyPem());
            Results result = UserHolder.getInstance().createUser(newUser);
            resultObject.setResult(result);
        } catch (IOException | MirandaException e) {
            resultObject.setResult(Results.Exception);
            resultObject.setAdditionalInfo(e);
        } catch (TimeoutException e) {
            resultObject.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), resultObject);
        resp.setStatus(200);
    }
}
