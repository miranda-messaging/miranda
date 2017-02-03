package com.ltsllc.miranda.messagesFile;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.User;
import com.ltsllc.miranda.file.SingleFile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/11/2017.
 */
public class MessagesFile extends SingleFile<Message> {
    public MessagesFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }

    public Type getBasicType ()
    {
        return new TypeToken<ArrayList<User>>() {}.getType();
    }

}
