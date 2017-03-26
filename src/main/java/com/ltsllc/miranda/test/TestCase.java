package com.ltsllc.miranda.test;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaFactory;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.mockito.Mock;
import sun.nio.ch.Net;

import java.io.*;
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
    private BlockingQueue<Message> writer = new LinkedBlockingQueue<Message>();

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
    private SystemMessages mockSystemMessages;

    @Mock
    private SystemDeliveriesFile mockSystemDeliveriesFile;

    @Mock
    private Logger mockLogger;

    @Mock
    private MirandaTimer mockTimer;

    @Mock
    private MirandaProperties mockProperties;

    public MirandaTimer getMockTimer() {
        return mockTimer;
    }

    public SystemDeliveriesFile getMockSystemDeliveriesFile() {
        return mockSystemDeliveriesFile;
    }

    public SystemMessages getMockSystemMessages() {

        return mockSystemMessages;
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

    public MirandaProperties getMockProperties() {
        return mockProperties;
    }

    public static boolean deleteFile (File file) {
        if (file.isDirectory())
            return false;

        if (!file.exists())
            return true;

        return file.delete();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean deleteDirectory (String filename) {
        File file = new File(filename);
        return deleteDirectory(file);
    }

    public static boolean deleteDirectory (File directory) {
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

    public static long touch (File file) {
        long now = System.currentTimeMillis();
        return touch(file, now);
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public BlockingQueue<Message> getWriter() {
        return writer;
    }

    public MirandaPanicPolicy getMockPanicPolicy() {
        return mockPanicPolicy;
    }

    public Logger getMockLogger() {
        return mockLogger;
    }

    public void reset () {
        network = null;
        writer = null;

        this.mockMiranda = null;
        this.mockCluster = null;
        this.mockNetwork = null;
        this.mockWriter = null;
        this.mockPanicPolicy = null;
        this.mockUsersFile = null;
        this.mockTopicsFile = null;
        this.mockSubscriptionsFile = null;
        this.mockSystemMessages = null;
        this.mockSystemDeliveriesFile = null;
        this.mockLogger = null;
        this.mockTimer = null;
        this.mockProperties = null;
    }

    public void setup () {
        network = new LinkedBlockingQueue<Message>();
        writer = new LinkedBlockingQueue<Message>();

        this.mockMiranda = mock(Miranda.class);
        this.mockWriter = mock(Writer.class);
        this.mockNetwork = mock(Network.class);
        this.mockCluster = mock(Cluster.class);
        this.mockPanicPolicy = mock(MirandaPanicPolicy.class);
        this.mockUsersFile = mock(UsersFile.class);
        this.mockTopicsFile = mock(TopicsFile.class);
        this.mockSubscriptionsFile = mock(SubscriptionsFile.class);
        this.mockSystemMessages = mock(SystemMessages.class);
        this.mockSystemDeliveriesFile = mock(SystemDeliveriesFile.class);
        this.mockLogger = mock(Logger.class);
        this.mockTimer = mock(MirandaTimer.class);
        this.mockProperties = mock(MirandaProperties.class);
    }

    public void setupMockNetwork () {
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



    public static void setuplog4j () {
        putFile(LOG4J_CONFIG_FILENAME, LOG4J_CONFIG_FILE_CONTENTS);
        DOMConfigurator.configure(LOG4J_CONFIG_FILENAME);
        logger = Logger.getLogger(TestCase.class);
    }

    public static void putFile (String filename, String[] contents) {
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

    public static void createFile (String filename, String contents) {
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


    public static void createFile (String filename, String[] contents) {
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


    public static boolean createFile (File file, int size, Random random) {
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


    public static final String TRUST_STORE_CONTENTS = "FEEDFEED000000020000000100000002000263610000015A4D1FC4430005582E353039000003643082036030820248A003020102020900B9D2AA076007077B300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137323835385A170D3138303231373137323835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100C1403BF5214A0602C7390A651C515C25963459AA7BD20EA64F9BC25268C9DE404194E03EABBACB268BF490BA72C44C08E19F153F489BD445681F72F4F9D97C6F4E65F5D4D677CE783A49F7DD49C2D19B250F619E8C3A0B5A6F8ECAF149915259728821DDE5D624DC57B8E82DBCB490B64F6B98B97C2F34D304F3607C75DB6FE9970B2D3CA4093BDB35F1FC7DECEEF5AC76E780CF37EA24E5265C26D3661B72427F45EA2358C1DF7FAA00A2103719EA7ABC1FD76FF0DAF0DBDFC23C05C04FC0CF6D9D235815149C2D8C44758C24B511BA1F0DC6461A8724D699DB17C9389BF234FBDE6AC344BF3279ED0DC00C7BD6933A36126E46C8DD438AB50C5BBDD0E4C3E30203010001A3533051301D0603551D0E041604145E8A55312C059F0C3037878F429B864C5D9EBED9301F0603551D230418301680145E8A55312C059F0C3037878F429B864C5D9EBED9300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101000D9E54163305E2234DDFAAE7A95FDF25B2BBDFEA90A80A92551F42D2EE8CF18071C62EB276A42094CA5E7D6C3FA823F00B729152ECF1DCF1068724D68F584BFCB280A890E698C35A89587D5455F9705B5BE5BFDABFFA4FFC323018920882592BEA7FEC9926E8F9979D47CE99F64506F43349FD8ACE4A0424CF438EE30D33F25C9F798543591C32E89CDF909844AEAF3C8B6E07076DA79E18CA23702592F2B9FB4F05B282891A13C058C0F1AD4C570C0CC0676E59E8C012C97BA2BE1A0E85EA878260C202316FE083A15F361AF4D0823BDF0EC1205396C8443FD7F162B6B3D20A57A65BA4EAF4FFAA535ECB15C8EB6CE0DEA7128C421608AC150E65E10D82D2541DF093110F1120E7F827E85E9D79F0D8DE2E8111";
    public static final String TEMP_TRUSTSTORE = "tempTrustStore";
    public static final String TEMP_TRUSTSTORE_PASSWORD = "whatever";

    public void setupTrustStore () {
        MirandaProperties properties = Miranda.properties;
        properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE, TEMP_TRUSTSTORE);
        properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD, TEMP_TRUSTSTORE_PASSWORD);

        createFile(TEMP_TRUSTSTORE, TRUST_STORE_CONTENTS);
    }

    public static void cleanupTrustStore () {
        deleteFile(TEMP_TRUSTSTORE);
    }


    public static final String KEY_STORE_CONTENTS = "FEEDFEED000000020000000200000002000263610000015A4D29B1EB0005582E353039000003643082036030820248A003020102020900B9D2AA076007077B300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137323835385A170D3138303231373137323835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100C1403BF5214A0602C7390A651C515C25963459AA7BD20EA64F9BC25268C9DE404194E03EABBACB268BF490BA72C44C08E19F153F489BD445681F72F4F9D97C6F4E65F5D4D677CE783A49F7DD49C2D19B250F619E8C3A0B5A6F8ECAF149915259728821DDE5D624DC57B8E82DBCB490B64F6B98B97C2F34D304F3607C75DB6FE9970B2D3CA4093BDB35F1FC7DECEEF5AC76E780CF37EA24E5265C26D3661B72427F45EA2358C1DF7FAA00A2103719EA7ABC1FD76FF0DAF0DBDFC23C05C04FC0CF6D9D235815149C2D8C44758C24B511BA1F0DC6461A8724D699DB17C9389BF234FBDE6AC344BF3279ED0DC00C7BD6933A36126E46C8DD438AB50C5BBDD0E4C3E30203010001A3533051301D0603551D0E041604145E8A55312C059F0C3037878F429B864C5D9EBED9301F0603551D230418301680145E8A55312C059F0C3037878F429B864C5D9EBED9300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101000D9E54163305E2234DDFAAE7A95FDF25B2BBDFEA90A80A92551F42D2EE8CF18071C62EB276A42094CA5E7D6C3FA823F00B729152ECF1DCF1068724D68F584BFCB280A890E698C35A89587D5455F9705B5BE5BFDABFFA4FFC323018920882592BEA7FEC9926E8F9979D47CE99F64506F43349FD8ACE4A0424CF438EE30D33F25C9F798543591C32E89CDF909844AEAF3C8B6E07076DA79E18CA23702592F2B9FB4F05B282891A13C058C0F1AD4C570C0CC0676E59E8C012C97BA2BE1A0E85EA878260C202316FE083A15F361AF4D0823BDF0EC1205396C8443FD7F162B6B3D20A57A65BA4EAF4FFAA535ECB15C8EB6CE0DEA7128C421608AC150E65E10D82D2540000000100067365727665720000015A4D29FC5C00000502308204FE300E060A2B060104012A021101010500048204EA65F71C7A5EB60E7D6F9201EBCEA7FCFDECB29233B5EA581516F71448B14EFC277B1A8A90EE7965E8C683EEB0AB266316CDF90FA2D93C600CA150B9F453F589FC965E9613175DF0B4C0A9AC70EA3AEE377B18A9ABAE431456F71D4CB02F9E9FD2222B3DEC0F17BC1AAE1B049FF6993CF9E399EEAA2259023F67FBDCF61FAA69EEBC9236CBEC0655D8F1646327BD9735C27D09707A369C8EC4BC24A367FFE5DDF846857E478D41D72B18E1C6C68EDD51AB3FD9324EDBD30F4414418039550025D2E7392952AC4B7936C4780FBE6039065765D4063972926F9C8056A3ED910227A6E9149D6D6AD542DDAA23D013BCB8F89FB86E6AB750099D69C3B197C0D9DA8FEAB704DB4185FAF68047228B2040E02238E7534EB323939AFE2F9351641A2DD41E6CB7D7F84F50FB7D64790028FC6F8752C8A9631B1BB754EA31424840A21148A2C23E8A63505FB6A394A1C9BA248BE0F436BF684B0BB39CAB2685E3C4983A9BA63068D2AEE4A2B74DB9CB2C05196681DE5192966B13C93CB58FFA5AB6B75936F104E3B6410C048FEF93A878B8B0BD60024402312A5F21CE6851CE6CE69DC0DC4885AD7F53F0E7171ACC4157BE1D3060844F67E1EBEF7E52B09A42CCFCFD0E95497FA6708971508488B7727EFA437359FF4CF4B46F1F433B33F6E8E1AD552C826A808C55203EE3151DBEA09D5134D8ED33D900BA13309B1051BDA225E61F5B831A5AEF086488D8FC4C145B9964CA9FA2CCEFBA9FFEC03F960DC23DB22B40C335F510BC3569B0A3EB1A186912639D429F9144013F07DE611B94EF71B748DF745A3B0AE65C8CB122EBCA90656E6DC7B3B07123B997957569938FA8CFC328CC201A2784FADF0F76D23F5138EC4D61EBA2FCC83CA7F3806B88C851161345D0101AEF9FE3A2866084437D4885A8A4F48204C39B3C17C48E9A66D9595F445D1E3D1A4F74854B40F2A8CFA6903499FB5AEADE7209CC48716001F32618ABFDA404F22F4885753653C173AE527D1BA55B564C4079A61A66CAF2A5F2F926FBDF91FB52CA9BB9E6EC1B9DCFF796681FAD86C28041256C1F4527B8BE29FDA02F8CD92C5D403AC8B4A1376F8BB2EAF37D61EA694C12D465C043AA8844EACDA50991EB8163677555F83E7DD85C0BDE59ACA20B6D3A6F51F1A23666E539CF9A5BE55106F9C5D09A39A6775D07013249384D37AC6110C698F495ACDABB09BE62B641EAADE18BF15D7BC271E3AC4DB1A9DBA1906C3355869BA43B5ACC1C89EEC6D3819728854429756E9B1A9E368173E81E31429014DE31B4370CCA83DF24AE3B4E72A08B55E3D2EF0DAE01C877D93E38E02DB0E4377DE717926CEFC4D482E774580DA528A1BB310A8463A40F93294D1C9D59EA7652CB058D4E258E1CA3D1A9131A8A19506E52CD82CC52B595054CA299326E6C4F723319045C434C13360BD895DA0804B7B2546B3B10C2BA8397F83A733B6957D265E3831F88786F55EADBFFD5735D63F5D6965D0E59BC3C4FA16C79BB8C7128899B7E7BB232314F0ACB321FEBA5B0BD7FE77993B18712B58030DB5C3CB3B5E1DD01FEC64BBD8CD7B825C8AF3F8D7B7C027C3CC9F60B55C6AA4E6D221B1B0EB84EB5A4CE67725B6CD2766CECEAA048EA571ADA7B189DB7742D9DA79FE093375846B9CD3A07F7A65FF6373E63AE151FC6634D420741A9AF468A2426DE5677AF3FDB2A6DBC9B1294B5CD92341FD2295B85F42E5DBD9AB59C3C7C1C0641791D2209CC882E02575952FE3B2C3F78D9093D2F68D797F4959B2D8B36DD8267439099C6000000020005582E353039000003313082032D30820215020900A57B24B7D63CE15D300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137343033335A170D3138303231373137343033335A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100B182939004DC4123A6AE31C00BC9AED6EC093661C4C72C8AFBE41DF10803F555141F26BC0A663EF769D4EB176DB0BD91B9B774A59C5186AC745F95F0560FE9266515B21535653983A07E68E2E2E5416F43CA2486E4EFE8DD649AB5A17542C8A9B3B7C6E83F04D21F0AC5E1808784C80D6A63FEAADDD70969A1A8A5D79338C2DD3256F6BEF2D6D21F4E00CF7A05A46D4FF1BFD73489A559C6A0408DEB617AF2D8AF96BD76776FBEABDF461D275745AA58F1251C02276E86727066C9A2185C88A8870997AA7F1F4C63E7541BE18FEFE1B81F6627ED50D67DEB8390100305CF6767514139DC1F8872ED872188C4F3B9712D32B0D5D252496235C66D6C86E670FE650203010001300D06092A864886F70D01010B050003820101008A8305BD8C85A2CB7257BB5EC7C5AC0AF7117196C205EF5D9641E22409F23441BEAF4E2ED94E0066BE7B304124F8A2284B18DFD80A0D59ABF616AEEBC84BE08261EE86E2D9E3AE4C4C42475E8F4A80D414C4C5DE0151F26523B6B4BA4CC5303E7683804C196F29271547D48E05BF754A1F781618805985EAD293A937181733D903AAF9AC88FBFEEF41C0D14E98CCC030C8321D9B6D15F191145717078440134D1FE841D1A6769F081DCD9B3858E7CDE49E99CEA31D6DC72043043DB6AE8AF6BA6B62CF0EB447A411283E3D54421CD3B1826C841377977EA3C9A58EAAA8C3ADA7FA1237E4E33B9543D44B1CB44EC77E16E01D05C4140A048241D2B7B9D82E6FAC0005582E353039000003643082036030820248A003020102020900B9D2AA076007077B300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137323835385A170D3138303231373137323835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100C1403BF5214A0602C7390A651C515C25963459AA7BD20EA64F9BC25268C9DE404194E03EABBACB268BF490BA72C44C08E19F153F489BD445681F72F4F9D97C6F4E65F5D4D677CE783A49F7DD49C2D19B250F619E8C3A0B5A6F8ECAF149915259728821DDE5D624DC57B8E82DBCB490B64F6B98B97C2F34D304F3607C75DB6FE9970B2D3CA4093BDB35F1FC7DECEEF5AC76E780CF37EA24E5265C26D3661B72427F45EA2358C1DF7FAA00A2103719EA7ABC1FD76FF0DAF0DBDFC23C05C04FC0CF6D9D235815149C2D8C44758C24B511BA1F0DC6461A8724D699DB17C9389BF234FBDE6AC344BF3279ED0DC00C7BD6933A36126E46C8DD438AB50C5BBDD0E4C3E30203010001A3533051301D0603551D0E041604145E8A55312C059F0C3037878F429B864C5D9EBED9301F0603551D230418301680145E8A55312C059F0C3037878F429B864C5D9EBED9300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101000D9E54163305E2234DDFAAE7A95FDF25B2BBDFEA90A80A92551F42D2EE8CF18071C62EB276A42094CA5E7D6C3FA823F00B729152ECF1DCF1068724D68F584BFCB280A890E698C35A89587D5455F9705B5BE5BFDABFFA4FFC323018920882592BEA7FEC9926E8F9979D47CE99F64506F43349FD8ACE4A0424CF438EE30D33F25C9F798543591C32E89CDF909844AEAF3C8B6E07076DA79E18CA23702592F2B9FB4F05B282891A13C058C0F1AD4C570C0CC0676E59E8C012C97BA2BE1A0E85EA878260C202316FE083A15F361AF4D0823BDF0EC1205396C8443FD7F162B6B3D20A57A65BA4EAF4FFAA535ECB15C8EB6CE0DEA7128C421608AC150E65E10D82D254DB479DBE9D0502221495C5977C87D501724545E8";
    public static final String TEMP_KEYSTORE = "tempKeyStore";
    public static final String TEMP_KEYSTORE_PASSWORD = "whatever";

    public void setupKeyStore () {
        MirandaProperties properties = Miranda.properties;
        properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE, TEMP_KEYSTORE);
        properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD, TEMP_KEYSTORE_PASSWORD);

        String filename = TEMP_KEYSTORE;

        deleteFile(filename);
        createFile(filename, KEY_STORE_CONTENTS);
    }

    public void cleanupKeyStore () {
        deleteFile(TEMP_KEYSTORE);
    }


    public static String readContents (String filename) {
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

    public static void deleteFile (String filename) {
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


    public static void pause (long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send (Message message, BlockingQueue queue) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean contains (Message.Subjects subject, BlockingQueue<Message> queue)
    {
        for (Message m : queue) {
            if (subject.equals(m.getSubject())) {
                return true;
            }
        }

        return false;
    }

    public boolean containsNetworkMessage (WireMessage wireMessage, BlockingQueue<Message> queue) {
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

    public void setupMiranda () {
        String[] empty = {};

        new Miranda(empty);
    }

    public static void setupTimer () {
        Miranda.timer = new MirandaTimer();
    }


    public static String toJson (Object o) {
        return ourGson.toJson(o);
    }


    public static Version createVersion (Object o) throws NoSuchAlgorithmException {
        String json = toJson(o);
        return new Version (json);
    }

    public boolean contains (Object o, List list)
    {
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

    public boolean createFileSystem (String rootFilename, String[][] spec) {
        ImprovedRandom random = new ImprovedRandom();
        File root = new File(rootFilename);
        FileCreator randomFileCreator = new RandomFileCreator(1024, random);
        MirandaProperties properties = Miranda.properties;
        int maxNumberOfEvents = 1 + properties.getIntegerProperty(MirandaProperties.PROPERTY_MESSAGE_FILE_SIZE);
        FileCreator eventFileCreator = new EventFileCreator(random, maxNumberOfEvents,1024);
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


    public boolean collectedAfter (long time, MirandaFile file) {
        return file.getLastCollection() > time;
    }


    public boolean collectedAfter (long time, List<MirandaFile> files) {
        for (MirandaFile file : files) {
            if (!collectedAfter(time, file))
                return false;
        }

        return true;
    }

    public static void setupFileWatcher (int period) {
        Miranda.fileWatcher = new FileWatcherService(period);
        Miranda.fileWatcher.start();
    }

    public Network getMockNetwork () {
        return mockNetwork;
    }

    public boolean queueIsEmpty (BlockingQueue<Message> queue)
    {
        return 0 == queue.size();
    }

    public void setupWriter () {
        com.ltsllc.miranda.writer.Writer writer = new com.ltsllc.miranda.writer.Writer();

        this.writer = new LinkedBlockingQueue<Message>();
        writer.setQueue(this.writer);
    }

    public static void makeFile (String filename, byte[] data) {
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

    public void setupMockCluster () {
        if (null == getMockCluster())
            this.mockCluster = mock(Cluster.class);

        Cluster.setInstance(getMockCluster());
    }

    public void setupMirandaFactory () {
        setupMirandaProperties();

        Miranda.factory = new MirandaFactory(Miranda.properties);
    }

    public void setupMockMiranda () {
        this.mockMiranda = mock(Miranda.class);
        Miranda.setInstance(mockMiranda);
    }

    public void setupMockPanicPolicy () {
        Miranda.getInstance().setPanicPolicy(mockPanicPolicy);
    }

    public void setupMockUsersFile () {
        UsersFile.setInstance(getMockUsersFile());
    }

    public void setupMockTopicsFile () {
        TopicsFile.setInstance(getMockTopicsFile());
    }

    public void setupMockSubscriptionsFile () {
        SubscriptionsFile.setInstance(getMockSubscriptionsFile());
    }

    public void setupMockSystemMessages () {
        SystemMessages.setInstance(getMockSystemMessages());
    }

    public void setupMockSystemDeliveries () {
        SystemDeliveriesFile.setInstance(getMockSystemDeliveriesFile());
    }

    public void setupMockTimer () {
        Miranda.timer = getMockTimer();
    }

    public void setupMockProperties () {
        Miranda.properties = getMockProperties();
    }
}
