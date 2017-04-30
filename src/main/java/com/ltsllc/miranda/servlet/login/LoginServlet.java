package com.ltsllc.miranda.servlet.login;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 3/31/2017.
 */
public class LoginServlet extends MirandaServlet {
    public void doOptions (HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Allow", "*");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, Access-Control-Allow-Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Access-Control-Max-Age", "1209600");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginResultObject result = new LoginResultObject();

        try {
            LoginObject loginObject = fromJson(req.getInputStream(), LoginObject.class);
            LoginHolder.LoginResult loginResult = LoginHolder.getInstance().login(loginObject.getName());
            result.setResult(loginResult.result);

            if (loginResult.session != null) {
                result.setCategory(loginResult.session.getUser().getCategory().toString());
                String sessionIdString = Long.toString(loginResult.session.getId());
                byte[] plainText = sessionIdString.getBytes();
                byte[] cipherText = loginResult.session.getUser().getPublicKey().encrypt(plainText);
                String string = new String(Base64.getEncoder().encode(cipherText));
                result.setSession(string);
            }
        } catch (MirandaException | GeneralSecurityException e) {
            result.setResult(Results.Exception);
            result.setAdditionalInfo(e);
        } catch (TimeoutException e) {
            result.setResult(Results.Timeout);
        }

        resp.setHeader("Access-Control-Allow-Origin", "*");
        respond(resp.getOutputStream(), result);
        resp.setStatus(200);
    }
}
