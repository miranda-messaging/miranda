package com.ltsllc.miranda.clientinterface.basicclasses;

public interface Mergeable {
    public void copyFrom(Mergeable mergeable);
    public boolean merge(Mergeable other);
    public String toJson ();
    public long getLastChange ();
}
