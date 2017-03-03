package com.ltsllc.miranda;

import com.ltsllc.miranda.netty.NettyNetwork;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.SocketNetwork;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.util.Utils;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;

/**
 * Based on the values of the properties, this class knows which classes to
 * build.
 */
public class MirandaFactory {
    private MirandaProperties properties;

    public MirandaProperties getProperties() {
        return properties;
    }

    public MirandaFactory (MirandaProperties properties) {
        this.properties = properties;
    }

    public Network buildNetwork () throws MirandaException {
        Network network = null;
        MirandaProperties.Networks networks = getProperties().getNetworkProperty();

        switch (networks) {
            case Netty: {
                network = new NettyNetwork(this);
                break;
            }

            case Socket: {
                network = new SocketNetwork();
                break;
            }

            default: {
                throw new IllegalArgumentException("unknown network: " + networks);
            }
        }

        return network;
    }

    public SslContext buildNettyClientSslContext () throws SSLException {
        String filename = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String password = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);

        return Utils.createClientSslContext(filename, password);
    }
}
