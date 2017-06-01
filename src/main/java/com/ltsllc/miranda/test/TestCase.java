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
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.*;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusObject;
import com.ltsllc.miranda.servlet.status.NodeStatus;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.JavaKeyStore;
import com.ltsllc.miranda.util.Utils;
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
    private static Logger logger;

    private BlockingQueue<Message> network = new LinkedBlockingQueue<Message>();
    private BlockingQueue<Message> writerQueue = new LinkedBlockingQueue<Message>();
    private KeyStore keyStore;
    private KeyStore trustStore;

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


    public static final String TEMP_TRUST_STORE_CONTENTS = "FEEDFEED000000020000000100000002000263610000015C602CA5980005582E353039000003643082036030820248A003020102020900951A1546B7E95ABF300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531383136333835385A170D3138303531383136333835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100D1B02C7DFFF1CE31C7E97C7E6629ED959255738B9B2A77DE5BA006BC586DFC6CA0F203E27A8BF9486412B45D5162703C9118E9A904F7FBFB1D5FAEE59CEADCD8E574B5E84B59CDCC12A57DAFF38066EB67DCA5503EB46841157A7C6FD43E1D535ACEEA76188463D8FBE086F6C92C83DB3B822C3A1FA88356A64C98DEF8532EA00F7ED6FD0DB14EC02F50C4D3151FE63E7D3D0D260E5318A5D3F2696906240CA3BC5A1EB10B3F66D7017080950FFFC99BDF38C564D877E51F7B01AC69BC48459B307D1DE1F951F4DE39D3C39117E448A9C9F9D7E940428729603F7C9664B169B246D7C3B9681F807EB2C0ED85AF5ADFD59E097D7B4A371F777465B7283C7C29D50203010001A3533051301D0603551D0E041604141F3008D3090510954EDFAC4949AA240CA1A801C7301F0603551D230418301680141F3008D3090510954EDFAC4949AA240CA1A801C7300F0603551D130101FF040530030101FF300D06092A864886F70D01010B0500038201010040B331E7A9214ECB5C79F70C746C75DCF8BEDF0F36B963396E9145FAFCA9308CAE82FF15F6072E9AB57C5A5C9058E9B1C7CD5FB014F9DB4C67470DF32D50B1B9B2AF5C8DCD5E84DC3BE7D328019447E538B24AC58589D2717A2FB9CB2768E06266381CC072C544D260BE89DB803A4D3DE31CCA8C34E3A944C385FCC9B6EE06B1D6904F1346BC131F1B31A192C1A12BE3409739251851F8770792DC7421F81DEEF24E2E7C8FD63075E769C8B8352B0F9AB04920EDEA4BBD6B7CEB61F3DE3E17C93422809D7143D88F6F56ADAB440C3F3B6473015DCB922B617D823C301F036F4478AAC0FB690A615CEA4D77F0863AE880C48DBB0319067DCECD43E12BC6C92FA4ACCA613EE11F9A6CE2C6AD92EBC85A3421391C08";
    public static final String TEMP_TRUSTSTORE = "tempTrustStore";
    public static final String TEMP_TRUSTSTORE_PASSWORD = "whatever";

    public void setupTrustStore() throws Exception {
        // createFile(TEMP_TRUSTSTORE, TEMP_TRUST_STORE_CONTENTS);
        // this.trustStore = Utils.loadKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD);
        trustStore = Utils.loadKeyStore("truststore", "whatever");
    }

    public static void cleanupTrustStore() {
        deleteFile(TEMP_TRUSTSTORE);
    }


    public static final String TEMP_KEY_STORE_CONTENTS = "FEEDFEED000000020000000300000002000B63657274696669636174650000015C602E53D40005582E353039000003313082032D30820215020900EAF78ECEAFD426CA300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303533313230323335305A170D3138303533313230323335305A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100C5C8FE8D48388BFDED2FF7803AA2EABFF7B555168DBB549996334321E59F8B63A645B386FB2E75D6B9490E781809A8D5727A3FD56957195B2E87C6D0998F8108BC1D13E8502208CE0C5FE0C174F9AB2501E57BAF67CECA1CE79D99B7DE4028A59D9469C600D9D1817FC8EA7DEAA2616F16889178AD547F24C0C50BD5A2A51DBF432F70C5C65C50B778C846AF5A2A3A50565F9B453460FB41D5554B3FD5C3D00666745C9E96E298347028E554009B9AB41FD5905A98C1682078377D1A834B9CB755FE333B81C8ADDFF9C6570198A0DE217F190F692040F7A5777EEDB81FBE9873DC87F22D499AB8344943574C64E45D8BA2D73B3C6B34A9E5C05E4BB4F033BC430203010001300D06092A864886F70D01010B050003820101005A332D1F0353FA98E2645F7E3F8BED8B22ED7791BE913843817A6E0A34A8A52958EBADE84BA9CB9878E487D230F521A5C605F6F0AE2F98FE113EE43F23CA3809DA3ABADE679D677C24A82AFE057E4B05E965D4ADFD5E02A397021910C432E5BC2A6CE8066790CA655E3419A4D193EED0C32E4CCDCA01901FB1BB59C5FA99F7FC5608FFC71CC5A6D82253A9F237793B4559E9302AD9C2FFAF5DD7E8322A064741B13DA24526A0532B90ECD7A3640C953F4E106E8F052AEB22DC0BE105CB080888E4FCA096DC4B825B828FDF4B5B7AC14F2B1DC1F76BCAC8BA638F71C1B94D99CC8BADD08CF1B69A06FA66A1502EF6BD8C21130F08DF80248A7560386AF55887E100000002000263610000015C602E19C70005582E353039000003643082036030820248A003020102020900951A1546B7E95ABF300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531383136333835385A170D3138303531383136333835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100D1B02C7DFFF1CE31C7E97C7E6629ED959255738B9B2A77DE5BA006BC586DFC6CA0F203E27A8BF9486412B45D5162703C9118E9A904F7FBFB1D5FAEE59CEADCD8E574B5E84B59CDCC12A57DAFF38066EB67DCA5503EB46841157A7C6FD43E1D535ACEEA76188463D8FBE086F6C92C83DB3B822C3A1FA88356A64C98DEF8532EA00F7ED6FD0DB14EC02F50C4D3151FE63E7D3D0D260E5318A5D3F2696906240CA3BC5A1EB10B3F66D7017080950FFFC99BDF38C564D877E51F7B01AC69BC48459B307D1DE1F951F4DE39D3C39117E448A9C9F9D7E940428729603F7C9664B169B246D7C3B9681F807EB2C0ED85AF5ADFD59E097D7B4A371F777465B7283C7C29D50203010001A3533051301D0603551D0E041604141F3008D3090510954EDFAC4949AA240CA1A801C7301F0603551D230418301680141F3008D3090510954EDFAC4949AA240CA1A801C7300F0603551D130101FF040530030101FF300D06092A864886F70D01010B0500038201010040B331E7A9214ECB5C79F70C746C75DCF8BEDF0F36B963396E9145FAFCA9308CAE82FF15F6072E9AB57C5A5C9058E9B1C7CD5FB014F9DB4C67470DF32D50B1B9B2AF5C8DCD5E84DC3BE7D328019447E538B24AC58589D2717A2FB9CB2768E06266381CC072C544D260BE89DB803A4D3DE31CCA8C34E3A944C385FCC9B6EE06B1D6904F1346BC131F1B31A192C1A12BE3409739251851F8770792DC7421F81DEEF24E2E7C8FD63075E769C8B8352B0F9AB04920EDEA4BBD6B7CEB61F3DE3E17C93422809D7143D88F6F56ADAB440C3F3B6473015DCB922B617D823C301F036F4478AAC0FB690A615CEA4D77F0863AE880C48DBB0319067DCECD43E12BC6C92FA4000000010007707269766174650000015C602D092E00000502308204FE300E060A2B060104012A021101010500048204EA407A7A3D73379EA1EA088684CF7A53CB8B4FA971246A489EE888E4C955D4B31CBB283DCDDBB36E4BFE94A29CE0E240307283218F7F1D92D848264EB18E28B4422FEAE944E3791017F26A02FD27F741454D7E00080562D65C392B21C9D74B1D894163C8EFC595977F087C6DD4A2C6861B6AFF7078931CA9C9089BDE154D7DFC016889C3AFD7D1E302E9B964BE80AE8A700B94C2132B8F1628FC8349E73724EF51C7E692D7C634F16EAF11DA215F9D26819CC3D3F5C90F7DB56FACF23BE10AECDB6A70DD2276963570B161FEFA6F93C750FCA2561FC43BF089631CE6A1CE8A0031294DF3FD672433DA3D0C04A80E9E920B48753B6EF50D4EF1D11BD071373F21EF74EBD230EECF23521C5BBDCDA2C9E5739847BA12CA22E134D127C80280BCC4C25C6A0E76E34B252A611AE1E107BFA2EF7A9DE89D9FD1E1E1B23B61ABF78151776DB0357B6F4348AD351D0B8F7FD7FD43D41224CEF5869C6F653C0965FC6CDDF0233670328C4E95D8B10A8CFF835A340F32BD486DBB54060B8287AEAE8E87720EC821F914BDCFFF18B0C46A9F5DD6CD18D7FFB1CF7FD3E0E9D6C522F1F590560AEB063255878CD7F72E725F328D6551A6FCB0C133DB0E3F2EB42C31BFA11C7118C65AFA31D6DF9EBA35CA5698BD94ABCFEC420264A1864011E699CBE2D916D3760C285A9153C5BB19D49D6DC95F33AFA026B98407ECA3B442A15A13839D6A219ABEB448D8DFE530C469F2B5706D480E488D2C5B9A9BB78DF06C34394D734970DD67D73D7E9815DAA089DB5910B66E50083A236099BFF979205762779D57F43D3521F292FA9C325034B485162AB5E6A0A0C2F480F7E7B2E2776F8A06B661C11E3F82ACCB7364916D7D08E8C7C321C015120D31B39668C90CF2588EB006087E4A72659CD5438991C1FF0B4B80EB1CF223AC1153B7A4D957DF0423A8D11577B41A65C787D18366C7590F0AE6A50D4F1794A91A21389B262C353837CA91B20A2C8B9262215AA42FFCDC67F84DD96C10EB9533011A2DBD55165A74347D616B31339EDFDABE2C2F021B859AEAE4CB21F6C2D2DADC4B10F9425768FFF3DB081FB21BEB18A4D4DCBEC5D2F9D075C7458DAF826A2F5E8EB4EA11BCA363A90A7AA62D70ABFB866BF8B6B0228A8707059105091F51755772A3CC587C2BE8510D4E932A10484FCD5FBA7B67B4C4B91B991A726C44EDBD87D946F2068CDA0033B50403A69F9B7C245A9B7B8B57191F9E1D92DC2BF4263EA61DEA4CE7B37E7BF6C43EB2F5276D8FC6A67F4BA0B5B706362DF22F3913F4DBE15E2C456E8F6638E8A84E94CBD24CEF0DF4B4224351972EBE81FD1B7C6A4E1668F03184C8E37BA5EE33FBBA252107D42427EE64990EFCCF14F60F8449354D7E074BEC2734C620559077D0BA1AC3BCDF840CDAD6DDA2A42194064C18CBDA5E5947DED53B6625F086930F10BAD60595E79CF5F2CC41386B9B0421E9BCC68EC1BC9A20AAD394249A38467DB2A7D9593CCA7326599E773FAC807F20CBF390A2EA41097BD95200D33D3D5FCDDFF37AE6D973354825C459986646C5E38E8A623FD13F270C6DCA15CAA7561DAC75FF246D8E98F9D63B59CF7184B976EAF6D4296127AE391471B46AB22626621B3F1E76A57606225FC82AC8383194D8AD9FB375E84BB190AB4ECDB45A8F7F23385465A38E893183802AC1A6F5A071402474190C5344318CBC62999844CC8DB375C170B5AAADA25F744D790B091CEB32D4AD00BC3C47B709A4E9D4CC1470A67517A8E1E64E1090D18EE417B8897A58E4D3000000010005582E3530390000037B308203773082025FA003020102020445E2B5C9300D06092A864886F70D01010B0500306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E301E170D3137303533313230323330315A170D3137303832393230323330315A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100C5C8FE8D48388BFDED2FF7803AA2EABFF7B555168DBB549996334321E59F8B63A645B386FB2E75D6B9490E781809A8D5727A3FD56957195B2E87C6D0998F8108BC1D13E8502208CE0C5FE0C174F9AB2501E57BAF67CECA1CE79D99B7DE4028A59D9469C600D9D1817FC8EA7DEAA2616F16889178AD547F24C0C50BD5A2A51DBF432F70C5C65C50B778C846AF5A2A3A50565F9B453460FB41D5554B3FD5C3D00666745C9E96E298347028E554009B9AB41FD5905A98C1682078377D1A834B9CB755FE333B81C8ADDFF9C6570198A0DE217F190F692040F7A5777EEDB81FBE9873DC87F22D499AB8344943574C64E45D8BA2D73B3C6B34A9E5C05E4BB4F033BC430203010001A321301F301D0603551D0E041604147E7B52E326B19E50AAC29E90FD12E4D88DD701CF300D06092A864886F70D01010B05000382010100BA227472DC5AA5EB5A190D7009CB550E908C6C5B847EAC3DEA5FB4E8601AEE44A45FD1A0B2233034ECD0C0CF83FA8B6D03C8109440FDCFEAA155B442F2409556739931BAB31ADE95DFB6D7D5C99C6795E165BFD905FE3DEF35E3152BA6659F246013A6F71DF1931CECF93D409E25A0A28DCC71AE95E7429477178294385A09C4FE640A19913C571EE70C3F3384853BE83C97A925AC6FDA4F521905FAD1ECE62AA45C1AA8A368A2F09C89CD9F45CED7160543D540CD38A37754D1D2EE4A45D324C2713793A0CEB9C2460DC08CDAAF9A423480C821552A1CCB66A282E4340033D19C0AC314AD0AEFCB0A43C7B111F8923E99398C29E41A787B688A4A65EA4A999D84370FC3B1ACC93E56D9D7D67A7CF20FC03B6CCC";

    public static final String TEMP_KEYSTORE = "tempKeyStore";
    public static final String TEMP_KEYSTORE_PASSWORD = "whatever";

    public void setupKeyStore() throws GeneralSecurityException, IOException {
        // createFile(TEMP_KEYSTORE, TEMP_KEY_STORE_CONTENTS);
        // this.keyStore = Utils.loadKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD);
        keyStore = Utils.loadKeyStore("keystore", "whatever");
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
            boolean failed = !f.delete();

            if (failed) {
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
        Writer.setInstance(getMockWriter());
        Miranda.getInstance().setWriter(getMockWriter());
    }

    public PublicKey loadPublicKey(String filename, String password, String alias) {
        String hexString = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            KeyStore keyStore = Utils.loadKeyStore(filename, password);
            JavaKeyStore javaKeyStore = new JavaKeyStore(keyStore);
            java.security.PublicKey publicKey = javaKeyStore.getPublicKey(alias);
            return new PublicKey(publicKey);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(byteArrayOutputStream);
        }

        return null;
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
