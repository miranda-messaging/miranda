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

package com.ltsllc.miranda.network;

/**
 * Created by Clark on 3/1/2017.
 */
public class NetworkException extends Exception {
    public enum Errors {
        ClassNotFound,
        ErrorGettingStreams,
        ExceptionSending,
        ExceptionConnecting,
        ExceptionCreating,
        ExceptionReceiving,
        InterruptedSending,
        Test,
        UnrecognizedHandle
    }

    private Errors error;

    public Errors getError() {
        return error;
    }

    public NetworkException(Errors error) {
        this.error = error;
    }


    public NetworkException(String string, Throwable throwable, Errors error) {
        super(string, throwable);

        this.error = error;
    }

    public NetworkException(Throwable throwable, Errors error) {
        super(throwable);

        this.error = error;
    }

    public NetworkException(String string, Errors error) {
        super(string);

        this.error = error;
    }
}
