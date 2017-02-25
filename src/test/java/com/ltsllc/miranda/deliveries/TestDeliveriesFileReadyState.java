package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.file.MirandaProperties;
import org.junit.Before;
import org.junit.Test;
import com.ltsllc.miranda.test.TestCase;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDeliveriesFileReadyState extends TestCase {
    private DeliveriesFile deliveriesFile;
    private DeliveriesFileReadyState deliveriesFileReadyState;

    public DeliveriesFile getDeliveriesFile() {
        return deliveriesFile;
    }

    public DeliveriesFileReadyState getDeliveriesFileReadyState() {
        return deliveriesFileReadyState;
    }

    public void reset () {
        super.reset();

        deliveriesFile = null;
        deliveriesFileReadyState = null;
    }

    @Before
    public void setup() {
        reset();

        setupMirandaProperties();
        MirandaProperties properties = MirandaProperties.getInstance();
        String directory = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
        deliveriesFile = new DeliveriesFile(directory, getWriter());

        deliveriesFileReadyState = new DeliveriesFileReadyState(deliveriesFile);
    }

    @Test
    public void testConstructor () {
        assert(getDeliveriesFileReadyState().getContainer() == getDeliveriesFile());
    }

    @Test
    public void testEquals () {
        assert(getDeliveriesFileReadyState().equals(getDeliveriesFileReadyState()));

        //
        // equals should return true to a different but equivalent deliveries file
        //
        MirandaProperties properties = MirandaProperties.getInstance();
        String directory = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
        DeliveriesFile temp = new DeliveriesFile(directory, getWriter());

        DeliveriesFileReadyState eqivalent = new DeliveriesFileReadyState(temp);

        assert(eqivalent.equals(eqivalent));

        //
        // make sure equals returns false in some cases
        //
        temp = new DeliveriesFile("junk", getWriter());
        DeliveriesFileReadyState notEquivalent = new DeliveriesFileReadyState(temp);
        Integer i = new Integer(13);
        assert (!getDeliveriesFileReadyState().equals(i));
        assert (!notEquivalent.equals(getDeliveriesFileReadyState()));
        assert (!getDeliveriesFileReadyState().equals(null));
    }
}
