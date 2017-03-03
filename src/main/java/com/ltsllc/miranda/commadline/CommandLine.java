package com.ltsllc.miranda.commadline;

import java.util.Properties;

public class CommandLine {
    private String[] argv;
    private int argIndex = 0;

    public CommandLine (String[] argv) {
        this.argv = argv;
        this.argIndex = 0;
    }

    public int getArgIndex() {
        return argIndex;
    }

    public boolean hasMoreArgs () {
        return getArg() != null;
    }

    public void setArgIndex(int argIndex) {
        this.argIndex = argIndex;
    }

    public void advance () {
        argIndex++;
    }

    public String getArgAndAdvance () {
        String value = argv[argIndex];
        argIndex++;
        return value;
    }


    public String[] getArgv() {
        return argv;
    }

    public void setArgv(String[] argv) {
        this.argv = argv;
    }

    public String getArg () {
        if (argIndex >= argv.length)
            return null;

        return argv[argIndex];
    }

    public Properties asProperties () {
        return new Properties();
    }

    public void parse () {
    }
}
