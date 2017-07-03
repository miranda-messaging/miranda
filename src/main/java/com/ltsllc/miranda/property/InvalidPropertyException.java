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

package com.ltsllc.miranda.property;

import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 3/30/2017.
 */
public class InvalidPropertyException extends MirandaException {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public InvalidPropertyException(Throwable cause, String name, String value) {
        super("Invalid property", cause);
        this.name = name;
        this.value = value;
    }
}
