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

package com.ltsllc.miranda.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/18/2017.
 */
public class ObjectFile<T> {
    private List<T> data = new ArrayList<T>();

    public List<T> getData() {
        return data;
    }

    public boolean contains(T t) {
        for (T instance : getData()) {
            if (instance.equals(t))
                return true;
        }

        return false;
    }

    public void add(T t) {
        getData().add(t);
    }
}
