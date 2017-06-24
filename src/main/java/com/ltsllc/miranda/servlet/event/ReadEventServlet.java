package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.servlet.objects.EventResultObject;
import com.ltsllc.miranda.servlet.objects.ReadEventRequestObject;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * An {@link EventServlet} that reads in an {@link com.ltsllc.miranda.event.Event}.
 */
public class ReadEventServlet extends EventServlet {
    @Override
    public Class<? extends RequestObject> getRequestClass() {
        return ReadEventRequestObject.class;
    }

    @Override
    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, RequestObject requestObject)
            throws ServletException, IOException, TimeoutException {

        ReadEventRequestObject readEventRequestObject = (ReadEventRequestObject) requestObject;
        EventHolder.ReadResult readResult = getEventHolder().read(readEventRequestObject.getGuid());
        EventResultObject eventResultObject = new EventResultObject();
        eventResultObject.setResult(readResult.result);
        eventResultObject.setEvent(readResult.event);
        eventResultObject.setException(readResult.exception);

        return eventResultObject;
    }
}
