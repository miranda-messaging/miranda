package com.ltsllc.miranda.topics;

import com.google.gson.Gson;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.Updateable;
import com.ltsllc.miranda.util.ImprovedRandom;

/**
 * Created by Clark on 1/5/2017.
 */
public class Topic extends StatusObject<Topic> implements Perishable {
    public enum RemotePolicies {
        None,
        Acknowledged,
        Written
    }


    private static Gson ourGson = new Gson();

    private String name;
    private String owner;
    private RemotePolicies remotePolicy;

    public RemotePolicies getRemotePolicy() {
        return remotePolicy;
    }

    public void setRemotePolicy(RemotePolicies remotePolicy) {
        this.remotePolicy = remotePolicy;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Topic() {
        super(Status.New);
    }

    public Topic (String name) {
        super(Status.New);

        this.name = name;
    }

    public Topic (String name, String owner) {
        super(Status.New);

        this.name = name;
        this.owner = owner;
    }

    public void updateFrom (Topic other) {
        super.updateFrom(other);

        setOwner(other.getOwner());
    }

    public boolean matches (Topic other) {
        if (!super.matches(other))
            return false;

        return getName().equals(other.getName()) && getOwner().equals(other.getOwner());
    }

    public String toJson() {
        return ourGson.toJson(this);
    }

    private static final String[] NAMES = {
            "whatever",
            "users",
            "subjects",
            "books",
            "people",
            "cars",
            "locations",
            "cats",
            "dogs",
            "gerbils"
    };

    private static final String[] OWNERS = {
            "whatever",
            "joe",
            "princess",
            "arnold",
            "sam",
            "steve",
            "hedi",
            "mushroom",
            "oscar",
            "clark"
    };

    public static Topic random (ImprovedRandom improvedRandom) {
        String name = NAMES[improvedRandom.nextIndex(NAMES)];
        String owner = OWNERS[improvedRandom.nextIndex(OWNERS)];

        return new Topic(name, owner);
    }
}
