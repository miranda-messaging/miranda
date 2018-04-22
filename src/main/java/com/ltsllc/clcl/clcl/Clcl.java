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

package com.ltsllc.clcl.clcl;


import com.ltsllc.clcl.password.PasswordCreator;
import com.ltsllc.clcl.password.PasswordPractise;
import com.ltsllc.commons.application.Option;
import com.ltsllc.commons.application.TwoLevelApplication;
import com.ltsllc.commons.commadline.CommandLine;

public class Clcl extends TwoLevelApplication {
    public enum ClclObjects {
        Password (Objects.LAST.getIndex() + 1);

        int index;

        ClclObjects(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public static ClclObjects toClclObject (Objects object) {
            ClclObjects returnValue = null;

            if (object.getIndex() == ClclObjects.Password.getIndex())
                returnValue = ClclObjects.Password;

            return returnValue;
        }
    }

    public enum ClclPredicates {
        Create (Predicates.LAST.getIndex() + 1),
        Practice (Predicates.LAST.getIndex() + 2);

        int index;

        ClclPredicates(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public static ClclPredicates toClclPredicte (Predicates predicate) {

            ClclPredicates returnValue = null;

            
            if (predicate.getIndex() == Create.getIndex())
                returnValue = Create;
            else if (predicate.getIndex() == ClclPredicates.Practice.getIndex())
                returnValue = ClclPredicates .Practice;

            return returnValue;
        }
    }

    public static void main (String[] argv) {
        Clcl clcl = new Clcl();
        clcl.run(argv);
    }

    @Override
    public Objects toObject(String string) {
        Objects object = Objects.Unknown;

        if (string.equalsIgnoreCase("password")) {
            object = Objects.LAST;
            object.setIndex(ClclObjects.Password.getIndex());
        }

        return object;
    }

    @Override
    public Predicates toPredicate(String string) {
        Predicates predicate = null;

        if (string.equalsIgnoreCase("create")) {
            predicate = Predicates.LAST;
            predicate.setIndex(ClclPredicates.Create.getIndex());
        } else if (string.equalsIgnoreCase("practise")) {
            predicate = Predicates.LAST;
            predicate.setIndex(ClclPredicates.Practice.getIndex());
        }

        return predicate;
    }

    @Override
    public String getUsageString() {
        return "clcl <object> <pedicate>";
    }

    @Override
    public Option getOption() {
        ClclObjects clclObject = ClclObjects.toClclObject(getObject());
        ClclPredicates clclPredicate = ClclPredicates.toClclPredicte(getPredicate());

        Option option = null;

        switch (clclObject) {
            case Password: {
                switch (clclPredicate) {
                    case Create:
                        option = new PasswordCreator();
                        break;

                    case Practice:
                        option = new PasswordPractise();
                        break;
                }
            }
        }

        return option;
    }

    @Override
    public CommandLine getCommandLine(String[] argv) {
        return null;
    }
}
