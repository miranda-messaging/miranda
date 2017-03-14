package com.ltsllc.miranda.user;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.User;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class UsersFile extends SingleFile<User> {
    private static UsersFile ourInstance;


    public static UsersFile getInstance() {
        return ourInstance;
    }

    public UsersFile (Writer writer, String filename) {
        super(filename, writer);

        UsersFileReadyState usersFileReadyState = new UsersFileReadyState(this, this);
        setCurrentState(usersFileReadyState);
    }


    public void addUser (User user) {
        getData().add(user);
        write();
    }

    public void add (User user, boolean write) {
        getData().add(user);

        if (write) {
            write();
        }
    }

    public static synchronized void initialize(String filename, Writer writer) {
        if (null == ourInstance) {
            ourInstance = new UsersFile(writer, filename);
            ourInstance.start();
            ourInstance.load();
        }
    }

    public List buildEmptyList () {
        return new ArrayList<User>();
    }

    public Type listType () {
        return new TypeToken<ArrayList<User>>(){}.getType();
    }

}
