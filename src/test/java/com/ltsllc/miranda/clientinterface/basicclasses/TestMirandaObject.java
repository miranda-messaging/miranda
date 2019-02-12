package com.ltsllc.miranda.clientinterface.basicclasses;

import org.apache.mina.util.byteaccess.ByteArray;
import org.junit.Before;
import org.junit.Test;

public class TestMirandaObject {
    private MirandaObject object;

    public MirandaObject getObject() {
        return object;
    }

    public void setObject(MirandaObject object) {
        this.object = object;
    }

    @Test
    public void testStringsAreEqual () {
        assert (!MirandaObject.stringsAreEqual("dog", "cat"));
        assert (!MirandaObject.stringsAreEqual("dog",null));
        assert (!MirandaObject.stringsAreEqual(null, "dog"));
        assert (MirandaObject.stringsAreEqual("dog", "dog"));
    }

    @Test
    public void testLongObjectsAreEqual () {
        Long l1 = new Long(1);
        Long l2 = new Long(2);
        assert (MirandaObject.longObjectsAreEquivalent(l1,l1));
        assert (!MirandaObject.longObjectsAreEquivalent(l1, null));
        assert (!MirandaObject.longObjectsAreEquivalent(null, l1));
        assert (!MirandaObject.longObjectsAreEquivalent(l1,l2));
    }

    @Test
    public void testByteArraysAreEqual () {
        byte[] byteArray1 = { 1 };
        byte[] byteArray2 = { 2 };
        assert (MirandaObject.byteArraysAreEqual(byteArray1, byteArray1));
        assert (!MirandaObject.byteArraysAreEqual(byteArray1, byteArray2));
        assert (!MirandaObject.byteArraysAreEqual(byteArray1, null));
        assert (!MirandaObject.byteArraysAreEqual(null, byteArray1));
    }

    String json = "{\"name\":\"hi there\"}";
    @Test
    public void testToJson  () {
        Topic topic = new Topic("hi there");
        String topicString = topic.toJson();
        assert (topicString.equals(json));
    }
}
