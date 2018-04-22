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

import com.ltsllc.commons.util.Bag;
import com.ltsllc.commons.util.ImprovedRandom;

public class Generator {
    public String generate () {
        ImprovedRandom improvedRandom = new ImprovedRandom();
        StringBuilder stringBuilder = new StringBuilder();
        UpperCaseLetter upperCaseLetter = new UpperCaseLetter(improvedRandom);
        stringBuilder.append(upperCaseLetter.generate());
        Bag<Candidate> bag = new Bag<Candidate>();
        bag.add(new Number(improvedRandom));
        bag.add(new Symbol(improvedRandom));

        for (int i = 0; i < 6; i++) {
            bag.add(new AnyCharacter(improvedRandom));
        }

        while (!bag.empty()) {
            Candidate candidate = bag.get();
            stringBuilder.append(candidate.generate());
        }

        return stringBuilder.toString();
    }
}
