/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.commons.application;

import com.ltsllc.commons.commadline.CommandLine;

/**
 * Applications that have a command line like &lt;name&gt; &gt;noun&lt; &gt;action&lt;
 * For example:
 * <pre>
 *     clcl password create
 * </pre>
 * or
 * <pre>
 *     clcl password practice
 * </pre>
 *
 *     <p>
 *         Application like this
 *         <ul>
 *             <li>have a noun (referred to as the object) as their first argument</li>
 *             <li>have a verb (referred to as the pedicate) as their second argument</li>
 *             <li>have any switches after that</li>
 *         </ul>
 *    </p>
 *
 *
 *     <p>
 *         Application like this:
 *         <ul>
 *             <li>Determine their object</li>
 *             <li>Determine a predicate approriate to the object</li>
 *             <li>Come up with a command line</li>
 *             <li>Execute</li>
 *         </ul>
 *     </p>
 *
 */
abstract public class TwoLevelApplication {
    /**
     * Return a string suitable for use in an error message of the form:
     *
     * <pre>
     *     usage : &lt;usage string&gt;
     * </pre>
     * @return The usage string as described above.
     */
    abstract public String getUsageString();

    /**
     * Convert a string to the onject for the application.
     *
     * @param string The string to convert.  This should not be null.
     * @return The object that corresponds to the String or {@link Objects#Unknown} if the string is not recognized.
     *          The index of the Object returned may not match the value.
     */
    abstract public Objects toObject (String string);

    /**
     * Convert a string to a predicit appropriate to the object or {@link Predicates#Unknown} if the string is not
     * recoginized for the object.
     *
     * <p>
     *     The object should be defined before calling this method.
     *     That is, {@link #getObject()} should return a meaningful value.
     * </p>
     *
     * @param string The string that contains the predicate.
     * @return The predicate for the object.
     */
    abstract public Predicates toPredicate (String string);

    /**
     * If any command line options are appropriate for the object and predicate return them.
     *
     * @return The command line options for the object and predicate, or null if that combination does not accept
     * arguments.
     */
    abstract public CommandLine getCommandLine (String[] argv);

    /**
     * Return an {@link Option} appropriate to the object/pedicate combination; or null if the object and predicate
     * cannot be used together.
     *
     * <p>
     *     Both {@link #getObject()} and {@link #getPredicate()} should return meaingful values.
     * </p>
     *
     * @return An option that represents the object/predicate combination or null if the two cannot be used together.
     */
    abstract public Option getOption ();

    public enum Objects {
        Unknown(-1),
        LAST(0);

        int index;

        Objects(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public enum Predicates {
        Unknown (-1),
        LAST (0);

        int index;

        Predicates(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    private Objects object;
    private Predicates predicate;
    private CommandLine commandLine;

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public Predicates getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicates predicate) {
        this.predicate = predicate;
    }

    public Objects getObject() {
        return object;
    }

    public void setObject(Objects object) {
        this.object = object;
    }

    public void printUsageAndExit (int status) {
        System.err.println("usage : " +getUsageString());
        System.exit(status);
    }

    /**
     * Run the application.
     *
     * <p>
     *     The application checks that the user called it with a suitable command line and that the object and predicat
     *     were recognized. and creates a command line and calls {@link #execute()}.
     * </p>
     * @param argv
     */
    public void run (String[] argv) {
        if (argv.length < 1) {
            System.err.println("Missing object");
            printUsageAndExit(1);
        }

        Objects object = toObject(argv[0]);

        if (object == Objects.Unknown || null == object) {
            System.err.println ("Unrecognized object: " + argv[0]);
            printUsageAndExit(2);
        }
        setObject(object);

        if (argv.length < 2) {
            System.err.println("Missing predicate");
            printUsageAndExit(1);
        }

        Predicates predicate = toPredicate(argv[1]);

        if (predicate == Predicates.Unknown || null == predicate) {
            System.err.println("The String, " + argv[1] + " is not recognized for the object " + argv[0]);
            printUsageAndExit(3);
        }
        setPredicate(predicate);

        CommandLine commandLine = getCommandLine(argv);
        if (commandLine == null && argv.length > 2) {
            System.err.println ("The object/predicate combination does not accept arguments");
            printUsageAndExit(4);
        }

        Option option = getOption();
        if (null == option) {
            System.err.println ("The combination of " + argv[0] + " and " + argv[1] + " is invalid.");
            printUsageAndExit(5);
        }

        // option.execute(commandLine);
    }
}
