package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.writer.Writer;
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

        super.setup();

        setupWriter();
        setupMirandaProperties();
        MirandaProperties properties = Miranda.properties;
        String directory = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
        deliveriesFile = new DeliveriesFile(directory, Writer.getInstance());

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
        MirandaProperties properties = Miranda.properties;
        String directory = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
        DeliveriesFile temp = new DeliveriesFile(directory, Writer.getInstance());

        DeliveriesFileReadyState eqivalent = new DeliveriesFileReadyState(temp);

        assert(eqivalent.equals(eqivalent));

        //
        // make sure equals returns false in some cases
        //
        temp = new DeliveriesFile("junk", Writer.getInstance());
        temp.load();

        DeliveriesFileReadyState notEquivalent = new DeliveriesFileReadyState(temp);
        Integer i = new Integer(13);
        getDeliveriesFile().load();

        assert (!getDeliveriesFileReadyState().equals(i));
        assert (!notEquivalent.equals(getDeliveriesFileReadyState()));
        assert (!getDeliveriesFileReadyState().equals(null));
    }
}
