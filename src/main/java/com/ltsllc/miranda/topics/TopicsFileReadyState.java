package com.ltsllc.miranda.topics;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Topic;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.SingleFileReadyState;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
public class TopicsFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(TopicsFileReadyState.class);

    private TopicsFile topicsFile;


    public TopicsFileReadyState(TopicsFile topicsFile) {
        super(topicsFile);

        this.topicsFile = topicsFile;
    }

    public TopicsFile getTopicsFile() {
        return topicsFile;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("topics", getTopicsFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getTopicsFile().getQueue(), this, nameVersion);
        send(getVersionMessage.getSender(), versionMessage);

        return this;
    }


    @Override
    public void add(Object o) {
        Topic topic = (Topic) o;
        getTopicsFile().getData().add(topic);
    }


    @Override
    public SingleFile getFile() {
        return getTopicsFile();
    }


    @Override
    public boolean contains(Object o) {
        Topic topic = (Topic) o;
        for (Topic t : getTopicsFile().getData()) {
            if (t.equals(topic))
                return true;
        }

        return false;
    }


    public void write() {
        WriteMessage writeMessage = new WriteMessage(getTopicsFile().getFilename(), getTopicsFile().getBytes(), getTopicsFile().getQueue(), this);
        send(getTopicsFile().getWriterQueue(), writeMessage);
    }


    @Override
    public Type getListType() {
        return new TypeToken<List<Topic>>(){}.getType();
    }

    @Override
    public String getName() {
        return "topics";
    }

    @Override
    public Version getVersion() {
        return getTopicsFile().getVersion();
    }

    @Override
    public List<Perishable> getPerishables() {
        return new ArrayList<Perishable>(getTopicsFile().getData());
    }
}
