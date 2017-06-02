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

package com.ltsllc.miranda.commadline;

import java.util.Properties;

public class CommandLine {
    private String[] argv;
    private int argIndex = 0;

    public CommandLine (String[] argv) {
        this.argv = argv;
        this.argIndex = 0;
    }

    public int getArgIndex() {
        return argIndex;
    }

    public boolean hasMoreArgs () {
        return getArg() != null;
    }

    public void setArgIndex(int argIndex) {
        this.argIndex = argIndex;
    }

    public void advance () {
        argIndex++;
    }

    public String getArgAndAdvance () {
        String value = argv[argIndex];
        advance();
        return value;
    }


    public String[] getArgv() {
        return argv;
    }

    public void setArgv(String[] argv) {
        this.argv = argv;
    }

    public String getArg () {
        if (argIndex >= argv.length)
            return null;

        return argv[argIndex];
    }

    public Properties asProperties () {
        return new Properties();
    }

    public void parse () {
    }

    public void backup () {
        if (argIndex > 0)
            argIndex--;
    }
}
