package com.ltsllc.commons.application;

import com.ltsllc.commons.commadline.CommandLine;
import com.ltsllc.commons.commadline.CommandLineException;

import java.util.Map;

/**
 * A command line application.
 */
abstract public class Application {
    abstract public CommandLine createCommandLine ();
    abstract public String getName ();
    abstract public String getUsageString ();
    abstract public Map<String, Object> createContext (CommandLine commandLine);
    abstract public Option determineOption (Map<String, Object> context);

    private CommandLine commandLine;

    public void execute (String[] argv) throws CommandLineException {
        CommandLine commandLine = createCommandLine();
        commandLine.parse(argv);
        Map<String, Object> context = createContext(commandLine);
        Option option = determineOption(context);
        option.execute(context);
    }

    public void usage() {
        System.err.println (getName() + ": " + getUsageString());
        System.exit(1);
    }


    public CommandLine getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }


}
