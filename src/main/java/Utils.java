import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clark on 1/31/2017.
 */
public class Utils {
    public static SslContext createSslContext() {
        SslContext sslContext = null;

        try {
            SSLContext defaultContext = SSLContext.getDefault();
            SSLSocketFactory sslSocketFactory = defaultContext.getSocketFactory();
            String[] cipherSuites = sslSocketFactory.getDefaultCipherSuites();
            List<String> ciphers = Arrays.asList(cipherSuites);

            sslContext = SslContextBuilder
                    .forClient()
                    .ciphers(ciphers)
                    .trustManager(createTrustManagerFactory())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }

    public static void close(FileInputStream fileInputStream) {
        if (null != fileInputStream) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    public static KeyStore getKeyStore(String filename, String passwordString) {
        KeyStore keyStore = null;
        FileInputStream fis = null;

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = null;
            if (null != passwordString)
                password = passwordString.toCharArray();

            fis = new FileInputStream(filename);
            keyStore.load(fis, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            close(fis);
        }

        return keyStore;
    }

    public static TrustManager[] getTrustManagers() {
        TrustManager[] trustManagers = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = getKeyStore("data/truststore", "whatever");
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagers;
    }

    public static TrustManager getTrustManager () {
        TrustManager[] trustManagers = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = getKeyStore("truststore", "whatever");
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagers[0];
    }

    public static TrustManagerFactory createTrustManagerFactory () {
        TrustManagerFactory trustManagerFactory = null;

        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = getKeyStore("data/truststore", "whatever");
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagerFactory;
    }

    public static KeyManager[] createKeyManagers () {
        FileInputStream fis = null;
        KeyManager[] keyManagers = null;

        try {
            String passwordString = "whatever";
            char[] password = passwordString.toCharArray();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = getKeyStore("data/serverkeystore", passwordString);
            kmf.init(keyStore, password);
            keyManagers = kmf.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return keyManagers;
    }


}
