/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.servlet.user.UserObject;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.states.UsersFileReadyState;
import com.ltsllc.miranda.user.states.UsersFileStartingState;
import com.ltsllc.miranda.writer.Writer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class UsersFile extends SingleFile<User> {
    public static final String FILE_NAME = "users";

    private static Gson gson = new Gson();

    private static UsersFile ourInstance;

    public static UsersFile getInstance() {
        return ourInstance;
    }

    public static void setInstance (UsersFile usersFile) {
        ourInstance = usersFile;
    }

    public UsersFile (com.ltsllc.miranda.reader.Reader reader, Writer writer, String filename) {
        super(filename, reader, writer);

        UsersFileStartingState usersFileStartingState = new UsersFileStartingState(this);
        setCurrentState(usersFileStartingState);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        gson = gsonBuilder.create();

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

    public static synchronized void initialize(String filename, Reader reader, Writer writer) {
        if (null == ourInstance) {
            ourInstance = new UsersFile(reader, writer, filename);
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

    public void removeUsers (List<User> users) {
        boolean modified = false;

        List<User> usersToRemove = new ArrayList<User>();

        for (User user : getData()) {
            for (User user2 : users) {
                if (user.equals(user2)) {
                    usersToRemove.add(user);
                    modified = true;
                }
            }
        }

        getData().removeAll(usersToRemove);

        if (modified)
            write();
    }

    public void sendNewUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        NewUserMessage newUserMessage = new NewUserMessage(senderQueue, sender, user);
        sendToMe(newUserMessage);
    }

    public static List<UserObject> asUserObjects (List<User> users) {
        List<UserObject> userObjects = new ArrayList<UserObject>();
        for (User user : users) {
            UserObject userObject = user.asUserObject();
            userObjects.add(userObject);
        }

        return userObjects;
    }

    public byte[] getBytes () {
        List<UserObject> userObjects = asUserObjects(getData());
        String json = gson.toJson(userObjects);
        return json.getBytes();
    }

    public List<User> asUsers (List<UserObject> userObjects) throws MirandaException {
        List<User> users = new ArrayList<User>();

        for (UserObject userObject : userObjects) {
            User user = userObject.asUser();
            users.add(user);
        }

        return users;
    }


    public void basicLoad () {
        try {
            String json = readFile(getFilename());
            Type listType = new TypeToken<ArrayList<UserObject>>() {}.getType();
            List<UserObject> userObjects = gson.fromJson(json, listType);
            List<User> users = asUsers(userObjects);

            setData(users);
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception trying to load users file", e, Panic.Reasons.ExceptionLoadingFile);
            Miranda.getInstance().panic(panic);
            List<User> emptyList = new ArrayList<User>();
            setData(emptyList);
        }
    }

    public void load () {
        File file = new File(getFilename());

        if (file.exists()) {
            basicLoad();
        } else {
            List<User> emptyList = new ArrayList<User>();
            setData(emptyList);
        }

        fireFileLoaded();
    }

    public void checkForDuplicates () {
        List<User> userList = new ArrayList<User>(getData());
        List<User> duplicates = new ArrayList<User>();

        for (User current : getData()) {
            for (User user : getData()) {
                if (current.getName().equals(user.getName()) && current != user) {
                    duplicates.add(current);
                }
            }
        }

        getData().removeAll(duplicates);
    }

    public User find (User user) {
        for (User candidate : getData()) {
            if (candidate.getName().equals(user.getName()))
                return candidate;
        }

        return null;
    }
}
