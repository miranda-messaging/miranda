package com.ltsllc.miranda.clientinterface;

import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.security.GeneralSecurityException;

public class TestVersion extends TestCase {
    private Version version1;
    private Version version2;

    public Version getVersion1() {
        return version1;
    }

    public void setVersion1(Version version1) {
        this.version1 = version1;
    }

    public Version getVersion2() {
        return version2;
    }

    public void setVersion2(Version version2) {
        this.version2 = version2;
    }

    @Before
    public void setup ()  {
        version1 = getGson().fromJson("{ sha256: 1234567890 }", Version.class);
        version2 = getGson().fromJson("{ sha256: 0987654321 }", Version.class);
        assert(version1 != version2);
    }

    @Test
    public void testEquals () {
        assert (!getVersion1().equals(getVersion2()));
        getVersion2().setSha256(getVersion1().getSha256());
        assert (getVersion1().equals(getVersion2()));
    }
}
