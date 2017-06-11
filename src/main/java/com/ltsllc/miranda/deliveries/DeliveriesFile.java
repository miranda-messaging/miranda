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

package com.ltsllc.miranda.deliveries;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.states.MirandaFileReadyState;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clark on 2/19/2017.
 */
public class DeliveriesFile extends SingleFile<Delivery> implements Comparer {

    public DeliveriesFile (String filename, Reader reader, Writer writer) throws IOException {
        super(filename, reader, writer);

        setCurrentState(new MirandaFileReadyState(this));
    }

    public Type getListType() {
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

    public void checkForDuplicates () {}

}
