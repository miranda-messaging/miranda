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

package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class MinaHandle extends Handle {
    private IoSession ioSession;

    public IoSession getIoSession() {
        return ioSession;
    }

    public MinaHandle(IoSession ioSession, BlockingQueue<Message> queue) {
        super(queue);
        this.ioSession = ioSession;
    }

    public void send(WireMessage wireMessage) throws NetworkException {
        try {
            String json = wireMessage.toJson();
            IoBuffer ioBuffer = IoBuffer.allocate(json.length());
            Charset charset = Charset.defaultCharset();
            ioBuffer.putString(json, charset.newEncoder());
            ioBuffer.flip();
            ioSession.write(ioBuffer);
        } catch (CharacterCodingException e) {
            throw new NetworkException("Exception trying to send", e, NetworkException.Errors.ExceptionSending);
        }
    }

    public void close() {
        ioSession.closeNow();
    }

    public void panic() {
        close();
    }


}
