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

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.GeneralSecurityException;

/**
 * Created by Clark on 3/28/2017.
 */
public class TestVersion extends TestCase {
    private static Logger logger = Logger.getLogger(TestVersion.class);

    private Version version;

    public Version getVersion() {
        return version;
    }

    public void reset() throws MirandaException {
        super.reset();

        version = null;
    }

    @Before
    public void setup() throws MirandaException {
        reset();

        super.setup();

        version = new Version();
    }

    @After
    public void cleanup() {
        deleteFile(TEST_FILENAME);
    }

    public static final String TEST_STRING = "whatever";
    public static final String TEST_SHA1 = "D869DB7FE62FB07C25A0403ECAEA55031744B5FB";

    @Test
    public void testConsrutctor() throws GeneralSecurityException {
        logger.info("constructor");

        deleteFile(TEST_FILENAME);
        version = new Version(TEST_STRING);
        assert (version.getSha256().equals(TEST_SHA1));
    }

    public static String TEST_FILENAME = "testfile";
    public static String TEST_BINARY_SHA1 = "E82C04D32DA09DC7F41581A5922E342B194FC67089A4A29345BB3B7C42D4DC91";

    @Test
    public void testEquals() {
        logger.info("equals");

        getVersion().setSha256(TEST_BINARY_SHA1);
        assert (getVersion().equals(getVersion()));
        assert (!getVersion().equals(null));
    }
}
