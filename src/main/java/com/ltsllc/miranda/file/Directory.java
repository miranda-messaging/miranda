package com.ltsllc.miranda.file;

/**
 * Created by Clark on 2/19/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.file.states.DirectoryReadyState;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * A directory containing file the system should keep an eye one
 */
abstract public class Directory extends MirandaFile {
    abstract public boolean isFileOfInterest (String filename);
    abstract public MirandaFile createMirandaFile (String filename);

    private List<MirandaFile> files = new ArrayList<MirandaFile>();

    public Directory (String filename, Reader reader, Writer writer) {
        super(filename, reader, writer);

        DirectoryReadyState readyState = new DirectoryReadyState(this);
        setCurrentState(readyState);
    }

    public List<MirandaFile> getFiles() {
        return files;
    }

    public void setFiles(List<MirandaFile> files) {
        this.files = files;
    }

    public List<String> traverse () {
        List<String> mathces = new ArrayList<String>();
        traverse(getFilename(), mathces);
        return mathces;
    }

    public List<String> traverse (String root) {
        List<String> matches = new ArrayList<String>();
        traverse(root, matches);
        return matches;
    }

    public void traverse (String directory, List<String> matches) {
        File f = new File(directory);
        String[] contents = f.list();
        if (null == contents)
            getFiles().clear();
        else {
            for (String file : contents) {
                String fullName = directory + File.separator + file;
                File entry = new File(fullName);
                if (entry.isDirectory()) {
                    String name = directory + File.separator + file;
                    traverse(name, matches);
                } else if (entry.isFile() && isFileOfInterest(file)) {
                    matches.add(fullName);
                }
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

        long now = System.currentTimeMillis();
        setLastLoaded(now);
    }


    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    public void updateVersion () throws NoSuchAlgorithmException {
        List<Version> list = new ArrayList<Version>();

        for (MirandaFile file : getFiles()) {
            file.updateVersion();
            list.add(file.getVersion());
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(list);

        Version version = new Version(json);
        setVersion(version);
    }

    public void write() {
        for (MirandaFile file : getFiles())
        {
            file.write();
        }
    }
}
