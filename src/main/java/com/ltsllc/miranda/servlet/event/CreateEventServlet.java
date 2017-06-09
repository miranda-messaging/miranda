package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.subscription.SubscriptionRequestObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.ltsllc.miranda.user.User.UserTypes.Subscriber;

/**
 * Created by Clark on 6/7/2017.
 */
public class CreateEventServlet extends EventServlet {
    @Override
    public ServletHolder getServletHolder() {
        return (ServletHolder) EventHolder.getInstance();
    }

    public EventHolder getEventHolder() {
        return EventHolder.getInstance();
    }

    public Class getRequestClass() {
        return SubscriptionRequestObject.class;
    }

    public boolean allowAccess() {
        return getSession().getUser().getCategory() == Subscriber;
    }

    public ResultObject basicPerformService(HttpServletRequest request, HttpServletResponse response,
                                            EventRequestObject eventRequestObject)
            throws IOException, ServletException, TimeoutException {

        EventHolder.CreateResult createResult = getEventHolder().create(eventRequestObject.getEvent());

        CreateEventResultObject createEventResultObject = new CreateEventResultObject();
        createEventResultObject.setResult(createResult.result);
        createEventResultObject.setGuid(createResult.guid);

        return createEventResultObject;
    }

    public ResultObject createResultObject () {
        return new ResultObject();
    }

}
