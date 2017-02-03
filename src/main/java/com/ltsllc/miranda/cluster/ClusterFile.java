package com.ltsllc.miranda.cluster;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.util.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/20/2017.
 */
public class ClusterFile extends SingleFile<NodeElement> {
    private Logger logger = Logger.getLogger(ClusterFile.class);

    public ClusterFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }

    public Type getBasicType () {
        return new TypeToken<ArrayList<Node>> (){}.getType();
    }


    public void load ()
    {
        logger.info("loading " + getFilename());
        File f = new File(getFilename());
        if (!f.exists()) {
            setData(null);
        } else {
            Gson gson = new Gson();
            FileReader fr = null;
            List<NodeElement> temp = new ArrayList<NodeElement>();
            try {
                fr = new FileReader(getFilename());
                Type listType = new TypeToken<List<NodeElement>>() {}.getType();
                temp = gson.fromJson(fr, listType);
            } catch (FileNotFoundException e) {
                logger.info(getFilename() + " not found");
            } finally {
                IOUtils.closeNoExceptions(fr);
            }


            setData(temp);

            Cluster.nodesLoaded(temp);
        }

    }




}
