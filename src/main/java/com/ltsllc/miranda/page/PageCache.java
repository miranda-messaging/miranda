package com.ltsllc.miranda.page;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.writer.Writer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PageCache extends Consumer {
    private static final String NAME = "page cache";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

    private int numberOfPages;
    private int numberOfEventsPerPage;
    private Writer writer;
    private Page[] pages;
    private Map<String, Page> pendingWrites = new HashMap<>();
    private String directory;
    public Map<String, Page> getPendingWrites() {
        return pendingWrites;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Page[] getPages() {
        return pages;
    }

    public void setPages(Page[] pages) {
        this.pages = pages;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfEventsPerPage() {
        return numberOfEventsPerPage;
    }

    public void setNumberOfEventsPerPage(int numberOfEventsPerPage) {
        this.numberOfEventsPerPage = numberOfEventsPerPage;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public PageCache (String directory, int numberOfPages, int numberOfEventsPerPage, Writer writer) {
        super(NAME);
        setNumberOfEventsPerPage(numberOfEventsPerPage);
        setNumberOfPages(numberOfPages);
        setWriter(writer);
        setDirectory(directory);
        Page[] pages = new Page[getNumberOfPages()];
        int i;
        for (i = 0; i < pages.length; i++) {
            pages[i] = new Page();
        }

        setPages(pages);
        setCurrentState(new PageCacheReadyState(this));
    }

    public void addEvent(Event event) {
        Page dirtyPage = null;
        boolean added = false;
        for (Page page : getPages()) {
            if (page.getNumberOfEvents() < getNumberOfEventsPerPage()) {
                page.addEvent(event);
                dirtyPage = page;
                break;
            }
        }

        if (dirtyPage == null) {
            Panic panic = new Panic("No room to add event", null, Panic.Reasons.CacheIsFull);
            Miranda.panicMiranda(panic);
        } else {
            writePage(dirtyPage);
        }
    }

    public void writeCompleted(String filename) {
        if (getPendingWrites().containsKey(filename)) {
            Page page = getPendingWrites().get(filename);
            getPendingWrites().remove(filename);
            page.setBeingWritten(false);
            page.setDirty(false);
        }
    }

    public void writePage(Page page) {
        String json = page.toJson();
        Date now = new Date(System.currentTimeMillis());
        String filename = getDirectory() + File.separator + simpleDateFormat.format(now);

        getPendingWrites().put (filename, page);
        page.setBeingWritten(true);

        getWriter().sendWrite(getQueue(), this, filename, json.getBytes());
    }

    public Event getEvent (String eventId) {
        for (Page p : getPages()) {
            Event event = p.getEvent(eventId);
            if (event != null)
                return event;
        }
        return null;
    }
}
