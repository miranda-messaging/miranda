package com.ltsllc.miranda.file;

/**
 * Created by Clark on 2/19/2017.
 */

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * A directory containing file the system should keep an eye one
 */
abstract public class Directory extends MirandaFile {
    abstract public boolean isFileOfInterest (String filename);
    abstract public MirandaFile createMirandaFile (String filename);

    private BlockingQueue<Message> writerQueue;
    private List<MirandaFile> files = new ArrayList<MirandaFile>();


    public Directory (String filename, BlockingQueue<Message> writerQueue) {
        super(filename);

        this.writerQueue = writerQueue;
    }

    public List<MirandaFile> getFiles() {
        return files;
    }

    @Override
    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public List<String> traverse () {
        List<String> mathces = new ArrayList<String>();
        traverse(getFilename(), mathces);
        return mathces;
    }

    public void traverse (String directory, List<String> matches) {
        File f = new File(directory);
        String[] contents = f.list();
        for (String file : contents)
        {
            String fullName = directory + File.separator + file;
            File entry = new File(fullName);
            if (entry.isDirectory()) {
                String name = directory + File.separator + file;
                traverse(name, matches);
            }
            else if (entry.isFile() && isFileOfInterest(file))
            {
                matches.add (fullName);
            }
        }
    }

    @Override
    public void load() {
        List<String> matches = traverse();
        for (String file : matches) {
            MirandaFile mirandaFile = createMirandaFile(file);
            mirandaFile.start();
            getFiles().add(mirandaFile);
        }
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    public void updateVersion () {
        Gson gson = new Gson();

        StringWriter stringWriter = new StringWriter();

        for (MirandaFile file : getFiles()) {
            String json = gson.toJson(file.getVersion());
            stringWriter.write(json);
        }

        Version version = new Version(stringWriter.toString());
        setVersion(version);
    }
}