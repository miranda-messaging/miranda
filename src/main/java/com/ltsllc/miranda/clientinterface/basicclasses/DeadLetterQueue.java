package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.common.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltsllc on 7/2/2017.
 */
public class DeadLetterQueue extends MirandaObject {
    private List<String> events;

    public DeadLetterQueue () {
        this.events = new ArrayList<String>();
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof DeadLetterQueue))
            return false;

        DeadLetterQueue other = (DeadLetterQueue) o;

        return Utils.StringListsAreEquivalent(events, other.events);
    }

    @Override
    public void copyFrom(Mergeable mergeable) {

    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public void addEvent (Event event) {
        events.add(event.getGuid());
    }

    public void addEventGuid (String guid) {
        events.add(guid);
    }
}
