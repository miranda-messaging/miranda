package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaUncheckedException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class SimpleFileWatcher extends FileWatcher {
    private Long modificationTime;

    public Long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public SimpleFileWatcher(File file, BlockingQueue<Message> listener) throws IOException {
        super(file, listener);
        scan();
    }

    @Override
    boolean scan() throws IOException {
        long lastmodification = -1;
        lastmodification = (getModificationTime() == null) ? -1 : getModificationTime().longValue();
        long filetime = getFile().lastModified();

        boolean changed = false;

        if (getFile().exists()) {
            if (null == getModificationTime())
                changed = true;
            else
                changed = getModificationTime().longValue() != getFile().lastModified();

            Long lastModified = new Long(getFile().lastModified());
            setModificationTime(lastModified);
        } else {
            changed = getModificationTime() != null;
            setModificationTime(null);
        }

        return changed;
    }

    @Override
    public boolean matches(File file, BlockingQueue<Message> listener) {
        try {
            if (!(file.getCanonicalPath().equals(getFile().getCanonicalPath())))
                return false;

            return listener == getListener();
        } catch (IOException e) {
            throw new MirandaUncheckedException("Exception in matches", e);
        }
    }
}
