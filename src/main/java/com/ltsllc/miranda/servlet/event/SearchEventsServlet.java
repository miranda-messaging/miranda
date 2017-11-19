package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.clientinterface.requests.EventSearchRequest;
import com.ltsllc.miranda.clientinterface.requests.Request;
import com.ltsllc.miranda.clientinterface.results.ResultObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * An {@link EventServlet} that is used to search through all the
 * events in the system for those that match its search criteria.
 */
public class SearchEventsServlet extends EventServlet {
    @Override
    public Class<? extends Request> getRequestClass() {
        return EventSearchRequest.class;
    }

    @Override
    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, Request requestObject)
            throws ServletException, IOException, TimeoutException {
        return null;
    }
}
