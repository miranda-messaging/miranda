package com.ltsllc.miranda.servlet.bootstrap;

import com.ltsllc.clcl.DistinguishedName;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.bootstrap.BootstrapOperation;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.user.messages.BootstrapResponseMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class BootstrapServlet extends MirandaServlet {
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Reply reply = new Reply();

        try {
            reply = basicPost(req, resp);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted while wait for respons", e, Panic.Reasons.Inerrupted);
            Miranda.panicMiranda(panic);
            reply.result = Results.Interrupted;
        }

        String json = getGson().toJson(reply);
        PrintWriter printWriter = new PrintWriter(resp.getOutputStream());
        printWriter.print(json);
        printWriter.close();
    }

    public Reply basicPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InterruptedException {
        Reply reply = new Reply();
        BootstrapOperation bootstrapOperation = new BootstrapOperation();
        bootstrapOperation.start();

        String caCountry = req.getParameter("ca.country");
        String caState = req.getParameter("ca.state");
        String caCity = req.getParameter("ca.city");
        String caCompany = req.getParameter("ca.company");
        String caDivision = req.getParameter("ca.division");
        String caName = req.getParameter("ca.name");
        String caPassword = req.getParameter("ca.password");

        String nodeCountry = req.getParameter("node.country");
        String nodeState = req.getParameter("node.state");
        String nodeCity = req.getParameter("node.city");
        String nodeCompany = req.getParameter("node.company");
        String nodeDivision = req.getParameter("node.division");
        String nodeName = req.getParameter("node.name");
        String nodePassword = req.getParameter("node.password");

        String adminCountry = req.getParameter("admin.country");
        String adminState = req.getParameter("admin.state");
        String adminCity = req.getParameter("admin.city");
        String adminCompany = req.getParameter("admin.company");
        String adminDivision = req.getParameter("admin.division");
        String adminName = req.getParameter("admin.name");
        String adminPassword = req.getParameter("admin.password");

        if (isEmpty(caCountry) || isEmpty(caState) || isEmpty(caCity) || isEmpty(caCompany) || isEmpty(caDivision)
                || isEmpty(caName) || isEmpty(caPassword) || isEmpty(adminPassword) || isEmpty(nodeCountry)
                || isEmpty(nodeState) || isEmpty(nodeCity) || isEmpty(nodeCompany) || isEmpty(nodeDivision)
                || isEmpty(nodeName) || isEmpty(adminCountry)
                || isEmpty(adminState) || isEmpty(adminCity) || isEmpty(adminCompany) || isEmpty(adminDivision)
                || isEmpty(adminName) || isEmpty(nodePassword)) {
            reply.result = Results.MissingData;
            return reply;
        }

        DistinguishedName caDistinguisdhedName = new DistinguishedName("c=" + caCountry + ", st=" + caState + ", l="
                + caCity + ", o=" + caCompany + ", ou=" + caDivision + ", cn=" + caName);
        DistinguishedName nodeDistinguishedName = new DistinguishedName("c=" + nodeCountry + ", st=" + nodeState + ", l="
                + nodeCity + ", o=" + nodeCompany + ", ou=" + nodeDivision + ", cn=" + nodeName);
        DistinguishedName adminDistinguishedName = new DistinguishedName("c=" + adminCountry + ", st=" + adminState + ", l="
                + adminCity + ", o=" + adminCompany + ", ou=" + adminDivision + ", cn=" + adminName);
        BootstrapMessage bootstrapMessage = new BootstrapMessage(getQueue(), this, caDistinguisdhedName,
                caPassword, nodeDistinguishedName, nodePassword, adminDistinguishedName, adminPassword);
        send(bootstrapOperation.getQueue(), bootstrapMessage);


        BootstrapResponseMessage bootstrapResponseMessage =
                (BootstrapResponseMessage) getQueue().poll(1, TimeUnit.SECONDS);

        if (bootstrapResponseMessage == null) {
            reply.result = Results.Timeout;
            return reply;
        } else {
            reply.result = bootstrapResponseMessage.getResult();
            return reply;
        }
    }
}
