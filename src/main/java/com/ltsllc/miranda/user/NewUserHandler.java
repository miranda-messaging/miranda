package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.User;
import io.netty.handler.codec.http.*;

/**
 * Created by Clark on 2/10/2017.
 */
public class NewUserHandler implements PostHandler {
    private static Gson ourGson = new Gson();
    private UsersFile usersFile;

    public NewUserHandler (UsersFile usersFile) {
        this.usersFile = usersFile;
    }

    public UsersFile getUsersFile() {
        return usersFile;
    }

    public HttpResponse handlePost(HttpRequest request, String content) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        User newUser = ourGson.fromJson(content, User.class);

        NewUserMessage newUserMessage = new NewUserMessage(null, this, newUser);
        Consumer.staticSend(newUserMessage, getUsersFile().getQueue());

        return response;
    }
}
