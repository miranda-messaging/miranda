/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.test;

import com.google.gson.Gson;
import com.ltsllc.clcl.JavaKeyStore;
import com.ltsllc.clcl.PrivateKey;
import com.ltsllc.clcl.PublicKey;
import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.basicclasses.Matchable;
import com.ltsllc.miranda.clientinterface.objects.ClusterStatusObject;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.*;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.mockito.Mock;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 2/20/2017.
 */
public class TestCase {
    private static Gson ourGson = new Gson();
    private static Logger logger = Logger.getLogger(TestCase.class);

    private BlockingQueue<Message> network = new LinkedBlockingQueue<Message>();
    private BlockingQueue<Message> writerQueue = new LinkedBlockingQueue<Message>();
    private KeyStore keyStore;
    private KeyStore trustStore;

    @Mock
    private HttpServer mockHttpServer;

    @Mock
    private Network mockNetwork;

    @Mock
    private Cluster mockCluster;

    @Mock
    private Writer mockWriter;

    @Mock
    private Miranda mockMiranda;

    @Mock
    private MirandaPanicPolicy mockPanicPolicy;

    @Mock
    private UsersFile mockUsersFile;

    @Mock
    private TopicsFile mockTopicsFile;

    @Mock
    private SubscriptionsFile mockSubscriptionsFile;

    @Mock
    private Logger mockLogger;

    @Mock
    private MirandaTimer mockTimer;

    @Mock
    private MirandaProperties mockProperties;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private UserManager mockUserManager;

    @Mock
    private TopicManager mockTopicManager;

    @Mock
    private SessionManager mockSessionManager;

    @Mock
    private SingleFile mockSingleFile;

    @Mock
    private BlockingQueue mockBlockingQueue;

    @Mock
    private Session mockSession;

    @Mock
    private PublicKey mockPublicKey;

    @Mock
    private MirandaFactory mockMirandaFactory;

    @Mock
    private com.ltsllc.miranda.reader.Reader mockReader;

    @Mock
    private FileWatcher mockFileWatcher;

    @Mock
    private FileWatcherService mockFileWatcherService;

    @Mock
    private ServletHolder mockServletHolder;

