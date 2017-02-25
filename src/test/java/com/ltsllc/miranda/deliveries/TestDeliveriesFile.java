package com.ltsllc.miranda.deliveries;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.file.MirandaProperties;
import org.junit.Before;
import org.junit.Test;
import com.ltsllc.miranda.test.TestCase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDeliveriesFile extends TestCase {
    private DeliveriesFile deliveriesFile;

    public DeliveriesFile getDeliveriesFile() {
        return deliveriesFile;
    }

    @Before
    public void setup () {
        reset();

        setupMirandaProperties();
        MirandaProperties properties = MirandaProperties.getInstance();

        String directory = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
        this.deliveriesFile = new DeliveriesFile(directory, getWriter());
    }

    @Test
    public void testConstructor () {
        DeliveriesFileReadyState readyState = new DeliveriesFileReadyState(getDeliveriesFile());

        assert (getDeliveriesFile().getCurrentState().equals(readyState));
    }

    @Test
    public void testListType () {
        Type theirType = getDeliveriesFile().listType();
        Type localType = new TypeToken<List<Delivery>>() {}.getType();

        assert (theirType.equals(localType));
    }

    @Test
    public void testBuildEmptyList () {
        List<Delivery> local = new ArrayList<Delivery>();
        List theirs = getDeliveriesFile().buildEmptyList();

        assert (local.equals(theirs));
    }
}
