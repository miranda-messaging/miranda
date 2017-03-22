package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.objects.ServletMapping;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/9/2017.
 */
public class SetupServletsMessage extends Message {
    private List<ServletMapping> mappings;

    public List<ServletMapping> getMappings() {
        return mappings;
    }

    public SetupServletsMessage(BlockingQueue<Message> senderQueue, Object sender, ServletMapping[] mappings) {
        super(Subjects.SetupServlets, senderQueue, sender);

        this.mappings = Arrays.asList(mappings);
    }
}
