package com.ltsllc.commons.application;

import com.ltsllc.commons.commadline.CommandLine;

/**
 * A command line command that consists of a number of commands like:
 *
 *     git commit
 *
 * or
 *
 *      git status
 *
 * This is the form git uses, hence the name.
 *
 * <p>
 *     An instance of this type must
 *     <ul>
 *         <li>have the command as the first argument</li>
 *     </ul>
 * </p>
 * <p>
 *     A command of this type identifies the subcommand being used,
 *     creates a subclass of {@link com.ltsllc.commons.commadline.CommandLine} to parse the command line
 *     and them performs the command.
 * </p>
 */
abstract public class GitStyle extends Application {
    public enum Command {
        Unknown(-1),
        LAST(0);

        private int index;

        private Command(int index) {
            this.index = index;
        }
    }

    private Command command;
    private CommandLine commandLine;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    /**
     * Determine which command the user wants to run.
     *
     * <p>
     *     Convert a string to a SubCommand object whose index reflects the command that the user wants to run.
     * </p>
     *
     * @param string The string to convert.
     * @return The command the user wants to
     */
    abstract public Command toCommand(String string);
    abstract public CommandLine toCommandLineParser(Command command);
    abstract public void execute ();

    public void usage() {
        System.err.println (getName() + ": " + getUsageString());
        System.exit(1);
    }


    public void error(String message) {
        System.err.println (getName() + ": " + message);
        System.exit(1);
    }

    public void execute (String[] argv) {
        if (argv.length < 1) {
            usage();
        } else {
            Command command = toCommand(argv[0]);
            setCommand(command);
            CommandLine commandLine = toCommandLineParser(command);
            setCommandLine(commandLine);
            execute();
        }
    }
}
