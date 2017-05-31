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
import java.security.cert.X509Certificate;
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


    public static final String TRUST_STORE_CONTENTS = "FEEDFEED000000020000000100000002000263610000015BC0483B9E0005582E353039000003643082036030820248A003020102020900E7BAF66A957188F5300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303433303139313235305A170D3138303433303139313235305A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100DCCA8344007EABD37C0CA96F456054029E157D193A3C3BFAE6B2EB5255708A60A513989305BE701C968CF77A99130C352AA08E0D10C80796453B5FED8967B0F11B079FD813A4E34C02A7708DE9F9C0FD132F155AAC773D28C24981700D8585F2612E3E87884A53416A459AAB0C827106281F51643D6705A4FC8C14166765393249189E82BB49EF2159AD4390CA61F1B366BC0752D6906FCCCD61A24B7BAC5AEB9793AF09DBD732FDDA7E9B460161F99DD6481C8507B6773171610E287BDADE4E29BB62D751B0D4935844967BBFD751A0993B6C5C9A6A36634AEFDB65A41ABA5B9C96C0E2581FDF7EA660F2416545BAC35EAF24D79D2DA66D453FB0B042679B4B0203010001A3533051301D0603551D0E0416041490D43024D13A91CFF8FE655B96A7795EB9A8CFB4301F0603551D2304183016801490D43024D13A91CFF8FE655B96A7795EB9A8CFB4300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101006F01C6F04E73588EE1C2222D5D46DE01FA133B33049543C39360F803E6D0AD49D825A106044AEC02B033C52375A1B73CC2C5C8C709A295614232B2B9EB252850623F9E1BAFF7B683DFC4CCF422D6418F1E5D68F9574572A803BF40DB3AD7AFA45EE14573B16C751E342589DADB50C8D91387559F083B9EF7DA5CB01C127C5E0F6212AEC5C406300A4FB59BC133778EC5EC9538C61ACA8A1EE85C8AA755A42997D11B0CF1D3FB84A9E53B831F8AEE4F3FDFA7F7F0A18AE2910172E404307D9B43AA7596A6315BFBF783CD2F8632DC438D1240EAF796CD91EE8B7B76BB465474E648B243705DD0969A2742BF237D1CD0ACD800EF5FACBE877C9FEBA1593921E0F5B195C578220A4763D600CE92C57B291B548D60BF";
    public static final String TEMP_TRUSTSTORE = "tempTrustStore";
    public static final String TEMP_TRUSTSTORE_PASSWORD = "whatever";

    public void setupTrustStore() throws Exception {
        MirandaProperties properties = Miranda.properties;
        properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME, TEMP_TRUSTSTORE);

        createFile(TEMP_TRUSTSTORE, TRUST_STORE_CONTENTS);

        this.trustStore = Utils.loadKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD);
    }

    public static void cleanupTrustStore() {
        deleteFile(TEMP_TRUSTSTORE);
    }


    public static final String KEY_STORE_CONTENTS = "FEEDFEED000000020000000300000002000B63657274696669636174650000015C1E78075F0005582E353039000003313082032D30820215020900EAF78ECEAFD426C9300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531393032303932355A170D3138303531393032303932355A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100D37F95D64584062347B9BCF6653EB3D4D33E20857C75168337B03BBB58C02B98BD644978F1EC5655AACB75DEF4B9112B5852C410F0BBD48524903A10D3C8E98065844F0423DD924A8D811F07CCAA66003E35185A9225A3EA96738BE0493DA31799AF50B0BDF72B67961F6F8695A31514267E8AD0DF19C4CA9D98447F24E262C387757E4547E89A06C2DA07E3060F4A6EFC49303249D8834B8C30EAC9F39940C23F8BB0DD6ACB67BC168AD6E1499322453EF0CC2BFF8FE4309A48F2FCD35C4C9D7E07C8486B9AFED517A91FC1026A34E1CF13D56EA06304D2B7DC0E34BE56FF98F12A3F086CADD405BA1CDD44F506D862B39E709E47800A7ACF6111FECEFE87650203010001300D06092A864886F70D01010B0500038201010051B917B37EE86013E5431F5347D344E2CDC15A07E6351155267A0781543BFFE9DF64A7C98D074ED3464D32419A55E7AD1E5E80592E95475A2C154274FFD0526285CA71E2FA3BFD0A4C6E08C09B1E5DDBA7E88394260360117FBEF3890E63331FD0EE168F4978E630D5E2A55FDD869D7A278670219C518ECA2FED7010DA63501350A1DFC9DDE0DE2B875C668DD7077958BFC8C45C445FB9CF09F308EBAFF74EA8B5659911102D4E989C018E3129925FAF259C374DD4FF002A8926CD131BBD02C6709047F62C0D1E144CC0D6AA91595605D80C279F214762B4EF6AEF9C6BE91669B9EDAC4B71911439044B47CE4F9D3108C856A6E52592D7401010D4EDC746AFF700000002000263610000015C1E77D26A0005582E353039000003643082036030820248A003020102020900951A1546B7E95ABF300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303531383136333835385A170D3138303531383136333835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100D1B02C7DFFF1CE31C7E97C7E6629ED959255738B9B2A77DE5BA006BC586DFC6CA0F203E27A8BF9486412B45D5162703C9118E9A904F7FBFB1D5FAEE59CEADCD8E574B5E84B59CDCC12A57DAFF38066EB67DCA5503EB46841157A7C6FD43E1D535ACEEA76188463D8FBE086F6C92C83DB3B822C3A1FA88356A64C98DEF8532EA00F7ED6FD0DB14EC02F50C4D3151FE63E7D3D0D260E5318A5D3F2696906240CA3BC5A1EB10B3F66D7017080950FFFC99BDF38C564D877E51F7B01AC69BC48459B307D1DE1F951F4DE39D3C39117E448A9C9F9D7E940428729603F7C9664B169B246D7C3B9681F807EB2C0ED85AF5ADFD59E097D7B4A371F777465B7283C7C29D50203010001A3533051301D0603551D0E041604141F3008D3090510954EDFAC4949AA240CA1A801C7301F0603551D230418301680141F3008D3090510954EDFAC4949AA240CA1A801C7300F0603551D130101FF040530030101FF300D06092A864886F70D01010B0500038201010040B331E7A9214ECB5C79F70C746C75DCF8BEDF0F36B963396E9145FAFCA9308CAE82FF15F6072E9AB57C5A5C9058E9B1C7CD5FB014F9DB4C67470DF32D50B1B9B2AF5C8DCD5E84DC3BE7D328019447E538B24AC58589D2717A2FB9CB2768E06266381CC072C544D260BE89DB803A4D3DE31CCA8C34E3A944C385FCC9B6EE06B1D6904F1346BC131F1B31A192C1A12BE3409739251851F8770792DC7421F81DEEF24E2E7C8FD63075E769C8B8352B0F9AB04920EDEA4BBD6B7CEB61F3DE3E17C93422809D7143D88F6F56ADAB440C3F3B6473015DCB922B617D823C301F036F4478AAC0FB690A615CEA4D77F0863AE880C48DBB0319067DCECD43E12BC6C92FA4000000010007707269766174650000015C1E76367A00000501308204FD300E060A2B060104012A021101010500048204E952758965A50EDC6486AE9F4B9B4030B452A5EBA3B750EE9D9A99F406CAC9F4F83E6419D729164F45B3D1CECA8D00FC18192C665E8D6B707F512FB0F0E487AA8A6DD090A24771EFA7C71642742B835C38B2E21118BC3E32733AB7D847374860508208A2FAAF7865FAAE2BCD59B097D21F516AB1D8405B485CDB9417F7A77AAEFBF07220B0E30984A1B35764FD70F969D4CE0CCAB300FA1030348AE3E78E3122E3E5990921FDE212BD20CD46DB1F2BF8AEABAAA44E068A42BE063686168514EC9B6F807467CA800D9394710BE9C29D192DC7E598CE320127FCB9A262198867F6D0CD2457BC509A557869A329FA76E297DE5A8C8104E784AF5F28259325A2013EE112A0343387C954CD85C3C085ABD5E51853364F46E82B55E494E34DEA25EA70A14AF0FCD96D72E4828810659133A5E87085B3808AB70E66162F1977D1213A4E7ED814ABFD351363F45BE5611ABF1396E08122279225ACE38E53A8AA0956B96FDBECC4B229458B32F3148C1478F16A15BA34FEF40FF5A3CDE8B4F43B4FF30FAFFAF3071C8564DD18131D6A571B2757CCF0B2E065DD546920E407914B35276CC75675259EF1A7415C7FC733ED33B70DD7CED221E44BDDED370F16016108CF0AFBBD0491BD8A632C6649E6B95AF762353C469DE71D37BD7D073110311AA420462817CEEA0FF1ACDE3D7271DD50833E454AA1C760B65B69ACFCBA536B1512E48896E541032871115D7E4536722173C7216F0FB516C685B48A742E93A4C3F224BCAEB3E06684AF81BCD21B3FA57918474266C3085B284DF50B7391A65B461B6258A7C22C5F117F0AE844096A878BC6E0E89BCCEF4E33DFAAB64F7EA372C462F90432492E97454A83391E706F1D54CF327C3D0E81778996D72D5E7BAEF91C9654C1F6237EB08C2D40473E037CDC089BCFEF1C427983DEA44DB07314F3EB4158A912C4AB3B37B2356C8B3BC3D72F2563DD5DFE8538609665EEF29B9D0157F470224F2EA1EA98A3152B32D1BD7BAF327CC5A33E28F5EA0941534E1882EF87558D348991972F247D243275F800A62B24CF26DB645EE5B0FBAA56015A6CCF4A04F4ED6CEC3C0F29433058E03D7A56671E74DBE46FAB7EDA5DF53D2F9489ED501257ACB8707B877B617068471F6E23ADD7062516F342477552863AA86509061CF727AF8894B5E42DFD5F56057947E5477ABC9A3CF4828BE969C46AB7E94DD62DB29BC0973E02C4D37343B0B2E510098E1D000473B5672D7CB5A9FF9777EE18ACA6F0FE79523D3E7B4B46818B608571900667A4969A7CC103C8A7A7D9E1CCC37A8AAAB9BA2CC9B02FB2770A4126E2C3F9D21FA1AEF11257C939FADFE8CBC85DF5759E408BC4637F82376A0E1583CFB9C4AB731D62E9839AD80EB1D09B41CBE27BE07B52E80165F0F4024BE646C964D2A4D770D233874D8C3D211AE6111E78BF47D4460FD99997B067D9AAE374528C4164F4551AC84A90988B1F8A34DB4E8EE883C43AB4169A3A39532D1986B681DFBECAEBC708192AAE22306107F022F1D3AF91DF8F782BB67BEC2CADC26F8FBC726E4CF42D4FD29EC29ABF9EA40809E8060742777B1151B388A7555835AA8CB6878CBE087A31CFD66FDE30884D4CDA8471FBC15A92161E13DF17B9E23ACE70B2474FEB6C27FB398892829E9927EAF73A0C6E67F90206A02A97C72586FD37EE4EDA4514CF746D1FC73CBFF82A0FB25A3104BFE1E522C6A63A89F56A338064CD2E737C88C47B46DB39EB39CF4F54CB56D1141AB9776A10CF520B847647BF9A23E1D868000000010005582E3530390000037B308203773082025FA003020102020417FFFCC9300D06092A864886F70D01010B0500306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E301E170D3137303531393032303830315A170D3137303831373032303830315A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100D37F95D64584062347B9BCF6653EB3D4D33E20857C75168337B03BBB58C02B98BD644978F1EC5655AACB75DEF4B9112B5852C410F0BBD48524903A10D3C8E98065844F0423DD924A8D811F07CCAA66003E35185A9225A3EA96738BE0493DA31799AF50B0BDF72B67961F6F8695A31514267E8AD0DF19C4CA9D98447F24E262C387757E4547E89A06C2DA07E3060F4A6EFC49303249D8834B8C30EAC9F39940C23F8BB0DD6ACB67BC168AD6E1499322453EF0CC2BFF8FE4309A48F2FCD35C4C9D7E07C8486B9AFED517A91FC1026A34E1CF13D56EA06304D2B7DC0E34BE56FF98F12A3F086CADD405BA1CDD44F506D862B39E709E47800A7ACF6111FECEFE87650203010001A321301F301D0603551D0E0416041410C6CC0AFC2CC780252C5ABCD4A96AC15D4AEB54300D06092A864886F70D01010B050003820101005729324786945AE2FBB3901806CC73C8FF5D7B80EE2BCE9DA24875BA0CC65C8224CB8C034F8DB7C67D37D2DBD52903A8A2254A55018CFAED620AB220CDAC0758AB41A7D7D75727C26D14E7EA7E4E81EA63486E46FD3272E43328D7F3B7B2A28CD37409F7E0462BDC1D45F785719B8D533843CED8C5930AC44E30CFEA69A9D0C7E50AAEC46F9B82D0693E98526BF9C886ADEC75E4BD2D8ED693DF44C74A765C56A5BBA10C9E3AE257AE66217FFEB40978A7F6054DA627C8BE5969FA3E9D635165D2E08ED4E26CA74399A1B79AF1E4E07F7BA6162FB12E05E0B0DF5824399E605A5C61CAED0053AC466CD5EF4A2BD3C107B95BA55AE2204E249C6491924F3D09EDE18DB0B3D0C145CC49526C21A80D231D6B86B462";
    public static final String TEMP_KEYSTORE = "tempKeyStore";
    public static final String TEMP_KEYSTORE_PASSWORD = "whatever";

    public void setupKeyStore() throws GeneralSecurityException, IOException {
        MirandaProperties properties = Miranda.properties;
        properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE, TEMP_KEYSTORE);

        String filename = TEMP_KEYSTORE;

        deleteFile(filename);
        createFile(filename, KEY_STORE_CONTENTS);

        this.keyStore = Utils.loadKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD);
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
