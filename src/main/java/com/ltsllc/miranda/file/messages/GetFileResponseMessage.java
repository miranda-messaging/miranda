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

package com.ltsllc.miranda.file.messages;

import com.ltsllc.commons.util.HexConverter;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.requests.Files;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileResponseMessage extends Message {
    private String contents;
    private Files file;

    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    public GetFileResponseMessage(BlockingQueue<Message> senderQueue, Object sender, byte[] contents) {
        super(Subjects.GetFileResponse, senderQueue, sender);
        String hexString = HexConverter.toHexString(contents);

        this.contents = hexString;
    }

    public GetFileResponseMessage(BlockingQueue<Message> senderQueue, Object sender, String contents) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        this.contents = contents;
    }

    public GetFileResponseMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetFileResponse, senderQueue, sender);
    }


    public String getContents() {
        return contents;
    }

    public byte[] getContentAsBytes() throws IOException {
        return HexConverter.toByteArray(contents);
    }

}
