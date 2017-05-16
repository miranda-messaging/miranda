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

    public void extendExpires (long time) {
        expires += time;
    }
}
