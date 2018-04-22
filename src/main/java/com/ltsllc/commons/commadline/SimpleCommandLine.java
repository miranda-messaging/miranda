package com.ltsllc.commons.commadline;

/**
 * A class that parses the command line to a java command
 */
public class SimpleCommandLine {
    private String[] argv;
    private int argIndex;

    public SimpleCommandLine (String[] argv)
    {
        this.argv = argv;
        argIndex = 0;
    }
}
