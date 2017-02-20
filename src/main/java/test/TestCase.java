package test;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Utils;

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
