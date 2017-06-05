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

package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Updateable;

/**
 * Created by Clark on 5/18/2017.
 */
public class StandardManagerReadyState<E extends Updateable<E> & Matchable<E>> extends ManagerReadyState<E, E> {
    public StandardManager<E> getManager () {
        return (StandardManager) getContainer();
    }

    public StandardManagerReadyState (StandardManager manager) {
        super(manager);
    }
}
