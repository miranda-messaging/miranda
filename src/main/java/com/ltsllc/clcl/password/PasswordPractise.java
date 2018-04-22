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

import java.util.Map;
import java.util.Scanner;

public class PasswordPractise extends Option {
    @Override
    public void execute(Map<String, Object> map) {

    }


    public void execute(CommandLine commandLine) {
        try {
            String hexString = Utils.readTextFile("password.digest");
            System.out.println("Enter password: ");
            Scanner scanner = new Scanner(System.in);
            String password = scanner.nextLine();
            MessageDigest messageDigest = new MessageDigest();
            messageDigest.update(password.getBytes());
            String digest = messageDigest.asHexString();
            if (digest.equalsIgnoreCase(hexString))
            {
                System.out.println("match");
            }
            else {
                System.out.println("difference");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
