package com.ltsllc.miranda.subscriptionInfo;

import com.ltsllc.miranda.clientinterface.basicclasses.DirectoryEntry;
import com.ltsllc.miranda.file.SingleFile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A file that contains the {@link com.ltsllc.miranda.clientinterface.basicclasses.Event}s and
 * {@link com.ltsllc.miranda.clientinterface.basicclasses.Delivery}
 * of a subscription.
 */
public class SubscriptionEntryFile extends SingleFile<SubscriptionEntry> {
    public void fromJson(String json) {
        SubscriptionEntryFile temp = getGson().fromJson(json, getListType());

    }

    @Override
    public List buildEmptyList() {
        return new ArrayList<SubscriptionEntry>();
    }

    public List<SubscriptionEntry> getEntryList() {
        return getData();
    }

    public void checkForDuplicates() {
        List<Integer> remove = new ArrayList<Integer>();

        for (int i = 0; i < getEntryList().size(); i++) {
            DirectoryEntry current = getEntryList().get(i);
            for (int j = 0; j < getEntryList().size(); j++) {
                if (i != j && current.isEquivalentTo(getEntryList().get(j))) {
                    remove.add(i);
                }
            }
        }

        ReverseIterator<Integer> reverseIterator = new ReverseIterator(remove);
        while (reverseIterator.hasMoreElements()) {
            Integer index = reverseIterator.next();
            getData().remove(index);
        }
    }

    @Override
    public Type getListType() {
        return null;
    }
}
