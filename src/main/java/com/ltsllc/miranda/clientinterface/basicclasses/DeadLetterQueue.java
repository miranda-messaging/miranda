package com.ltsllc.miranda.clientinterface.basicclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltsllc on 7/2/2017.
 */
public class DeadLetterQueue extends MirandaObject implements Cloneable,Mergeable {
    private List<String> events;

    public DeadLetterQueue() {
        this.events = new ArrayList<String>();
    }

    public boolean stringListsAreEquivalent (List<String> us, List<String> other) {
        if (us.size() != other.size())
        {
            return false;
        }

        for (int i = 0; i < us.size(); i++)
        {
            String s1 = us.get(i);
            String s2 = other.get(i);

            if (!s1.equals(s2)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof DeadLetterQueue))
            return false;

        DeadLetterQueue other = (DeadLetterQueue) o;

        return stringListsAreEquivalent(events, other.events);
    }

    @Override
    public void copyFrom(MergeableObject mergeable) {
        DeadLetterQueue other = (DeadLetterQueue) mergeable;

        List<String> events = new ArrayList<String>(other.getEvents());
        setEvents(events);

        setLastChange(other.getLastChange());
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        events.add(event.getGuid());
    }

    public void addEventGuid(String guid) {
        events.add(guid);
    }

    @Override
    public void copyFrom(Mergeable mergeable) {

    }

    @Override
    public boolean merge(Mergeable mergeable) {
        if (getLastChange() > mergeable.getLastChange())
            return false;
        else {
            copyFrom(mergeable);
            return true;
        }


    }

    public Object clone () throws CloneNotSupportedException {
        return super.clone();
    }
}
