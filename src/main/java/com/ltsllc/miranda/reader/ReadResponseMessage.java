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

package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/14/2017.
 */
public class ReadResponseMessage extends Message {
    public enum Results {
        Success,
        FileDoesNotExist,
        ExceptionReadingFile,
        ExceptionDecryptingFile,
        Unknown
    }

    private Throwable exception;
    private String filename;
    private Results result;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public Results getResult() {
        return result;
    }

    public byte[] getData() {
        return data;
    }

    public ReadResponseMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.ReadResponse, senderQueue, sender);
    }

    public void setResult (Results result) {
        this.result = result;
    }

    public void setData (byte[] data) {
        this.data = data;
    }

    public void setFilename (String filename) {
        this.filename = filename;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
