package com.ltsllc.miranda.clientinterface;

import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import org.junit.Before;
import org.junit.Test;

import java.security.GeneralSecurityException;

public class TestVersion {
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
    public void setup () throws GeneralSecurityException {
        version1 = new Version("hi there");
        version2 = new Version( "low there");
    }

    @Test
    public void testEquals () {
        assert (!getVersion1().equals(getVersion2()));
        getVersion2().setSha256(getVersion1().getSha256());
        assert (getVersion1().equals(getVersion2()));
    }
}
