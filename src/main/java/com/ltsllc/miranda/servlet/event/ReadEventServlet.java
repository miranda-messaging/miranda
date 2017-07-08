package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.clientinterface.requests.ReadEventRequest;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.results.EventResultObject;
import com.ltsllc.miranda.clientinterface.results.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * An {@link EventServlet} that reads in an {@link com.ltsllc.miranda.test.TestNetworkListener.Event}.
 */
public class ReadEventServlet extends EventServlet {
    @Override
    public Class<? extends Request> getRequestClass() {
        return ReadEventRequest.class;
    }

    @Override
    public ResultObject createResultObject() {
        return new EventResultObject();
    }

    @Override
    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, Request requestObject)
            throws ServletException, IOException, TimeoutException {

        ReadEventRequest readEventRequestObject = (ReadEventRequest) requestObject;
        EventHolder.ReadResult readResult = getEventHolder().read(readEventRequestObject.getGuid());
        EventResultObject eventResultObject = new EventResultObject();
        eventResultObject.setResult(readResult.result);
        eventResultObject.setEvent(readResult.event);
        eventResultObject.setAdditionalInfo(readResult.exception);

        return eventResultObject;
    }
}
