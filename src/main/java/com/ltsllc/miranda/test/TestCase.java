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
import com.ltsllc.clcl.*;
import com.ltsllc.clcl.Certificate;
import com.ltsllc.clcl.KeyPair;
import com.ltsllc.clcl.KeyStore;
import com.ltsllc.clcl.PrivateKey;
import com.ltsllc.clcl.PublicKey;
import com.ltsllc.commons.util.HexConverter;
import com.ltsllc.commons.util.ImprovedRandom;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Equivalent;
import com.ltsllc.miranda.clientinterface.objects.ClusterStatusObject;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.*;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.panics.StartupPanic;
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
import java.security.*;
import java.util.Calendar;
import java.util.Date;
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
    private JavaKeyStore keyStore;
    private JavaKeyStore trustStore;

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

    public JavaKeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(JavaKeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public JavaKeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(JavaKeyStore keyStore) {
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

        long lastTime = file.lastModified();

        if (!file.setLastModified(time)) {
            Exception e = new Exception("could not set the time of last modification of " + file);
            e.printStackTrace();
            System.exit(1);
        }

        if ((file.lastModified() - time) >= 1000) {
            Exception e = new Exception("time disprency in touch");
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

    public HttpServer getMockHttpServer() {
        return mockHttpServer;
    }

    public void reset() throws Exception {
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

    public void setup() throws Exception {
        StopState.initializeClass();

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

        setuplog4j();
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

    public void setupMockHttpServer() {
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

    public static void createFile(String filename) {
        SecureRandom random = new SecureRandom();
        byte[] contents = new byte[1024];
        random.nextBytes(contents);
        createFile(filename, contents);
    }

    public static void createFile(String filename, String contents) {
        FileOutputStream fileOutputStream = null;

        try {
            byte[] buffer = HexConverter.toByteArray(contents);

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
    public static final String TEMP_TRUSTSTORE_ALIAS = "CA";

    public void setupTrustStore() {
        try {
            deleteFile(TEMP_TRUSTSTORE);
            createFile(TEMP_TRUSTSTORE, TEMP_TRUST_STORE_CONTENTS);
            this.trustStore = new JavaKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD);
            Miranda.properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME, TEMP_TRUSTSTORE);
        } catch (EncryptionException e) {
            StartupPanic startupPanic = new StartupPanic("Eception loading truststore from " + TEMP_TRUSTSTORE,
                    e, StartupPanic.StartupReasons.ExceptionLoadingKeystore);
            Miranda.panicMiranda(startupPanic);
        }
    }

    public static void cleanupTrustStore() {
        deleteFile(TEMP_TRUSTSTORE);
    }



    public static final String TEMP_KEY_STORE_CONTENTS = "FEEDFEED00000002000000010000000100056D796B657900000162C670C2E90000018F3082018B300E060A2B060104012A02110101050004820177FAEEE38A311B04601727AA486F75C1A6B94810A5301392C826D3DC94162E310A2F04D5B454C335DBB9280B691450292F7213E936E6A0A82A7C9815E9BA98DD76FD9FA2ACC12EF60CC4CCB4041ABB9787A3F65038696E865DB70FCC03210A9019E963BBB5F65AF0B62DA089179AB21F84C2B17AD42B2D1CBA74BC6B413DC3826FAB2A43199641853CA6F2A406038B0B6FAFF9B0D8A6BA6FE0F7BFF991FD27DF2364EF15A55AE19A76E51CEBC99AFFB0F01E26922BF43EDFD784324C947926DB56BA70B6176230CF15096DDDF5526631FD8880034B45A6AB27FBC823103A0A66E00F58D495AAA8188C41CE05AF1B48531A3C54AB6B9A6022185C16FD730BFF03BCA32CBA48CA52F95DF2DA075182191AA4202D3BB30E3D27D5A83E7055BE06C8622CB8F5C70B9CD8DDBD3F1D6D621C10452B703FA22849FBEBADC172D33CC102792AA596C4841D20AE51E58CA3722DFDEE58C0DF6CF00A11559C968693F7FB414BE58D2CE168360E41D6274869AD7B268741793FA9A27882000000010005582E3530390000033930820335308202F2A00302010202047886F8B4300B06072A8648CE3804030500306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E301E170D3138303431343233313530305A170D3138303731333233313530305A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E308201B73082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A03818400028180776501B39BC05579C56084C050C0B0A8ED07BEF9961EB8FFA8E151F6D7B370BBFBA55E17BDBA0EABEAADCAEB743BF8680047D3E6EE5AE6A91B2561ABF811800244112499EF36A5E58907B02236D162B13C261AAC4B55EF6841BDD844C015EFA809839EC4A5BCCE564A692D65A2CF2256431F47D098C3C0B2E112903D0A9545E9A321301F301D0603551D0E041604145B83E0C7431B37FF53E943C6FFFD299F55A0C2B2300B06072A8648CE3804030500033000302D0215008B013074CA5E3A83F3CCFEB51094940E7EAAD953021437EAB563500945B1AF686174FB17C8750EAB90E0576959E69B1487A3378C92FE13A5933AADE29A3F";
    public static final String TEMP_KEYSTORE = "tempKeyStore";
    public static final String TEMP_KEYSTORE_PASSWORD = "whatever";
    public static final String TEMP_KEYSTORE_ALIAS = "server";
    public static final String TEMP_ALIAS = "mykey";

    public void setupKeyStore() {
        try {
            deleteFile(TEMP_KEYSTORE);
            createFile(TEMP_KEYSTORE, TEMP_KEY_STORE_CONTENTS);
            this.keyStore = new JavaKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD);
            Miranda.properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE, TEMP_KEYSTORE);
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
    }

    public void setupSecurity() throws Exception {
        deleteFile(TEMP_KEYSTORE);
        createKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD, TEMP_KEYSTORE_ALIAS);
        Miranda.properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE, TEMP_KEYSTORE);
        keyStore = new JavaKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD);

        deleteFile(TEMP_TRUSTSTORE);
        createKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD, TEMP_TRUSTSTORE_ALIAS);
        Miranda.properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME, TEMP_TRUSTSTORE);
        trustStore = new JavaKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD);
    }

    public void createKeyStore (String filename, String passwordString, String alias) throws Exception {
        KeyPair keyPair = KeyPair.createInstance();
        DistinguishedName distinguishedName = new DistinguishedName();
        distinguishedName.setCity("Denver");
        distinguishedName.setCompany("Long Term Software");
        distinguishedName.setCountryCode("US");
        distinguishedName.setDivision("Development");
        distinguishedName.setName("Clark Hobbie");
        distinguishedName.setState("Colorado");
        keyPair.getPublicKey().setDn(distinguishedName);
        keyPair.getPrivateKey().setDn(distinguishedName);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date aYearFromNow = calendar.getTime();
        CertificateSigningRequest certificateSigningRequest = keyPair.getPublicKey().createCertificateSigningRequest(keyPair.getPrivateKey());
        Certificate certificate = keyPair.getPrivateKey().sign(certificateSigningRequest, now, aYearFromNow);
        JavaKeyStore javaKeyStore = new JavaKeyStore();
        javaKeyStore.setFilename(filename);
        javaKeyStore.setPasswordString(passwordString);
        javaKeyStore.add(alias, keyPair, null);
        javaKeyStore.add(alias + "Certificate", certificate);
        javaKeyStore.store();
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

            contents = HexConverter.toHexString(buffer);
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

    public static void pause() {
        pause(1);
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

    public void setupMiranda() throws MirandaException {
        new Miranda("-p whatever");
        Miranda.properties = new MirandaProperties();
    }

    public static void setupTimer() throws MirandaException {
        Miranda.timer = new MirandaTimer();
    }


    public static String toJson(Object o) {
        return ourGson.toJson(o);
    }


    public static Version createVersion(Object o) throws Exception {
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

    public static void setupFileWatcherService(int period) throws MirandaException {
        Miranda.fileWatcher = new FileWatcherService(period);
        Miranda.fileWatcher.start();
    }


    public Network getMockNetwork() {
        return mockNetwork;
    }

    public boolean queueIsEmpty(BlockingQueue<Message> queue) {
        return 0 == queue.size();
    }

    public void setupWriter() throws MirandaException {
        com.ltsllc.miranda.writer.Writer writer = new com.ltsllc.miranda.writer.Writer(false, getMockPublicKey());

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
        Miranda.setInstance(getMockMiranda());
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

    public String loadHexString(String filename) throws IOException {
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

        return HexConverter.toHexString(byteArrayOutputStream.toByteArray());
    }

    public String loadPrivateKey(String filename, String password, String alias) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            KeyStore keyStore = new KeyStore(filename, password);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias);
            byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(privateKey);
            objectOutputStream.close();
            byteArrayOutputStream.close();
            byte[] data = byteArrayOutputStream.toByteArray();

            return HexConverter.toHexString(data);
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

    public boolean listContains(Equivalent item, List<Equivalent> list) {
        for (Equivalent element : list) {
            if (element.isEquivalentTo(item))
                return true;
        }

        return false;
    }

    public boolean listsAreEquivalent(List<Equivalent> l1, List<Equivalent> l2) {
        for (Object o : l1) {
            Equivalent item = (Equivalent) o;
            if (!listContains(item, l2))
                return false;
        }

        return true;
    }

    public String fileContentsAsHexString(String filename) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File file = new File(filename);
        int intLength = (int) file.length();
        byte[] buffer = new byte[intLength];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int bytesRead = fileInputStream.read(buffer);
            while (-1 != bytesRead)
            {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                bytesRead = fileInputStream.read(buffer);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return HexConverter.toHexString(byteArrayOutputStream.toByteArray());
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
