package com.ltsllc.miranda.deliveries;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.file.states.MirandaFileReadyState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clark on 2/19/2017.
 */
public class DeliveriesFile extends SingleFile<Delivery> implements Comparer {

    public DeliveriesFile (String filename, Writer writer) {
        super(filename, writer);

        setCurrentState(new MirandaFileReadyState(this));
    }

    public Type listType() {
        return new TypeToken<List<Delivery>>() {}.getType();
    }

    public List buildEmptyList() {
        return new ArrayList<Delivery>();
    }

    public boolean equals (Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof DeliveriesFile))
            return false;

        Map<Object,Boolean> map = new HashMap<Object,Boolean>();
        return compare (map, o);
    }


    public boolean compare (Map<Object,Boolean> map, Object o) {
        if (map.containsKey(o))
            return map.get(o).booleanValue();

        if (this == o)
            return true;

        if (null == o || !(o instanceof DeliveriesFile)) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        return super.compare(map, o);
    }

}
