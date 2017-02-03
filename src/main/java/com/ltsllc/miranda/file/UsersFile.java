package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class UsersFile extends SingleFile<User> {
    private static UsersFile ourInstance;

    private List<User> users = new ArrayList<User>();

    public static UsersFile getInstance() {
        return ourInstance;
    }

    public UsersFile (BlockingQueue<Message> queue, String s) {
        super(s, queue);
    }


    public void add(User user) {
        users.add(user);
        write();
    }

    public void add (User user, boolean write) {
        users.add(user);

        if (write) {
            write();
        }
    }

    public static synchronized void initialize(String filename, BlockingQueue<Message> writerQueue) {
        if (null == ourInstance) {
            ourInstance = new UsersFile(writerQueue, filename);
        }
    }

    public Type getBasicType ()
    {
        return new TypeToken<ArrayList<User>>() {}.getType();
    }


}
