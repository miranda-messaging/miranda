/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.commadline;

import com.ltsllc.commons.commadline.CommandLine;
import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * The arguments to the program.
 * <p>
 * <p>
 * Note that the default value for things like the properties filename is
 * null.  This means it was absent from the arguments.
 * </p>
 */
public class MirandaCommandLine extends CommandLine {
    public enum Options {
        Unknown(1 + CommandLine.Switches.LAST.getIndex()),

        Debug(2 + Switches.LAST.getIndex()),
        LoggingLevel(3 + Switches.LAST.getIndex()),
        Log4j(4 + Switches.LAST.getIndex()),
        Mode(5 + Switches.LAST.getIndex()),
        Password(6 + Switches.LAST.getIndex()),
        Properties(7 + Switches.LAST.getIndex()),
        Keystore(8 + Switches.LAST.getIndex()),
        TrustoreFile(9 + Switches.LAST.getIndex()),
        TrustorePassword(9 + Switches.LAST.getIndex());

        private int index;

        Options(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static Options toOption(Switches aSwitch) {
            Options option = Unknown;

            if (aSwitch.getIndex() == Debug.getIndex())
                option = Debug;
            else if (aSwitch.getIndex() == LoggingLevel.getIndex())
                option = LoggingLevel;
            else if (aSwitch.getIndex() == Log4j.getIndex())
                option = Log4j;
            else if (aSwitch.getIndex() == Mode.getIndex())
                option = Mode;
            else if (aSwitch.getIndex() == Password.getIndex())
                option = Password;
            else if (aSwitch.getIndex() == Properties.getIndex())
                option = Properties;
            else if (aSwitch.getIndex() == TrustoreFile.getIndex())
                option = TrustoreFile;
            else if (aSwitch.getIndex() == TrustorePassword.getIndex())
                option = TrustorePassword;

            return option;
        }
    }

    public static final String OPTION_DEBUGGING_MODE_SHORT = "-d";
    public static final String OPTION_DEBUGGING_MODE_LONG = "--debug";

    public static final String OPTION_LOGGING_LEVEL_SHORT = "-l";
    public static final String OPTION_LOGGING_LEVEL_LONG = "--loggingLevel";

    public static final String OPTION_MODE_SHORT = "-m";
    public static final String OPTION_MODE_LONG = "--mode";

    public static final String OPTION_KEYSTORE_PASSWORD_SHORT = "-p";
    public static final String OPTION_KEYSTORE_PASSWORD_LONG = "--keyStorePassword";

    public static final String OPTION_PROPERTIES_SHORT = "-r";
    public static final String OPTION_PROPERTIES_LONG = "--properties";

    public static final String OPTION_LOG4J_SHORT = "-4";
    public static final String OPTION_LOG4J_LONG = "--log4j";

    public static final String OPTION_TRUSTORE_FILE_SHORT = "-t";
    public static final String OPTION_TRUSTORE_FILE_LONG = "--trustStoreFile";

    public static final String OPTION_TRUSTORE_PASSWORD_SHORT = "-u";
    public static final String OPTION_TRUSTORE_PASSWORD_LONG = "--trustStorePassword";

    public static final String USAGE = "miranda [-d] [-l <logging level>] [-m <mode>] [-p <keystore password>] [-r <properties file>] [-4 <log4j XML file>] [-t <trustore password>]";

    private static Logger logger = Logger.getLogger(MirandaCommandLine.class);

    private String loggingLevel;
    private String log4jFilename;
    private String propertiesFilename;
    private String mirandaMode;
    private String password;
    private boolean error;
    private String trustorePassword;

    public String getUsageString() {
        return USAGE;
    }

    public String getTrustorePassword() {
        return trustorePassword;
    }

