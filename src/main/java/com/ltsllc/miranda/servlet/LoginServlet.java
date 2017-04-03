package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.ltsllc.miranda.servlet.holder.LoginHolder;
import com.ltsllc.miranda.servlet.objects.LoginObject;
import com.ltsllc.miranda.servlet.objects.LoginResult;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 3/31/2017.
 */
public class LoginServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(LoginServlet.class);
    private static Gson gson = new Gson();

    public String read(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringWriter stringWriter = new StringWriter();
        for (int c = inputStreamReader.read(); c != -1; c = inputStreamReader.read()) {
            stringWriter.append((char) c);
        }

        return stringWriter.toString();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = read(req.getInputStream());
        LoginObject loginObject = gson.fromJson(json, LoginObject.class);
        long session = LoginHolder.getInstnace().login(loginObject);

        LoginResult loginResult = null;
        if (-1 == session) {
            loginResult = new LoginResult(LoginResult.LoginResults.Failure);
            logger.info("Failed to log in " + loginObject.getName());
        }
        else {
            loginResult = new LoginResult(LoginResult.LoginResults.Success);
            logger.info("Logged in " + loginObject.getName() + ", session " + session);
        }

        json = gson.toJson(loginResult);

        User user = LoginHolder.getInstnace().getUser();
        if (null == user) {
            loginResult = new LoginResult(LoginResult.LoginResults.Failure);
            logger.info ("Failed to get user info for " + loginObject.getName());
        } else {
            String sessionString = Long.toString(session);
            String cipherText = null;
            try {
                byte[] cipherTextBytes = user.getPublicKey().encrypt(sessionString);
                cipherText = Utils.bytesToString(cipherTextBytes);
            } catch (GeneralSecurityException e) {
                throw new ServletException("Exception encrypting session", e);
            }

            loginResult.setSession(cipherText);
        }

        resp.getOutputStream().println(json);
        resp.setStatus(200);
    }
}
