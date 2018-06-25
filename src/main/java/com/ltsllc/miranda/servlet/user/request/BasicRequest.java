package com.ltsllc.miranda.servlet.user.request;

import com.ltsllc.miranda.clientinterface.requests.Request;

/**
 * A request that has no attributes other than the sessinId
 */
public class BasicRequest extends Request {
    public BasicRequest (String sessionId) {
        super(sessionId);
    }
}
