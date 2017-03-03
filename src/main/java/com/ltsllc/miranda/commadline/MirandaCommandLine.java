package com.ltsllc.miranda.commadline;

import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by Clark on 2/28/2017.
 */
public class MirandaCommandLine extends CommandLine {
    private static Logger logger = Logger.getLogger(MirandaCommandLine.class);

    private String loggingLevel = MirandaProperties.LoggingLevel.Warning.toString();
    private String log4jFilename = MirandaProperties.DEFAULT_LOG4J_FILE;
    private String propertiesFilename = MirandaProperties.DEFAULT_PROPERTIES_FILENAME;
    private String mirandaMode = MirandaProperties.DEFAULT_MIRANDA_MODE;

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        if (loggingLevel.equalsIgnoreCase("degub") || loggingLevel.equalsIgnoreCase("debugging"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Debug.toString();
        else if (loggingLevel.equalsIgnoreCase("info") || loggingLevel.equalsIgnoreCase("information"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Info.toString();
        else if (
                loggingLevel.equalsIgnoreCase("warn") || loggingLevel.equalsIgnoreCase("warning")
                || loggingLevel.equalsIgnoreCase("default")
        ) {
            this.loggingLevel = MirandaProperties.LoggingLevel.Warning.toString();
        }
        else if (loggingLevel.equalsIgnoreCase("error"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Error.toString();
        else if (loggingLevel.equalsIgnoreCase("fatal"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Fatal.toString();
        else {
            String level = MirandaProperties.LoggingLevel.Warning.toString();
            logger.error ("Unknown logging level " + loggingLevel + " setting level to " + level);
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

    public MirandaCommandLine (String[] argv) {
        super(argv);
    }

    public Properties asProperties () {
        Properties properties = super.asProperties();

        properties.setProperty(MirandaProperties.PROPERTY_LOG4J_FILE, getLog4jFilename());
        properties.setProperty(MirandaProperties.PROPERTY_LOGGING_LEVEL, getLoggingLevel());
        properties.setProperty(MirandaProperties.PROPERTY_PROPERTIES_FILE, getPropertiesFilename());
        properties.setProperty(MirandaProperties.PROPERTY_MIRANDA_MODE, getMirandaMode());

        return properties;
    }

    public void parse () {
        super.parse();

        if (null != getArg()) {
            String mode = getArgAndAdvance();

            if (mode.equalsIgnoreCase("normal"))
                setMirandaMode(MirandaProperties.MirandaModes.Normal.toString());
            else if (mode.equalsIgnoreCase("debug"))
                setMirandaMode(MirandaProperties.MirandaModes.Debugging.toString());
            else {
                printUsage();
                throw new IllegalArgumentException ("Unknown Mirana mode: " + mode);
            }
        }

        if (null != getArg()) {
            setPropertiesFilename(getArgAndAdvance());
        }

        while (hasMoreArgs()) {
            if (getArg().equalsIgnoreCase("-debug")) {
                setMirandaMode(MirandaProperties.MirandaModes.Debugging.toString());
                setLoggingLevel(MirandaProperties.LoggingLevel.Debug.toString());
            } else if (getArg().equalsIgnoreCase("-log4j")) {
                advance();

                if (!hasMoreArgs()) {
                    throw new IllegalStateException ("Must suuply a parameter to -log4j");
                }

                setLog4jFilename(getArgAndAdvance());
            }
        }
    }

    public void printUsage () {
        System.err.println ("usage: miranda [<properties file> [<mirada mode>  [-debug] [-log4j <log4j config file>]");
    }
}
