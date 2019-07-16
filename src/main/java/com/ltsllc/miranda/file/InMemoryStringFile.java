package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InMemoryStringFile extends SingleFile {
    private List<String> list;

    public InMemoryStringFile(List<String> inList) {
        list = new ArrayList<>(inList);
    }

    @Override
    public List buildEmptyList() {
        return new ArrayList<String>();
    }

    @Override
    public Type getListType() {
        return new TypeToken<List<String>>(){}.getType();
    }

    @Override
    public void checkForDuplicates() {

    }

    @Override
    public void fromJson(String json) {
        InMemoryStringFile temp = getGson().fromJson(json, getListType());
    }
}
