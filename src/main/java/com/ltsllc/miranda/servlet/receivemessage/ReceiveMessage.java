package com.ltsllc.miranda.servlet.receivemessage;

import com.ltsllc.commons.io.Util;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.user.messages.NewUserMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeoutException;

/**
 * A Miranda message receiver
 *
 * <p>
 *     This class is responsible for receiving User messages.
 *     It does this by passing the message onto the appropriate TopicActor
 * </p>
 */
public class ReceiveMessage extends MirandaServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte[] content = Util.readCompletely(req.getInputStream());
        Miranda.getInstance().sendPublisherMessage (getQueue(), this, content, req.getRequestURL().toString());

        try {
            waitForReply(2000);
            resp.setStatus(200);
        } catch (TimeoutException e) {
            resp.setStatus(501);
            OutputStream outputStream = resp.getOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write("The system timed out waiting for a reply");
        }
    }
}
