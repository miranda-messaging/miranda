package com.ltsllc.miranda.server;

/**
 * Created by Clark on 2/18/2017.
 */
abstract public class NewObjectPostHandler<T> extends HttpPostHandler {
    private T file;

    public NewObjectPostHandler (T file) {
        this.file = file;
    }

    public T getFile() {
        return file;
    }
}
