package test;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Utils;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestCase {
    private BlockingQueue<Message> network = new LinkedBlockingQueue<Message>();
    private BlockingQueue<Message> writer = new LinkedBlockingQueue<Message>();

    public TestCase () {

    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public BlockingQueue<Message> getWriter() {
        return writer;
    }

    public void reset () {
        network = new LinkedBlockingQueue<Message>();
        writer = new LinkedBlockingQueue<Message>();
    }


    private static final String LOG4J_CONFIG_FILENAME = "log4j.xml";

    private static final String[] LOG4J_CONFIG_FILE_CONTENTS = {
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
            "<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">",
            "<log4j:configuration debug=\"true\"",
            "                     xmlns:log4j='http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd'",
            "                     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
            "                     xsi:schemaLocation=\"http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd \">",
            "",
            "    <appender name=\"consoleAppender\" class=\"org.apache.log4j.ConsoleAppender\">",
            "        <layout class=\"org.apache.log4j.PatternLayout\">",
            "            <param name=\"ConversionPattern\" value=\"%d{dd MMM yyyy HH:mm:ss} %5p %c{1} - %m%n\"/>",
            "        </layout>",
            "    </appender>",
            "",
            "    <appender name=\"fileAppender\" class=\"org.apache.log4j.RollingFileAppender\">",
            "        <param name=\"append\" value=\"false\"/>",
            "        <param name=\"file\" value=\"out/learning.log\"/>",
            "        <layout class=\"org.apache.log4j.PatternLayout\">",
            "            <param name=\"ConversionPattern\" value=\"%d{ABSOLUTE} %-5p [%c{1}] %m%n\"/>",
            "        </layout>",
            "    </appender>",
            "",
            "    <root>",
            "        <level value=\"INFO\"/>",
            "        <appender-ref ref=\"consoleAppender\"/>",
            "        <appender-ref ref=\"fileAppender\"/>",
            "    </root>",
            "",
            "</log4j:configuration>",
    };

    public void setuplog4j () {
        putFile(LOG4J_CONFIG_FILENAME, LOG4J_CONFIG_FILE_CONTENTS);
        DOMConfigurator.configure(LOG4J_CONFIG_FILENAME);
    }

    public static void putFile (String filename, String[] contents) {
        PrintWriter out = null;

        try {
            FileWriter fileWriter = new FileWriter(filename);
            out = new PrintWriter(fileWriter);

            for (String line : contents) {
                out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            Utils.closeIgnoreExceptions(out);
        }
    }

    public static void deleteFile (String filename) {
        File f = new File(filename);
        if (f.exists()) {
            boolean result = f.delete();
            assert(result);
        }
    }


    public static void pause (long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send (Message message, BlockingQueue queue) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean contains (Message.Subjects subject, BlockingQueue<Message> queue)
    {
        for (Message m : queue) {
            if (subject.equals(m.getSubject())) {
                return true;
            }
        }

        return false;
    }
}
