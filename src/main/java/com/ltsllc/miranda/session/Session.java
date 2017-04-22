package com.ltsllc.miranda.session;

import com.ltsllc.miranda.user.User;

/**
 * Created by Clark on 3/30/2017.
 */
public class Session {
    private User user;
    private long expires;
    private long id;

    public User getUser() {
        return user;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires (long expires) {
        this.expires = expires;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Session (User user, long expires, long id) {
        this.user = user;
        this.expires = expires;
        this.id = id;
    }

    public boolean equals (Object o) {
        if (null == o || !(o instanceof Session))
            return false;

        Session other = (Session) o;
        return getUser().equals(other.getUser()) && getExpires() == other.getExpires() && getId() == other.getId();
    }
}
