package com.ltsllc.miranda.user;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by Clark on 2/10/2017.
 */
public interface HttpActionHandler {
    public HttpResponse handleAction (HttpRequest request);
}
