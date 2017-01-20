package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class UsersFile extends SingleFile<User> {
    public UsersFile (BlockingQueue<Message> queue, String s) {
        super(s, queue);
    }
}
