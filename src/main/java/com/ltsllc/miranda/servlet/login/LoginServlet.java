package com.ltsllc.miranda.servlet.login;

import com.google.gson.Gson;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 3/31/2017.
 */
public class LoginServlet extends MirandaServlet {
    private static Logger logger = Logger.getLogger(LoginServlet.class);
    private static Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginResultObject result = new LoginResultObject();

        try {
            LoginObject loginObject = fromJson(req.getInputStream(), LoginObject.class);
            LoginHolder.LoginResult loginResult = LoginHolder.getInstance().login(loginObject.getName());
            result.setResult(loginResult.result);

            if (loginResult.session != null) {
                byte[] plainText = Utils.toBytes(loginResult.session.getId());
                byte[] cipherText = loginResult.session.getUser().getPublicKey().encrypt(plainText);
                result.setSession(Utils.bytesToString(cipherText));
            }
        } catch (MirandaException | GeneralSecurityException e) {
            result.setResult(Results.Exception);
            result.setAdditionalInfo(e);
        } catch (TimeoutException e) {
            result.setResult(Results.Timeout);
        }

        respond(resp.getOutputStream(), result);
        resp.setStatus(200);
    }
}