    @Mock
    private SubscriptionManager mockSubscriptionManager;

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public KeyStore getKeyStore() {

        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public SubscriptionManager getMockSubscriptionManager() {
        return mockSubscriptionManager;
    }

    public ServletHolder getMockServletHolder() {
        return mockServletHolder;
    }

    public FileWatcherService getMockFileWatcherService() {
        return mockFileWatcherService;
    }

    public FileWatcher getMockFileWatcher() {
        return mockFileWatcher;
    }

    public MirandaFactory getMockMirandaFactory() {
        return mockMirandaFactory;
    }

    public PublicKey getMockPublicKey() {
        return mockPublicKey;
    }

    public BlockingQueue getMockBlockingQueue() {
        return mockBlockingQueue;
    }

    public SingleFile getMockSingleFile() {
        return mockSingleFile;
    }

    public SessionManager getMockSessionManager() {
        return mockSessionManager;
    }

    public TopicManager getMockTopicManager() {
        return mockTopicManager;
    }

    public UserManager getMockUserManager() {
        return mockUserManager;
    }

    public InputStream getMockInputStream() {
        return mockInputStream;
    }

    public MirandaTimer getMockTimer() {
        return mockTimer;
    }

    public SubscriptionsFile getMockSubscriptionsFile() {
        return mockSubscriptionsFile;
    }

    public TopicsFile getMockTopicsFile() {
        return mockTopicsFile;
    }

    public UsersFile getMockUsersFile() {
        return mockUsersFile;
    }

    public Miranda getMockMiranda() {
        return mockMiranda;
    }

    public Writer getMockWriter() {
        return mockWriter;
    }

    public Cluster getMockCluster() {
        return mockCluster;
    }

    public Reader getMockReader() {
        return mockReader;
    }

    public MirandaProperties getMockProperties() {
        return mockProperties;
    }

    public Session getMockSession() {
        return mockSession;
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory())
            return false;

        if (!file.exists())
            return true;

        return file.delete();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean deleteDirectory(String filename) {
        File file = new File(filename);
        return deleteDirectory(file);
    }

    public static boolean deleteDirectory(File directory) {
        if (!directory.isDirectory()) {
            return !directory.exists();
        }

        try {
            String[] contents = directory.list();

            for (String s : contents) {
                String fullname = directory.getCanonicalPath() + File.separator + s;
                File file = new File(fullname);
                if (file.isDirectory()) {
                    if (!deleteDirectory(file))
                        return false;
                } else {
                    if (!deleteFile(file))
                        return false;
                }
            }

            return directory.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long touch(File file, long time) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (!file.setLastModified(time)) {
            Exception e = new Exception("could not set the time of last modification of " + file);
            e.printStackTrace();
            System.exit(1);
        }

        return file.lastModified();
    }

    public static long touch(String filename) {
        long now = System.currentTimeMillis();
        File file = new File(filename);
        return touch(file, now);
    }

    public static long touch(File file) {
        long now = System.currentTimeMillis();
        return touch(file, now);
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public MirandaPanicPolicy getMockPanicPolicy() {
        return mockPanicPolicy;
    }

    public Logger getMockLogger() {
        return mockLogger;
    }

    public HttpServer getMockHttpServer () {
        return mockHttpServer;
    }

    public void reset() {
        network = null;
        writerQueue = null;

        this.mockMiranda = null;
        this.mockCluster = null;
        this.mockNetwork = null;
        this.mockWriter = null;
        this.mockPanicPolicy = null;
        this.mockUsersFile = null;
        this.mockTopicsFile = null;
        this.mockSubscriptionsFile = null;
        this.mockLogger = null;
        this.mockTimer = null;
        this.mockProperties = null;
        this.mockInputStream = null;
        this.mockUserManager = null;
        this.mockTopicManager = null;
        this.mockSessionManager = null;
        this.mockSingleFile = null;
        this.mockBlockingQueue = null;
        this.mockSession = null;
        this.mockPublicKey = null;
        this.mockMirandaFactory = null;
        this.mockFileWatcher = null;
        this.mockFileWatcherService = null;
        this.mockReader = null;
        this.mockServletHolder = null;
        this.mockSubscriptionManager = null;
        this.mockHttpServer = null;
    }

    public void setup() {
        network = new LinkedBlockingQueue<Message>();
        writerQueue = new LinkedBlockingQueue<Message>();

        this.mockMiranda = mock(Miranda.class);
        this.mockWriter = mock(Writer.class);
        this.mockNetwork = mock(Network.class);
        this.mockCluster = mock(Cluster.class);
        this.mockPanicPolicy = mock(MirandaPanicPolicy.class);
        this.mockUsersFile = mock(UsersFile.class);
        this.mockTopicsFile = mock(TopicsFile.class);
        this.mockSubscriptionsFile = mock(SubscriptionsFile.class);
        this.mockLogger = mock(Logger.class);
        this.mockTimer = mock(MirandaTimer.class);
        this.mockProperties = mock(MirandaProperties.class);
        this.mockInputStream = mock(InputStream.class);
        this.mockUserManager = mock(UserManager.class);
        this.mockTopicManager = mock(TopicManager.class);
        this.mockSessionManager = mock(SessionManager.class);
        this.mockSingleFile = mock(SingleFile.class);
        this.mockBlockingQueue = mock(BlockingQueue.class);
        this.mockSession = mock(Session.class);
        this.mockPublicKey = mock(PublicKey.class);
        this.mockMirandaFactory = mock(MirandaFactory.class);
        this.mockFileWatcher = mock(FileWatcher.class);
        this.mockFileWatcherService = mock(FileWatcherService.class);
        this.mockReader = mock(Reader.class);
        this.mockServletHolder = mock(ServletHolder.class);
        this.mockSubscriptionManager = mock(SubscriptionManager.class);
        this.mockHttpServer = mock(HttpServer.class);
    }

    public void setupMockNetwork() {
        if (null == this.mockNetwork)
            this.mockNetwork = mock(Network.class);

        Network.setInstance(this.mockNetwork);
    }

    private static final String LOG4J_CONFIG_FILENAME = "log4j.xml";

    private static final String[] LOG4J_CONFIG_FILE_CONTENTS = {
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
            "<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">",
            "<log4j:configuration xmlns:log4j=\"http://jakarta.apache.org/log4j/\">",
            "",
            "    <appender name=\"consoleAppender\" class=\"org.apache.log4j.ConsoleAppender\">",
            "        <layout class=\"org.apache.log4j.PatternLayout\">",
            "            <param name=\"ConversionPattern\" value=\"%d{dd MMM yyyy HH:mm:ss} %5p %c{1} - %m%n\"/>",
            "        </layout>",
            "    </appender>",
            "",
            "    <appender name=\"fileAppender\" class=\"org.apache.log4j.RollingFileAppender\">",
            "        <param name=\"append\" value=\"false\"/>",
            "        <param name=\"file\" value=\"out/learning.log\"/>",
            "        <layout class=\"org.apache.log4j.PatternLayout\">",
            "            <param name=\"ConversionPattern\" value=\"%d{ABSOLUTE} %-5p [%c{1}] %m%n\"/>",
            "        </layout>",
            "    </appender>",
            "",
            "    <root>",
            "        <level value=\"INFO\"/>",
            "        <appender-ref ref=\"consoleAppender\"/>",
            "        <appender-ref ref=\"fileAppender\"/>",
            "    </root>",
            "",
            "</log4j:configuration>",
    };

    public void setupMockHttpServer () {
        Miranda.getInstance().setHttpServer(getMockHttpServer());
    }

    public static void setuplog4j() {
        putFile(LOG4J_CONFIG_FILENAME, LOG4J_CONFIG_FILE_CONTENTS);
        DOMConfigurator.configure(LOG4J_CONFIG_FILENAME);
        logger = Logger.getLogger(TestCase.class);
    }

    public static void putFile(String filename, String[] contents) {
        PrintWriter out = null;

        try {
            FileWriter fileWriter = new FileWriter(filename);
            out = new PrintWriter(fileWriter);

            for (String line : contents) {
                out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            Utils.closeIgnoreExceptions(out);
        }
    }

    public static void createFile(String filename, byte[] contents) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(contents);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public static void createFile(String filename, String contents) {
        FileOutputStream fileOutputStream = null;

        try {
            byte[] buffer = Utils.hexStringToBytes(contents);

            fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }


    public static void createFile(String filename, String[] contents) {
        PrintWriter printWriter = null;

        try {
            FileWriter fileWriter = new FileWriter(filename);
            printWriter = new PrintWriter(fileWriter);

            for (String line : contents) {
                printWriter.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(printWriter);
        }
    }


    public static boolean createFile(File file, int size, Random random) {
        byte[] buffer = new byte[size];
        random.nextBytes(buffer);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }

        return true;
    }


    public static final String TEMP_TRUST_STORE_CONTENTS = "FEEDFEED000000020000000100000002000263610000015C613262430005582E353039000003643082036030820248A003020102020900951A1546B7E95ABF300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531383136333835385A170D3138303531383136333835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100D1B02C7DFFF1CE31C7E97C7E6629ED959255738B9B2A77DE5BA006BC586DFC6CA0F203E27A8BF9486412B45D5162703C9118E9A904F7FBFB1D5FAEE59CEADCD8E574B5E84B59CDCC12A57DAFF38066EB67DCA5503EB46841157A7C6FD43E1D535ACEEA76188463D8FBE086F6C92C83DB3B822C3A1FA88356A64C98DEF8532EA00F7ED6FD0DB14EC02F50C4D3151FE63E7D3D0D260E5318A5D3F2696906240CA3BC5A1EB10B3F66D7017080950FFFC99BDF38C564D877E51F7B01AC69BC48459B307D1DE1F951F4DE39D3C39117E448A9C9F9D7E940428729603F7C9664B169B246D7C3B9681F807EB2C0ED85AF5ADFD59E097D7B4A371F777465B7283C7C29D50203010001A3533051301D0603551D0E041604141F3008D3090510954EDFAC4949AA240CA1A801C7301F0603551D230418301680141F3008D3090510954EDFAC4949AA240CA1A801C7300F0603551D130101FF040530030101FF300D06092A864886F70D01010B0500038201010040B331E7A9214ECB5C79F70C746C75DCF8BEDF0F36B963396E9145FAFCA9308CAE82FF15F6072E9AB57C5A5C9058E9B1C7CD5FB014F9DB4C67470DF32D50B1B9B2AF5C8DCD5E84DC3BE7D328019447E538B24AC58589D2717A2FB9CB2768E06266381CC072C544D260BE89DB803A4D3DE31CCA8C34E3A944C385FCC9B6EE06B1D6904F1346BC131F1B31A192C1A12BE3409739251851F8770792DC7421F81DEEF24E2E7C8FD63075E769C8B8352B0F9AB04920EDEA4BBD6B7CEB61F3DE3E17C93422809D7143D88F6F56ADAB440C3F3B6473015DCB922B617D823C301F036F4478AAC0FB690A615CEA4D77F0863AE880C48DBB0319067DCECD43E12BC6C92FA4425C54906FDF0DA9E5A1775771FAC160E72818F8";
    public static final String TEMP_TRUSTSTORE = "tempTrustStore";
    public static final String TEMP_TRUSTSTORE_PASSWORD = "whatever";

    public void setupTrustStore() {
        try {
            deleteFile(TEMP_TRUSTSTORE);
            createFile(TEMP_TRUSTSTORE, TEMP_TRUST_STORE_CONTENTS);
            this.trustStore = Utils.loadKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD);
            Miranda.properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME, TEMP_TRUSTSTORE);
        } catch (GeneralSecurityException | IOException e) {
            StartupPanic startupPanic = new StartupPanic("Eception loading truststore from " + TEMP_TRUSTSTORE,
                    e, StartupPanic.StartupReasons.ExceptionLoadingKeystore);
            Miranda.panicMiranda(startupPanic);
        }
    }

    public static void cleanupTrustStore() {
        deleteFile(TEMP_TRUSTSTORE);
    }


    public static final String TEMP_KEY_STORE_CONTENTS = "FEEDFEED000000020000000200000002000263610000015C613401C30005582E353039000003643082036030820248A003020102020900951A1546B7E95ABF300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531383136333835385A170D3138303531383136333835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100D1B02C7DFFF1CE31C7E97C7E6629ED959255738B9B2A77DE5BA006BC586DFC6CA0F203E27A8BF9486412B45D5162703C9118E9A904F7FBFB1D5FAEE59CEADCD8E574B5E84B59CDCC12A57DAFF38066EB67DCA5503EB46841157A7C6FD43E1D535ACEEA76188463D8FBE086F6C92C83DB3B822C3A1FA88356A64C98DEF8532EA00F7ED6FD0DB14EC02F50C4D3151FE63E7D3D0D260E5318A5D3F2696906240CA3BC5A1EB10B3F66D7017080950FFFC99BDF38C564D877E51F7B01AC69BC48459B307D1DE1F951F4DE39D3C39117E448A9C9F9D7E940428729603F7C9664B169B246D7C3B9681F807EB2C0ED85AF5ADFD59E097D7B4A371F777465B7283C7C29D50203010001A3533051301D0603551D0E041604141F3008D3090510954EDFAC4949AA240CA1A801C7301F0603551D230418301680141F3008D3090510954EDFAC4949AA240CA1A801C7300F0603551D130101FF040530030101FF300D06092A864886F70D01010B0500038201010040B331E7A9214ECB5C79F70C746C75DCF8BEDF0F36B963396E9145FAFCA9308CAE82FF15F6072E9AB57C5A5C9058E9B1C7CD5FB014F9DB4C67470DF32D50B1B9B2AF5C8DCD5E84DC3BE7D328019447E538B24AC58589D2717A2FB9CB2768E06266381CC072C544D260BE89DB803A4D3DE31CCA8C34E3A944C385FCC9B6EE06B1D6904F1346BC131F1B31A192C1A12BE3409739251851F8770792DC7421F81DEEF24E2E7C8FD63075E769C8B8352B0F9AB04920EDEA4BBD6B7CEB61F3DE3E17C93422809D7143D88F6F56ADAB440C3F3B6473015DCB922B617D823C301F036F4478AAC0FB690A615CEA4D77F0863AE880C48DBB0319067DCECD43E12BC6C92FA4000000010007707269766174650000015C61343B0D0000050430820500300E060A2B060104012A021101010500048204EC8845F9D863C9C0A6ADFA8E9A734BF70A15D74A4D1F80B2C3D327DD1B3E1BA41BBE91D9BDAD1A81EABE1E121F024F377691096BBCEDD5BB5ED20EFE0FEF3E8098C54C08E64051BA94BB8D0855E2B89A92B0D165E51245C201CCB3AF2192B6BBFFDBE3913360DB8A48DC90677D445E93A5DCFB583E69EE9DBB07554E65B75267C807B1DE67D9F820A333F2FF599C081E0D25A8DBEEB856CA9505F9B3C0F502573042AD0544AA07E4ECE85A01CDA9CF93C62AB9DB02EDC6C9D92D48DB0C3BACFD47DACCEBED3B428B6C01BCD4682DA1DEB469E98F6FC1042F8590DE060BDE73EFDC8360564B7A58346858627881149D843FCD31DF53401CE3C6F86BF9E8F58F2001D0C71E79C34E26703E437C4CDD06F473EF9813F7CB6099E320670C3E43910760AAD1042C0B40EAC8F29D1B9BBD599A1F4F9013D023217F2D4D535F2B22E2F021512DE49FA912595087018BF3D4B7F436131AB13C82900BEC4B980FE620FB81722402BF62D7E7F705C79B713E0DB2224A21EFF5078CE13247EC74160366F54D113453FAAF8EFC4225E94149171AF123285C8673F3189F9B712E08FE2316B4860E46BB1ECC13FEC07C74F1CA7B9653C5A4B38F1CC654A9767CA68E765DE8DEBD0EEB2159017D8F9D27FD0BC085BFCC1238A4D26946B560DA75A2F086C60A221C0B151DF64DC1732F1205DA98ED24CFF11B7089C199865F6709B86FE8AF8E00B97BDE15462F475A773D290C10DBC2852BB5C0772E29DF6DCB48E87149518F3A940DA4BEBA98BF34DB35CAF666ED8CD7D65CE0AADB46F39BB230EDB8C3576C13FD3895C62E304D4C233A5217FC2AF98263C09B1754B71CFF9991AF5014381CB71E2F1B2CAB631745CD555F2D7C5526AF5CC35238343627977EFAC7F85047B70184ECD250BB111053C3246920F3C0B73F132452745B01494900EAD8E0E3BD4DF04C4443F6725BCD55E498EA7B5531E6E763027BBC5A2113994729DD7C8BAD1C75D77ACCFD44D113957123724BB69C729550BA13B374C3E53F45AF3AA76E836558A8A903CDDBF7AC469ABB5A3FCF3DC60BFDF92AEA2F1DCF6149A4C479EBF6DB0854A0BB3ABC57A0433B70CFE1F35531B6D9FF1EB22330CDD5FAC7577B233261DCDF7C3421174B7B53718125430CF089276F64236F149BA0213906C28F1E19D3205C976A9C8857F317A96B8CA328E823D42D6C322334B83216DCC0433A560C148E936F5F088120DB11E7E34F6A829F003E0D6FE6747EEB7D648EE8987A25183FB1019343E827D5DA87923529FE6E29A62A6E81704C1869C9BA272659F8EFE7A2589CE07E393CB6E1B27E9ABB451F09CA34EDA1EA0F09E1CB7A5AD009688291C240845174D8EB18FE3845A7D398DD1EA3518C34A5C01ED708DA440538369BE23A280816FA902839E349EA27449FCEA0C9EEE3A937AE2FFBDC67E71FF0329C0D15F004A2536BF2EA31E52B0000DCCB16DE38AE02C09035A041E42A1236450B54676BC6CC294E8FE6F9FDA44AAC077A19B09870AB02F4C397D99553C892E65F9AC03FA5DD044E7C022F013A2DAA5E6C0BF6F4D739AB45D0DED201A4823B17862DF361C422776A081F2D05E55377010822CB78E28095C2B782FF5BE4FBD42B9AF4877B7FC01B66834D50888BD069ED8BA03DCD67CB598E6A23CA315099B90E1F19D47991CFDED5ED7B1AB5C3CA4A5EF54D0BD76E685CB38208E3A6612F147DCF4DDACC1197DD763FDE134AEF92F30AD57D25058C273B8558A1F9716F97E7414272AD38E1C22CF00FB1E88EE69FF9EAD72F000000020005582E353039000003313082032D30820215020900EAF78ECEAFD426CD300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303630313031303935355A170D3138303630313031303935355A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100C8B8422EDA69ED33BA67E98B1C19F9DCE9EB587F2DFCEA4B08DD5B2EEBBD07A549D34D39BB6C348F9131DC5B012F8CEA46C55C14EA82BBF7BC1899CB583860D74D40B097F4CB9D237652C18A17F2B2979E45F045884A2E74202147E9ECF18F74B4FF0056191E7B4682E96B0019F18643D311EBCB90EBEAAD9A68362CF5345434C0C7750E1D3B83E56E6E58E88BD79802A5E23CE9247207D98A6AF7C748C8122565B5E04E53FB7B3B5FB14E8ABB85D522072F08B3CA0C15378045C9C184C7F02CDAA4D8294EF6BC3FBEBFFB291F8E3DB951D5F237B4A873B7B7AD3228E56D4F09D9557C79FC94BEB94EB7231D0BD027A3107AEA95C6FB9BDE2E256DB2B33B73FD0203010001300D06092A864886F70D01010B0500038201010028A2EB10E163B4CF5C4525E09EF7BFFEF73B1DFD2451FC07E58687645F0FD5E05B8EC1F4754FC8682958828F307859E84AFFDF296805518AE90B3A7A7A22AF8C641EF5963D2EF56BAEB74930F3AD4D2867FB2E1FB90ACB929C73BC76236A2485A91F5507F9C27AAEE26F481A1BBF44DEDDDCBF37DE411E0F80913CA4B5A0BD0ACA3105C282EB3F4D0B9CA3B3977299D970576028E7D58DAF532EADB1EE42B763058B5412DBBF60EFBF65159462E13D438167A3C076C842EC907787F7B4CD2303AF0AC3A92F60B0475443F17CA98DB55EB65211C0FA37A5CB341B83C1729E958E46F4001C82D7B2DDF9535688F96CEDA8D8201CB02E560B16958622CBBC4C54420005582E353039000003643082036030820248A003020102020900951A1546B7E95ABF300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531383136333835385A170D3138303531383136333835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100D1B02C7DFFF1CE31C7E97C7E6629ED959255738B9B2A77DE5BA006BC586DFC6CA0F203E27A8BF9486412B45D5162703C9118E9A904F7FBFB1D5FAEE59CEADCD8E574B5E84B59CDCC12A57DAFF38066EB67DCA5503EB46841157A7C6FD43E1D535ACEEA76188463D8FBE086F6C92C83DB3B822C3A1FA88356A64C98DEF8532EA00F7ED6FD0DB14EC02F50C4D3151FE63E7D3D0D260E5318A5D3F2696906240CA3BC5A1EB10B3F66D7017080950FFFC99BDF38C564D877E51F7B01AC69BC48459B307D1DE1F951F4DE39D3C39117E448A9C9F9D7E940428729603F7C9664B169B246D7C3B9681F807EB2C0ED85AF5ADFD59E097D7B4A371F777465B7283C7C29D50203010001A3533051301D0603551D0E041604141F3008D3090510954EDFAC4949AA240CA1A801C7301F0603551D230418301680141F3008D3090510954EDFAC4949AA240CA1A801C7300F0603551D130101FF040530030101FF300D06092A864886F70D01010B0500038201010040B331E7A9214ECB5C79F70C746C75DCF8BEDF0F36B963396E9145FAFCA9308CAE82FF15F6072E9AB57C5A5C9058E9B1C7CD5FB014F9DB4C67470DF32D50B1B9B2AF5C8DCD5E84DC3BE7D328019447E538B24AC58589D2717A2FB9CB2768E06266381CC072C544D260BE89DB803A4D3DE31CCA8C34E3A944C385FCC9B6EE06B1D6904F1346BC131F1B31A192C1A12BE3409739251851F8770792DC7421F81DEEF24E2E7C8FD63075E769C8B8352B0F9AB04920EDEA4BBD6B7CEB61F3DE3E17C93422809D7143D88F6F56ADAB440C3F3B6473015DCB922B617D823C301F036F4478AAC0FB690A615CEA4D77F0863AE880C48DBB0319067DCECD43E12BC6C92FA4415AE9B442E73B833EC43B86FC4322BFB468B749";

    public static final String TEMP_KEYSTORE = "tempKeyStore";
    public static final String TEMP_KEYSTORE_PASSWORD = "whatever";

    public void setupKeyStore()
    {
        try {
            deleteFile(TEMP_KEYSTORE);
            createFile(TEMP_KEYSTORE, TEMP_KEY_STORE_CONTENTS);
            this.keyStore = Utils.loadKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD);
            Miranda.properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE,TEMP_KEYSTORE);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanupKeyStore() {
        deleteFile(TEMP_KEYSTORE);
    }


    public static String readContents(String filename) {
        FileInputStream fileInputStream = null;
        String contents = null;

        try {
            File file = new File(filename);
            int size = (int) (file.length());
            fileInputStream = new FileInputStream(filename);
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);

            contents = Utils.bytesToString(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return contents;
    }

    public static void deleteFile(String filename) {
        File f = new File(filename);

        if (f.exists()) {
            f.delete();

            if (f.exists()) {
                logger.error("Failed to delete " + f.getName());
                Exception e = new Exception();
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    public static void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message, BlockingQueue queue) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean contains(Message.Subjects subject, BlockingQueue<Message> queue) {
        for (Message m : queue) {
            if (subject.equals(m.getSubject())) {
                return true;
            }
        }

        return false;
    }

    public boolean containsNetworkMessage(WireMessage wireMessage, BlockingQueue<Message> queue) {
        for (Message m : queue) {
            if (m.getSubject() == Message.Subjects.NetworkMessage) {
                NetworkMessage networkMessage = (NetworkMessage) m;
                if (networkMessage.getWireMessage().equals(wireMessage))
                    return true;
            }
        }

        return false;
    }

    private static final String PROPERTIES_FILENAME = "ssltest.properties";

    private static final String[] PROPERTIES_FILE_CONTENTS = {
            "com.ltsllc.miranda.TruststorePassword=whatever",
            "com.ltsllc.miranda.KeyStorePassword=whatever"
    };

    public void setupMirandaProperties() {
        //
        // force it to use the defaults
        //
        Miranda.properties = new MirandaProperties();
    }

    public void setupMiranda() {
        new Miranda("-p whatever");
    }

    public static void setupTimer() {
        Miranda.timer = new MirandaTimer();
    }


    public static String toJson(Object o) {
        return ourGson.toJson(o);
    }


    public static Version createVersion(Object o) throws NoSuchAlgorithmException {
        String json = toJson(o);
        return new Version(json);
    }

    public boolean contains(Object o, List list) {
        for (Object candidate : list) {
            if (candidate.equals(o))
                return true;
        }

        return false;
    }

    public static final String DIRECTORY = "directory";
    public static final String EVENT_FILE = "event file";
    public static final String FILE = "file";
    public static final String RANDOM_FILE = "random file";
    public static final String NODE_ELEMENT_FILE = "nodeElement file";

    public boolean createFileSystem(String rootFilename, String[][] spec) {
        ImprovedRandom random = new ImprovedRandom();
        File root = new File(rootFilename);
        FileCreator randomFileCreator = new RandomFileCreator(1024, random);
        MirandaProperties properties = Miranda.properties;
        int maxNumberOfEvents = 1 + properties.getIntegerProperty(MirandaProperties.PROPERTY_MESSAGE_FILE_SIZE);
        FileCreator eventFileCreator = new EventFileCreator(random, maxNumberOfEvents, 1024);
        FileCreator nodeElementCreator = new NodeElementFileCreator(random, 16);

        if (!root.isDirectory()) {
            if (root.exists())
                return false;

            if (!root.mkdirs())
                return false;

            for (String[] record : spec) {
                String fullName = rootFilename + File.separator + record[0];
                File file = new File(fullName);


                if (DIRECTORY.equalsIgnoreCase(record[1])) {
                    if (!file.isDirectory() && !file.mkdirs())
                        return false;
                } else if (FILE.equalsIgnoreCase(record[1]) || RANDOM_FILE.equalsIgnoreCase(record[1])) {
                    randomFileCreator.createFile(file);
                } else if (EVENT_FILE.equalsIgnoreCase(record[1])) {
                    eventFileCreator.createFile(file);
                } else if (NODE_ELEMENT_FILE.equalsIgnoreCase(record[1])) {
                    nodeElementCreator.createFile(file);
                } else {
                    System.err.println("Unrecognized entry: " + record[1]);
                }
            }
        }

        return true;
    }


    public boolean collectedAfter(long time, MirandaFile file) {
        return file.getLastCollection() > time;
    }


    public boolean collectedAfter(long time, List<MirandaFile> files) {
        for (MirandaFile file : files) {
            if (!collectedAfter(time, file))
                return false;
        }

        return true;
    }

    public static void setupFileWatcher(int period) {
        Miranda.fileWatcher = new FileWatcherService(period);
        Miranda.fileWatcher.start();
    }

    public Network getMockNetwork() {
        return mockNetwork;
    }

    public boolean queueIsEmpty(BlockingQueue<Message> queue) {
        return 0 == queue.size();
    }

    public void setupWriter() {
        com.ltsllc.miranda.writer.Writer writer = new com.ltsllc.miranda.writer.Writer(getMockPublicKey());

        this.writerQueue = new LinkedBlockingQueue<Message>();
        writer.setQueue(this.writerQueue);
    }

    public static void makeFile(String filename, byte[] data) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public void setupMockCluster() {
        if (null == getMockCluster())
            this.mockCluster = mock(Cluster.class);

        Miranda.getInstance().setCluster(mockCluster);
    }

    public void setupMirandaFactory(String keystorePassword, String truststorePassword) {
        setupMirandaProperties();

        Miranda.factory = new MirandaFactory(Miranda.properties, keystorePassword, truststorePassword);
    }

    public void setupMockMiranda() {
        Miranda.setInstance(mockMiranda);
    }

    public void setupMockPanicPolicy() {
        Miranda.getInstance().setPanicPolicy(mockPanicPolicy);
    }

    public void setupMockUsersFile() {
        UsersFile.setInstance(getMockUsersFile());
    }

    public void setupMockTopicsFile() {
        TopicsFile.setInstance(getMockTopicsFile());
    }

    public void setupMockSubscriptionsFile() {
        SubscriptionsFile.setInstance(getMockSubscriptionsFile());
    }

    public void setupMockTimer() {
        Miranda.timer = getMockTimer();
    }

    public void setupMockProperties() {
        Miranda.properties = getMockProperties();
    }

    public void setupMockWriter() {
        Miranda.getInstance().setWriter(getMockWriter());
    }

    public String loadHexString (String filename) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            int b = fileInputStream.read();
            while (-1 != b) {
                byteArrayOutputStream.write(b);
                b = fileInputStream.read();
            }
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return Utils.bytesToString(byteArrayOutputStream.toByteArray());
    }

    public String loadPrivateKey(String filename, String password, String alias) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            KeyStore keyStore = Utils.loadKeyStore(filename, password);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(privateKey);
            objectOutputStream.close();
            byteArrayOutputStream.close();
            byte[] data = byteArrayOutputStream.toByteArray();
            return Utils.bytesToString(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(byteArrayOutputStream);
        }

        return null;
    }

    public void setupMockSessionManager() {
        Miranda.getInstance().setSessionManager(getMockSessionManager());
    }

    public void setupMockUserManager() {
        Miranda.getInstance().setUserManager(getMockUserManager());
    }

    public boolean isSubscriber(Consumer consumer, List<Subscriber> subscribers) {
        BlockingQueue<Message> queue = consumer.getQueue();

        for (Subscriber subscriber : subscribers) {
            if (subscriber.getQueue() == queue)
                return true;
        }

        return false;
    }

    public boolean containsStatus(NodeStatus nodeStatus, ClusterStatusObject clusterStatusObject) {
        for (NodeStatus nodeStatus2 : clusterStatusObject.getNodes()) {
            if (nodeStatus.equals(nodeStatus2))
                return true;
        }

        return false;
    }

    public boolean listContains(Matchable matchable, List<Matchable> list) {
        for (Matchable element : list) {
            if (element.matches(matchable))
                return true;
        }

        return false;
    }

    public boolean listsAreEquivalent(List l1, List l2) {
        for (Object o : l1) {
            Matchable matchable = (Matchable) o;
            if (!listContains(matchable, l2))
                return false;
        }

        return true;
    }

    public String fileContentsAsHexString(String filename) {
        File file = new File(filename);
        int intLength = (int) file.length();
        byte[] buffer = new byte[intLength];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(buffer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return Utils.bytesToString(buffer);
    }

    public void setupInputStream(String input) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        Miranda.setInputStream(byteArrayInputStream);
    }

    public void createDirectory(String directory) {
        File file = new File(directory);
        file.mkdir();
    }

    public void setupMockFileWatcherService() {
        Miranda.fileWatcher = getMockFileWatcherService();
    }

    public void setupMockReader() {
        Miranda.getInstance().setReader(getMockReader());
    }

    public boolean arraysAreEquivalent(byte[] a1, byte[] a2) {
        if (a1 == a2)
            return true;

        if (null == a1)
            return false;
        else if (null == a2)
            return false;
        else if (a1.length != a2.length)
            return false;

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i])
                return false;
        }

        return true;
    }

    public void setupMockFileWatcher() {
        Miranda.fileWatcher = getMockFileWatcherService();
    }

    public void setupMockFactory() {
        Miranda.factory = getMockMirandaFactory();
    }
}
