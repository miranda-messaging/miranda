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

package com.ltsllc.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/23/2017.
 */
public class Bag<E> {
    private static ImprovedRandom random = new ImprovedRandom();

    private List<E> components = new ArrayList<E>();

    public Bag () {
    }

    public void add (E item) {
        components.add(item);
    }

    public E get() {
        int index = random.nextIndex(components.size());
        return components.remove(index);
    }

    public boolean empty () {
        return components.size() <= 0;
    }

}
