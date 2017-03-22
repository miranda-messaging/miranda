package com.ltsllc.miranda.user;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/5/2017.
 */
public class UsersFile extends SingleFile<User> {
    public static final String FILE_NAME = "users";

    private static UsersFile ourInstance;

    public static UsersFile getInstance() {
        return ourInstance;
    }

    public static void setInstance (UsersFile usersFile) {
        ourInstance = usersFile;
    }

    public UsersFile (Writer writer, String filename) {
        super(filename, writer);

        UsersFileReadyState usersFileReadyState = new UsersFileReadyState(this, this);
        setCurrentState(usersFileReadyState);

        setInstance(this);
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
