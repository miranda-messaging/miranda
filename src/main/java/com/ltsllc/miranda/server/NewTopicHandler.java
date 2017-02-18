package com.ltsllc.miranda.server;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.PostHandler;

/**
 * Created by Clark on 2/18/2017.
 */
public class NewTopicHandler extends NewObjectPostHandler<TopicsFile> {
    public NewTopicHandler (TopicsFile file) {
        super(file);

        NewObjectHandlerReadyState newObjectHandlerReadyState = new NewTopicHandlerReadyState(this, getFile(), this);
        setCurrentState(newObjectHandlerReadyState);
    }
}
