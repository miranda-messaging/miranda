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

package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 3/18/2017.
 */
public class SingleFileStoppingState extends State {
    private static Logger logger = Logger.getLogger(SingleFileStoppingState.class);

    private int numberOfWriteFailures;

    public int getNumberOfWriteFailures() {
        return numberOfWriteFailures;
    }

    public void setNumberOfWriteFailures(int numberOfWriteFailures) {
        this.numberOfWriteFailures = numberOfWriteFailures;
    }

    public void incrementNumberOfWriteFailures() {
        numberOfWriteFailures++;
    }

    public SingleFile getSingleFile() {
        return (SingleFile) getContainer();
    }

    public SingleFileStoppingState(SingleFile singleFile) throws MirandaException {
        super(singleFile);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case WriteSucceeded: {
                WriteSucceededMessage writeSucceededMessage = (WriteSucceededMessage) message;
                nextState = processWriteSucceededMessage(writeSucceededMessage);
                break;
            }

            case WriteFailed: {
                WriteFailedMessage writeFailedMessage = (WriteFailedMessage) message;
                nextState = processWriteFailedMessage(writeFailedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processWriteSucceededMessage(WriteSucceededMessage writeSucceededMessage) {
        logger.info(getSingleFile() + " stopping");
        return StopState.getInstance();
    }

    public State processWriteFailedMessage(WriteFailedMessage writeFailedMessage) {
        incrementNumberOfWriteFailures();

        int maxFailures = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_MAX_WRITE_FAILURES);

        if (getNumberOfWriteFailures() >= maxFailures) {
            String message = "Could not write out " + getSingleFile().getFilename() + " after " + getNumberOfWriteFailures()
                    + " tries.  Giving up.";
            logger.error(message, writeFailedMessage.getCause());
        }

        return this;
    }
}
