package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.servlet.objects.ListResultObject;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.subscription.SubscriptionRequestObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 6/7/2017.
 */
public class ListEventsServlets extends EventServlet {
    public EventHolder getEventHolder() {
        return EventHolder.getInstance();
    }

    @Override
    public ResultObject basicPerformService(HttpServletRequest request, HttpServletResponse response, SubscriptionRequestObject requestObject) throws IOException, ServletException, TimeoutException {
        getEventHolder().list();
        EventHolder.ListResult listResult = getEventHolder().list();
        ListResultObject listResultObject = new ListResultObject();
        listResultObject.setResult(listResult.result);
        listResultObject.setList(listResult.events);
        listResultObject.setException(listResult.exception);

        return listResultObject;
    }

    @Override
    public ResultObject createResultObject() {
        return new ListResultObject();
    }
}
