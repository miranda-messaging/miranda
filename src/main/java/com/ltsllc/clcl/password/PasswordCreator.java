/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.clcl.password;

import com.ltsllc.clcl.MessageDigest;
import com.ltsllc.commons.application.Option;
import com.ltsllc.commons.commadline.CommandLine;
import com.ltsllc.commons.util.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class PasswordCreator extends Option {
    @Override
    public void execute(Map<String, Object> map) {

    }

    public void execute(CommandLine commandLine) {
        try {
            Generator generator = new Generator();
            String password = generator.generate();
            System.out.println(password);

            MessageDigest messageDigest = new MessageDigest();
            messageDigest.update(password.getBytes());

            String filename = "password.digest";
            String hexString = messageDigest.asHexString();
            try {
                Utils.writeTextFile(filename, hexString);
            } catch (IOException e) {
                System.err.println("Error writing file " + filename);
                e.printStackTrace();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

}
