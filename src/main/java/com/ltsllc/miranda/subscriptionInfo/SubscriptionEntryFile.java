package com.ltsllc.miranda.subscriptionInfo;

import com.ltsllc.miranda.directory.DirectoryEntry;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A file that contains the {@link com.ltsllc.miranda.event.Event Events} and {@link com.ltsllc.miranda.deliveries.Delivery Deliveries}
 * of a subscription.
 */
public class SubscriptionEntryFile extends SingleFile<SubscriptionEntry> {
    @Override
    public List buildEmptyList() {
        return new ArrayList<SubscriptionEntry>();
    }

    public List<SubscriptionEntry> getEntryList () {
        return getData();
    }

    public void checkForDuplicates () {
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
