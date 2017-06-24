package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.servlet.objects.EventSearchRequestObject;
import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;

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
    public Class<? extends RequestObject> getRequestClass() {
        return EventSearchRequestObject.class;
    }

    @Override
    public ResultObject performService(HttpServletRequest request, HttpServletResponse response, RequestObject requestObject)
            throws ServletException, IOException, TimeoutException
    {
        return null;
    }
}
