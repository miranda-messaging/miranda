package com.ltsllc.miranda.file;

import com.ltsllc.miranda.message.Message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * An object that watches a file or directory.
 * <p>
 * <p>
 * An instance of this class checks to see if the file or directory has changed.
 * If it has, the object will send a message to the subscriber.
 * The actual message sent depends on the subclass.
 * </p>
 */
public interface Watcher {
    /**
     * Check for a change in the file or directory and take the appropriate action if the
     * file or directory has changed.
     */
    public void check() throws IOException;

    /**
     * Is this instance watching a particular file and sending message to a particular listener?
     *
     * @param file     The file to check against.
     * @param listener The listener to check against.
     * @return true if the instance is watching the specified file and sending message to the specified
     * listener.
     */
    public boolean matches(File file, BlockingQueue<Message> listener);
}
