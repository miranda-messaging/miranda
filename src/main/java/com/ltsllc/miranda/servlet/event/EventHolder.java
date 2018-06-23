package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.session.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 6/7/2017.
 */
public class EventHolder extends ServletHolder {
    public static class ReadResult {
        public Results result;
        public Throwable exception;
        public Event event;
    }

    public static class CreateResult {
        public Results result;
        public Throwable exception;
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
    private Map<Long, Long> sessionIdToExpirationTime;
    private long msecCheckIsGoodFor;
    private Long wakeUpSessionId;
    private Map<Thread, Long> threadToWakeupTime;


    public Map<Thread, Long> getThreadToWakeupTime() {
        return threadToWakeupTime;
    }

    public Long getWakeUpSessionId() {
        return wakeUpSessionId;
    }

    public void setWakeUpSessionId(Long wakeUpSessionId) {
        this.wakeUpSessionId = wakeUpSessionId;
    }

    public long getMsecCheckIsGoodFor() {
        return msecCheckIsGoodFor;
    }

    public void setMsecCheckIsGoodFor(long msecCheckIsGoodFor) {
        this.msecCheckIsGoodFor = msecCheckIsGoodFor;
    }

    public Map<Long, Long> getSessionIdToExpirationTime() {
        return sessionIdToExpirationTime;
    }

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

    public static void initialize(long timeout) throws MirandaException {
        ourInstance = new EventHolder(timeout);
    }

    public Results getCreateResult() {
        return createResult;
    }

    public void setCreateResult(Results createResult) {
        this.createResult = createResult;
    }

    public EventHolder(long timeout) throws MirandaException {
        super("subscription holder", timeout);

        this.sessionIdToExpirationTime = new HashMap<Long, Long>();
        this.msecCheckIsGoodFor = MirandaProperties.getInstance().getLongProperty(MirandaProperties.PROPERTY_SESSION_LENGTH) / 2;
        this.threadToWakeupTime = new HashMap<Thread, Long>();

        EventHolderReadyState readyState = new EventHolderReadyState(this);
        setCurrentState(readyState);
    }

    public List<Event> getSubscriptions() throws TimeoutException {
        setEventList(null);

        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionsMessage(getQueue(), this);

        sleep();

        return getEventList();
    }

    public void setReadResultAndAwaken(Results result, Event event) {
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

    public CreateResult create(Event event) throws TimeoutException {
        setCreateResult(Results.Unknown);
        setGuid(null);
        Miranda.getInstance().getEventManager().sendCreateEventMessage(getQueue(), this, event);

        sleep();

        CreateResult createResult = new CreateResult();
        createResult.result = getCreateResult();
        createResult.guid = getGuid();

        return createResult;
    }

    public ReadResult read(String guid) throws TimeoutException {
        setEvent(null);
        setReadResult(Results.Unknown);

        Miranda.getInstance().getEventManager().sendReadEventMessage(getQueue(), this, guid);

        sleep();

        ReadResult readResult = new ReadResult();
        readResult.event = getEvent();
        readResult.result = getReadResult();
        return readResult;
    }

    public ListResult list() throws TimeoutException {
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

    public synchronized void sleepOn(long sessionId) throws TimeoutException {
        Thread thread = Thread.currentThread();
        long wakeupTime = System.currentTimeMillis() + getTimeoutPeriod();
        getThreadToWakeupTime().put(thread, wakeupTime);

        sleep();

        while (!shouldWakeUp(sessionId))
            goBackToSleep();
    }

    public synchronized void wakeUpFor(long sessionId) {
        setWakeUpSessionId(sessionId);
        notifyAll();
    }

    public boolean shouldWakeUp(long sessionId) {
        if (null == getWakeUpSessionId()) {
            Panic panic = new Panic("null session ID", Panic.Reasons.InvalidWakeup);
            Miranda.panicMiranda(panic);
            return false;
        }

        return sessionId == getWakeUpSessionId().longValue();
    }

    public void goBackToSleep() throws TimeoutException {
        Thread thread = Thread.currentThread();
        if (getThreadToWakeupTime().get(thread) == null) {
            Panic panic = new Panic("null wakeup time for tread " + thread, Panic.Reasons.NullWakeupTime);
            Miranda.panicMiranda(panic);
        } else if (getThreadToWakeupTime().get(thread) > System.currentTimeMillis()) {
            throw new TimeoutException();
        } else {
            long remainingPeriod = getThreadToWakeupTime().get(thread) - System.currentTimeMillis();
            waitFor(remainingPeriod);
        }
    }

    public synchronized void removeSleeperForSession(long sessionIds) {
        Thread thread = Thread.currentThread();
        getThreadToWakeupTime().remove(thread);
    }

    public boolean sessionIsGood(long sessionId) throws TimeoutException {
        long now = System.currentTimeMillis();
        Long expirationTime = getSessionIdToExpirationTime().get(sessionId);

        if (expirationTime == null || expirationTime.longValue() > now) {
            Miranda.getInstance().getSessionManager().sendCheckSessionMessage(getQueue(), this, sessionId);
            sleepOn(sessionId);
            removeSleeperForSession(sessionId);
        }

        expirationTime = getSessionIdToExpirationTime().get(sessionId);
        return expirationTime != null;
    }

    public void setCheckSessionIdResultAndAwaken(long sessionId, Session session) {
        if (session != null) {
            long expirationTime = System.currentTimeMillis() + getMsecCheckIsGoodFor();
            Long value = new Long(expirationTime);
            Long key = new Long(sessionId);
            getSessionIdToExpirationTime().put(key, value);
        }

        wakeUpFor(sessionId);
    }
}
