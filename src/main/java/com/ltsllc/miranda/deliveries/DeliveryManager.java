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

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;

/**
 * Created by Clark on 5/1/2017.
 */
public class DeliveryManager extends DirectoryManager {
    public static final String NAME = "delivery manager";

    public DeliveryManager(String directory, int objectLimit, Reader reader, Writer writer) throws IOException, MirandaException {
        super(NAME, directory, objectLimit, reader, writer);
    }

    public MirandaFile getFile(String name) throws MirandaException
    {
        try {
            Miranda miranda = Miranda.getInstance();
            return new DeliveriesFile(name, miranda.getReader(), miranda.getWriter());
        } catch (IOException e) {
            throw new MirandaException(e);
        }
    }


}
