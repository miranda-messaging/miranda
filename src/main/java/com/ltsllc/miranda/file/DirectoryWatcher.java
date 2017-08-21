package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaUncheckedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class DirectoryWatcher extends FileWatcher {
    private Map<String, Long> filenameToModificationTime;

    public DirectoryWatcher (File directory, BlockingQueue<Message> listener) throws IOException {
        super(directory, listener);
        filenameToModificationTime = new HashMap<String, Long>();
        performScan();
    }

    public Map<String, Long> getFilenameToModificationTime() {
        return filenameToModificationTime;
    }

    public void setFilenameToModificationTime(Map<String, Long> filenameToModificationTimes) {
        this.filenameToModificationTime = filenameToModificationTimes;
    }

    public boolean scan(File file, Map<String, Long> filenameToModificationTimes) throws IOException {
        if (file.isDirectory())
            return scanDirectory(file, filenameToModificationTimes);
        else
            return scanFile(file, filenameToModificationTimes);
    }

    public boolean scanFile(File file, Map<String, Long> filenameToModificationTimes) throws IOException {
        String name = file.getCanonicalPath();

        filenameToModificationTimes.put(name, file.lastModified());

        return fileChanged (name, file.lastModified());
    }

    public boolean scanDirectory (File directory, Map<String, Long> filenameToModificationTimes) throws IOException {
        String fullname = directory.getCanonicalPath();
        filenameToModificationTimes.put(fullname, new Long(directory.lastModified()));

        String prefix = fullname + File.separatorChar;
        String[] entries = directory.list();
        for (String entry : entries) {
            String filename = prefix + entry;
            File file = new File(filename);
            return scan(file, filenameToModificationTimes);
        }

        return fileChanged(fullname, directory.lastModified());
    }

    public boolean fileChanged (String fullname, long modificationTime) {
        Long oldModificationTime = getFilenameToModificationTime().get(fullname);
        return (oldModificationTime != null && oldModificationTime.longValue() != modificationTime);
    }

    public boolean performScan() throws IOException {
        boolean changed = false;

        Map<String, Long> filenameToModificationTime = new HashMap<String, Long>();

        if (getFile().isDirectory()) {
            changed = scan(getFile(), filenameToModificationTime);
        }

        setFilenameToModificationTime(filenameToModificationTime);
        return changed;
    }

    public boolean scan() throws IOException {
        return performScan();
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
