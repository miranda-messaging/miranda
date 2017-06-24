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

import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.Message;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileResponseMessage extends Message {
    private String contents;
    private String requester;

    public GetFileResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String requester, byte[] contents) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        String hexString = Utils.bytesToString(contents);

        this.requester = requester;
        this.contents = hexString;
    }

    public GetFileResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String requester, String contents) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        this.requester = requester;
        this.contents = contents;
    }

    public GetFileResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String requester) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        this.requester = requester;
    }


    public String getContents() {
        return contents;
    }

    public byte[] getContentAsBytes () throws IOException {
        return Utils.hexStringToBytes(contents);
    }

    public String getRequester() {
        return requester;
    }
}
