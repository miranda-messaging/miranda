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

import com.ltsllc.commons.util.ImprovedRandom;

abstract public class Candidate {
    abstract public String getCandidateString();

    private ImprovedRandom improvedRandom;
    private Character[] candidates;

    public Candidate (ImprovedRandom improvedRandom) {
        setImprovedRandom(improvedRandom);
    }

    public ImprovedRandom getImprovedRandom() {
        return improvedRandom;
    }

    public void setImprovedRandom(ImprovedRandom improvedRandom) {
        this.improvedRandom = improvedRandom;
    }

    public Character[] getCandidates() {
        if (null == candidates)
            candidates = toCharacterArray(getCandidateString());

        return candidates;
    }

    public void setCandidates(Character[] candidates) {
        this.candidates = candidates;
    }

    public static Character[] toCharacterArray (String string) {
        char[] ca = string.toCharArray();
        Character[] characterArray = new Character[ca.length];
        for (int i = 0; i < ca.length; i++) {
            characterArray[i] = ca[i];
        }

        return characterArray;
    }

    public char generate () {
        return getImprovedRandom().<Character>next(getCandidates());
    }
}
