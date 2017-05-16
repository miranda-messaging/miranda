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

package com.ltsllc.miranda;

import java.io.Serializable;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/2/2017.
 */
abstract public class Key implements Serializable {
    public byte[] encrypt (String clearText) throws GeneralSecurityException{
        return encrypt(clearText.getBytes());
    }

    abstract byte[] encrypt (byte[] clearText) throws GeneralSecurityException;
    abstract byte[] decrypt (byte[] cipherText) throws GeneralSecurityException;
}
