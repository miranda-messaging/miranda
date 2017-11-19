package com.ltsllc.miranda.subscriptionInfo;

import java.util.List;

/**
 * An iterator that traverses a list backwards.
 * <p>
 * <p>
 * <h3>PROPERTIES</h3>
 * <ul>
 * <li>list --- The List that this instance is iterating over.</li>
 * <li>index --- The index of the next element to be returned by {@link ReverseIterator#next}</li>
 * </ul>
 */
public class ReverseIterator<T> {
    private List list;
    private int index;

    public ReverseIterator(List list) {
        this.list = list;
        this.index = list.size() - 1;
    }

    public List getList() {
        return list;
    }

    public int getIndex() {
        return index;
    }

    /**
     * Decrement the index by 1.
     */
    public void backup() {
        index--;
    }

    /**
     * Will {@link ReverseIterator#next()} return non-null?
     *
     * @return <code>true</code> if {@link ReverseIterator#next()} will retur non-null.  <code>false</code> otherwise.
     */
    public boolean hasMoreElements() {
        return (index > -1 && index < getList().size());
    }

    /**
     * The next entry in the list, null otherwise - this operation bill "advance" the iterator.
     * <p>
     * Calling this method will go to the previous element, if there is one.  If this instance is
     * already before the first element, then callling this method shall have no effect.
     *
     * @return The current element, or <code>null</code> if we are before the first element.
     */
    public T next() {
        T item = null;
        if (getIndex() < getList().size()) {
            item = (T) getList().get(getIndex());
            backup();
        }

        return item;
    }
}
