package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 6/7/2017.
 */
public class EventHolder extends ServletHolder {
    public static class ReadResult {
        public Results result;
        public Event event;
    }

    public static class CreateResult {
        public Results result;
        public String guid;
    }

    public static class ListResult {
        public Results result;
        public List<Event> events;
        public Throwable exception;
    }

    private static EventHolder ourInstance;

    private List<Event> eventList;
    private Results listResult;
    private Event event;
    private Results readResult;
    private Results createResult;
    private String guid;

    public Results getListResult() {
        return listResult;
    }

    public void setListResult(Results listResult) {
        this.listResult = listResult;
    }

    public List<Event> getEventList() {
        if (eventList == null)
            eventList = new ArrayList<Event>();

        return eventList;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGuid() {

        return guid;
    }

    public void setEventList(List<Event> list) {
        this.eventList = list;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Results getReadResult() {
        return readResult;
    }

    public void setReadResult(Results readResult) {
        this.readResult = readResult;
    }

    public static EventHolder getInstance() {
        return ourInstance;
    }

    public static void setInstance(EventHolder subscriptionHolder) {
        ourInstance = subscriptionHolder;
    }

    public static void initialize(long timeout) {
        ourInstance = new EventHolder(timeout);
    }

    public Results getCreateResult() {
        return createResult;
    }

    public void setCreateResult(Results createResult) {
        this.createResult = createResult;
    }

    public EventHolder(long timeout) {
        super("subscription holder", timeout);

        EventHolderReadyState readyState = new EventHolderReadyState(this);
        setCurrentState(readyState);
    }

    public List<Event> getSubscriptions() throws TimeoutException {
        setEventList(null);

        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionsMessage(getQueue(), this);

        sleep();

        return getEventList();
    }

    public void setReadResultAndAwaken (Results result, Event event) {
        setReadResult(result);
        setEvent(event);

        wake();
    }

    public Event readEvent(String guid) throws TimeoutException {
        setReadResult(Results.Unknown);

        Miranda.getInstance().getEventManager().sendReadEventMessage(getQueue(), this, guid);

        sleep();

        return getEvent();
    }

    public CreateResult create (Event event) throws TimeoutException {
        setCreateResult(Results.Unknown);
        setGuid(null);
        Miranda.getInstance().getEventManager().sendCreateEventMessage(getQueue(), this, event);

        sleep();

        CreateResult createResult = new CreateResult();
        createResult.result = getCreateResult();
        createResult.guid = getGuid();

        return createResult;
    }

    public ReadResult read (String guid) throws TimeoutException {
        setEvent(null);
        setReadResult(Results.Unknown);

        Miranda.getInstance().getEventManager().sendReadEventMessage(getQueue(), this, guid);

        sleep();

        ReadResult readResult = new ReadResult();
        readResult.event = getEvent();
        readResult.result = getReadResult();
        return readResult;
    }

    public ListResult list () throws TimeoutException {
        setListResult(Results.Unknown);
        setEventList(null);

        Miranda.getInstance().getEventManager().sendListMessage(getQueue(), this);

        sleep();

        ListResult listResult = new ListResult();
        listResult.result = getListResult();
        listResult.events = getEventList();

        return listResult;
    }

    public void setEventsAndAwaken(List<Event> events) {
        setEventList(events);

        wake();
    }

    public void setCreateResultAndAwaken(Results result, String guid) {
        setCreateResult(result);
        setGuid(guid);

        wake();
    }
}