    public void setTrustorePassword(String trustorePassword) {
        this.trustorePassword = trustorePassword;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public Switches toSwitch(String argument) {
        Switches aSwitch = Switches.PlaceHolder;

        if (null == argument)
            aSwitch.setIndex(Options.Unknown.getIndex());
        else if (argument.equals(OPTION_DEBUGGING_MODE_SHORT) || argument.equals(OPTION_DEBUGGING_MODE_LONG))
            aSwitch.setIndex(Options.Debug.getIndex());
        else if (argument.equals(OPTION_LOGGING_LEVEL_SHORT) || argument.equals(OPTION_LOGGING_LEVEL_LONG)) {
            aSwitch.setIndex(Options.LoggingLevel.getIndex());
        } else if (argument.equals(OPTION_MODE_SHORT) || argument.equals(OPTION_MODE_LONG))
            aSwitch.setIndex(Options.Mode.getIndex());
        else if (argument.equals(OPTION_KEYSTORE_PASSWORD_SHORT) || argument.equals(OPTION_KEYSTORE_PASSWORD_LONG))
            aSwitch.setIndex(Options.Keystore.getIndex());
        else if (argument.equals(OPTION_PROPERTIES_SHORT) || argument.equals(OPTION_PROPERTIES_LONG))
            aSwitch.setIndex(Options.TrustorePassword.getIndex());
        else if (argument.equals(OPTION_LOG4J_SHORT) || argument.equals(OPTION_LOG4J_LONG))
            aSwitch.setIndex(Options.Log4j.getIndex());
        else if (argument.equals(OPTION_TRUSTORE_FILE_SHORT) || argument.equals(OPTION_TRUSTORE_FILE_LONG))
            aSwitch.setIndex(Options.TrustoreFile.getIndex());
        else if (argument.equals(OPTION_TRUSTORE_PASSWORD_SHORT) || argument.equals(OPTION_TRUSTORE_PASSWORD_LONG))
            aSwitch.setIndex(Options.TrustorePassword.getIndex());

        return aSwitch;
    }

    public void setLoggingLevel(String loggingLevel) {
        if (loggingLevel.equalsIgnoreCase("debug") || loggingLevel.equalsIgnoreCase("debugging"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Debug.toString();
        else if (loggingLevel.equalsIgnoreCase("info") || loggingLevel.equalsIgnoreCase("information"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Info.toString();
        else if (
                loggingLevel.equalsIgnoreCase("warn") || loggingLevel.equalsIgnoreCase("warning")
                        || loggingLevel.equalsIgnoreCase("default")
                ) {
            this.loggingLevel = MirandaProperties.LoggingLevel.Warning.toString();
        } else if (loggingLevel.equalsIgnoreCase("error"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Error.toString();
        else if (loggingLevel.equalsIgnoreCase("fatal"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Fatal.toString();
        else {
            String level = MirandaProperties.LoggingLevel.Warning.toString();
            logger.error("Unknown logging level " + loggingLevel + " setting level to " + level);
            this.loggingLevel = level;
        }
    }

    public String getPropertiesFilename() {
        return propertiesFilename;
    }

    public void setPropertiesFilename(String propertiesFilename) {
        this.propertiesFilename = propertiesFilename;
    }

    public String getLog4jFilename() {
        return log4jFilename;
    }

    public void setLog4jFilename(String log4jFilename) {
        this.log4jFilename = log4jFilename;
    }

    public String getMirandaMode() {
        return mirandaMode;
    }

    public void setMirandaMode(String mirandaMode) {
        this.mirandaMode = mirandaMode;
    }

    public MirandaCommandLine(String[] argv) {
        super(argv);
    }

    public Properties asProperties() {
        Properties properties = super.asProperties();

        if (null != getLog4jFilename())
            properties.setProperty(MirandaProperties.PROPERTY_LOG4J_FILE, getLog4jFilename());

        if (null != getPropertiesFilename())
            properties.setProperty(MirandaProperties.PROPERTY_PROPERTIES_FILE, getPropertiesFilename());

        return properties;
    }

    public void processSwitch(Switches aSwitch) {
        Options option = Options.toOption(aSwitch);

        switch (option) {
            case Debug: {
                processDebug();
                break;
            }

            case Log4j: {
                processLog4j();
                break;
            }

            case LoggingLevel: {
                processLoggingLevel();
                break;
            }

            case Mode: {
                processMode();
                break;
            }

            case Password: {
                processPassword();
                break;
            }

            case Properties: {
                processProperties();
                break;
            }

            case TrustorePassword: {
                processTrustorePassword();
                break;
            }

            default: {
                backup();
                error("Unknown option: " + getArgAndAdvance());
                break;
            }
        }
    }

    public void error(String message) {
        System.err.println(message);
        System.err.println(USAGE);
        setError(true);
    }

    public void processDebug() {
        setMirandaMode(MirandaProperties.MirandaModes.Debugging.toString());
        setLoggingLevel(MirandaProperties.LoggingLevel.Debug.toString());
    }

    public void processLog4j() {
        if (hasMoreArgs())
            setLog4jFilename(getArgAndAdvance());
        else
            error("missing log4j properties file argument");
    }

    public void processMode() {
        String mode = getArgAndAdvance();

        if (mode.equalsIgnoreCase("normal"))
            setMirandaMode(MirandaProperties.MirandaModes.Normal.toString());
        else if (mode.equalsIgnoreCase("debug"))
            setMirandaMode(MirandaProperties.MirandaModes.Debugging.toString());
        else {
            String message = "Unknown mode: " + mode;
            error(message);
        }
    }

    public void processLoggingLevel() {
        if (!hasMoreArgs()) {
            error("Missing logging level argument");
            return;
        }

        if (getArg().equalsIgnoreCase("debug") || getArg().equalsIgnoreCase("debugging"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Debug.toString();
        else if (getArg().equalsIgnoreCase("info") || getArg().equalsIgnoreCase("information"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Info.toString();
        else if (
                getArg().equalsIgnoreCase("warn") || getArg().equalsIgnoreCase("warning")
                        || getArg().equalsIgnoreCase("default")
                ) {
            this.loggingLevel = MirandaProperties.LoggingLevel.Warning.toString();
        } else if (getArg().equalsIgnoreCase("error"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Error.toString();
        else if (getArg().equalsIgnoreCase("fatal"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Fatal.toString();
        else {
            String message = "Unknown logging level: " + getArg() + ". Setting logging level to warning";
            error(message);
            this.loggingLevel = MirandaProperties.LoggingLevel.Warning.toString();
        }

    }

    public void processPassword() {
        if (hasMoreArgs())
            setPassword(getArgAndAdvance());
        else
            error("Missing password argument");
    }

    public void processProperties() {
        if (hasMoreArgs())
            setPropertiesFilename(getArgAndAdvance());
        else
            error("Missing properties file argument");
    }

    public void processTrustorePassword() {
        if (hasMoreArgs())
            setTrustorePassword(getArgAndAdvance());
        else
            error("Missing trustore password argument");
    }
}
