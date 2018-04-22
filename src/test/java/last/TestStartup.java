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

package last;

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.ShutdownException;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.PanicPolicy;
import com.ltsllc.miranda.miranda.Startup;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.states.ReadyState;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.UsersFile;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/5/2017.
 */
public class TestStartup extends TestCase {
    public static class LocalPanicPolicy implements PanicPolicy {
        public void panic(Panic panic) {
            throw new ShutdownException("shutdown");
        }
    }

    public static Logger logger = Logger.getLogger(TestStartup.class);


    public static final String TEST_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1avWB4w2AtIN/DOSyyDu\n"
            + "dN7OA3XVbjyq9cKkVkLtHuKQYvq2w1sFoToeZ15R+J7WxDGFuSzdWa/RbR5LLNeM\n"
            + "BqgGZ+/jwGOipRtUMVa8467ZV5BL6vowkjAyUUevTABUxzTo+YvwrL8LPVpNOO1v\n"
            + "VmAsWOe+lTyeQkAILaSeCvyjdmDRr5O5U5UILlAcZDJ8LFOm9kNQQ4yIVUqAMbBo\n"
            + "MF+vPrmEA09tMqrmR5lb4RsmAUlDxiMWCU9AxwWfksHbd7fV8puvnxjuI1+TZ7SS\n"
            + "Fk1L/bPothhCjsWYr4RMVDluzSAgqsFbAgLXGpraDibVOOrmmBtG2ngu9NJV5fGA\n"
            + "NwIDAQAB\n"
            + "-----END PUBLIC KEY-----";


    private Startup startup;

    private BlockingQueue<Message> cluserFile;
    private BlockingQueue<Message> usersFile;
    private BlockingQueue<Message> topicFile;
    private BlockingQueue<Message> subscriptionsFile;
    private BlockingQueue<Message> messages;
    private BlockingQueue<Message> deliveries;
    private BlockingQueue<Message> startupQueue;

    public Miranda getMiranda() {
        return Miranda.getInstance();
    }


    public Startup getStartup() {
        return startup;
    }

    public void reset() throws Exception {
        super.reset();

        Miranda miranda = Miranda.getInstance();
        if (null != miranda) {
            miranda.stop();
        }

        Miranda.setInstance(null);
        this.startup = null;
    }

    public BlockingQueue<Message> getCluserFile() {
        return cluserFile;
    }

    public BlockingQueue<Message> getUsersFile() {
        return usersFile;
    }

    public BlockingQueue<Message> getTopicFile() {
        return topicFile;
    }

    public BlockingQueue<Message> getSubscriptionsFile() {
        return subscriptionsFile;
    }

    public BlockingQueue<Message> getMessages() {
        return messages;
    }

    public BlockingQueue<Message> getDeliveries() {
        return deliveries;
    }

    public BlockingQueue<Message> getStartupQueue() {
        return startupQueue;
    }

    @Before
    public void setup() throws Exception {
        reset();

        super.setup();

        setupMiranda();
        setupMirandaProperties();
        setupSecurity();
        setuplog4j();

        String[] args = {
                "-p",
                "whatever",
                "-t",
                "whatever"
        };
        setupMockPanicPolicy();
        this.startup = new Startup(getMiranda(), args);
    }

    @After
    public void cleanup () {
        if (null != getMiranda()) {
            getMiranda().stop();
        }
    }

    @Test
    public void testStartMethod () {
        try {
            setupMiranda();
            setupMockReader();
            setupMockHttpServer();
            getStartup().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStart() throws Exception {
        Miranda.getInstance().setKeyStore(getKeyStore());
        Miranda.getInstance().setTrustStore(getTrustStore());
        setuplog4j();

        setupMockReader();
        setupMockHttpServer();


        long then = System.currentTimeMillis();

        MirandaProperties mirandaProperties = new MirandaProperties();
        mirandaProperties.setProperty(MirandaProperties.PROPERTY_HTTP_SSL_PORT, "20000");
        Miranda.properties = mirandaProperties;
        Miranda.getInstance().setPanicPolicy(getMockPanicPolicy());
        setupSecurity();

        Properties properties = new Properties();
        properties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE, TEMP_KEYSTORE);
        properties.setProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME, TEMP_TRUSTSTORE);
        getStartup().setOverrideProperties(properties);
        getMiranda().setCurrentState(getStartup());

        getMiranda().start("-p whatever -u whatever", getKeyStore(), getTrustStore());

        pause(5000);

        //
        // test that local variables got set
        //
        System.out.println("current state = " + getMiranda().getCurrentState().getClass());
        assert (getMiranda().getCurrentState() instanceof ReadyState);
        assert (getMiranda().getCluster() != null);
        assert (getMiranda().getUserManager() != null);
        assert (getMiranda().getPanicPolicy() != null);
        assert (getMiranda().getEventManager() != null);
        assert (getMiranda().getDeliveryManager() != null);
        assert (getMiranda().getTopicManager() != null);
        assert (getMiranda().getHttpServer() != null);
        assert (getMiranda().getSubscriptionManager() != null);
        assert (getMiranda().getSessionManager() != null);
        assert (getMiranda().getWriter() != null);
        assert (getMiranda().getCommandLine() != null);

        //
        // test that static variables got set
        //
        assert (Miranda.getLogger() != null);
        assert (Miranda.timer != null);
        assert (Miranda.fileWatcher != null);
        assert (Miranda.properties != null);

        //
        // test that initial garbage collection got done
        //
        assert (getMiranda().getSubscriptionManager().getSubscriptionsFile().getLastCollection() > then);
        assert (getMiranda().getTopicManager().getTopicsFile().getLastCollection() > then);

        verify(getMockPanicPolicy(), never()).panic(Matchers.any(Panic.class));
    }

    public boolean containsRootUser(UsersFile usersFile) {
        User root = new User("root", "System admin", "Admin", TEST_PUBLIC_KEY_PEM);
        for (User user : usersFile.getData()) {
            if (user.equals(root))
                return true;
        }

        return false;
    }

    @Test
    public void testGetKeysKeystoreUndefined() throws Exception {
        ShutdownException shutdownException = null;
        try {
            setupMiranda();
            Miranda.getInstance().setPanicPolicy(new LocalPanicPolicy());
            MirandaProperties mirandaProperties = new MirandaProperties();
            mirandaProperties.remove(MirandaProperties.PROPERTY_KEYSTORE_FILE);
            getStartup().setProperties(mirandaProperties);
            getStartup().getKeys("whatever");
        } catch (ShutdownException e) {
            shutdownException = e;
        }

        assert (null != shutdownException);
    }

    @Test
    public void testGetKeysPrivateKeyAliasUndefined() throws Exception {
        ShutdownException shutdownException = null;

        setupMiranda();
        Miranda.getInstance().setPanicPolicy(new LocalPanicPolicy());
        MirandaProperties mirandaProperties = new MirandaProperties();
        mirandaProperties.remove(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);
        getStartup().setProperties(mirandaProperties);
        try {
            getStartup().getKeys(getStartup().getKeystorePasswordString());
        } catch (ShutdownException e) {
            shutdownException = e;
        }

        assert (shutdownException != null);
    }

    @Test
    public void testGetKeysKeystoreDoesNotExist() throws Exception {
        ShutdownException shutdownException = null;

        try {
            setupMiranda();
            Miranda.getInstance().setPanicPolicy(new LocalPanicPolicy());
            MirandaProperties mirandaProperties = new MirandaProperties();
            mirandaProperties.setProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE, "wrong");
            getStartup().setProperties(mirandaProperties);

            getStartup().getKeys(getStartup().getKeystorePasswordString());
        } catch (ShutdownException e) {
            shutdownException = e;
        }

        assert (shutdownException != null);
    }

    @Test
    public void testGetKeysWrongPassword() throws MirandaException {
        Miranda miranda = new Miranda("-p wrong -t wrong");
        Miranda.setInstance(miranda);

        Miranda.getInstance().setPanicPolicy(getMockPanicPolicy());
        MirandaProperties mirandaProperties = new MirandaProperties();
        Startup startup = (Startup) getMiranda().getCurrentState();
        startup.setProperties(mirandaProperties);

        getMiranda().start();

        pause(100);

        verify(getMockPanicPolicy(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    public static final String TEST_INVALID_KEYSTORE = "FEEDFEED000000020000000200000002000263610000015BC04AFE7D0005582E353039000003643082036030820248A003020102020900E7BAF66A957188F5300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303433303139313235305A170D3138303433303139313235305A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100DCCA8344007EABD37C0CA96F456054029E157D193A3C3BFAE6B2EB5255708A60A513989305BE701C968CF77A99130C352AA08E0D10C80796453B5FED8967B0F11B079FD813A4E34C02A7708DE9F9C0FD132F155AAC773D28C24981700D8585F2612E3E87884A53416A459AAB0C827106281F51643D6705A4FC8C14166765393249189E82BB49EF2159AD4390CA61F1B366BC0752D6906FCCCD61A24B7BAC5AEB9793AF09DBD732FDDA7E9B460161F99DD6481C8507B6773171610E287BDADE4E29BB62D751B0D4935844967BBFD751A0993B6C5C9A6A36634AEFDB65A41ABA5B9C96C0E2581FDF7EA660F2416545BAC35EAF24D79D2DA66D453FB0B042679B4B0203010001A3533051301D0603551D0E0416041490D43024D13A91CFF8FE655B96A7795EB9A8CFB4301F0603551D2304183016801490D43024D13A91CFF8FE655B96A7795EB9A8CFB4300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101006F01C6F04E73588EE1C2222D5D46DE01FA133B33049543C39360F803E6D0AD49D825A106044AEC02B033C52375A1B73CC2C5C8C709A295614232B2B9EB252850623F9E1BAFF7B683DFC4CCF422D6418F1E5D68F9574572A803BF40DB3AD7AFA45EE14573B16C751E342589DADB50C8D91387559F083B9EF7DA5CB01C127C5E0F6212AEC5C406300A4FB59BC133778EC5EC9538C61ACA8A1EE85C8AA755A42997D11B0CF1D3FB84A9E53B831F8AEE4F3FDFA7F7F0A18AE2910172E404307D9B43AA7596A6315BFBF783CD2F8632DC438D1240EAF796CD91EE8B7B76BB465474E648B243705DD0969A2742BF237D1CD0ACD800EF5FACBE877C9FEBA1593921E0F50000000100067365727665720000015BC04B565C00000502308204FE300E060A2B060104012A021101010500048204EA8F8BE92FB0D9E9F39E2CDAD8B20BD64D76374381EC4878FD5A42910DC5670D7D7EBE77954ECB87CFFB21F4064791BBE41A57A78D70EED7AAB01942B8DA10B1BE78A1A241C7A45C01001BED32C6FAAB4A46DDCAEE54E0C73C94360096A9040728597907196251B7820D3B1A67AC153E8906EC7CBF777B5E5DA1AC2C730D129225DEB0081B66F9B68C83569D2EFD8A3C4EEF3A65D1D060FD0619103B171EC12642E06E12CE503735241FF92C9B2897D349F5D85DC978E030BBA300353B17E8ABF0078ED4DB716675A56EFF75BC08B76F81584D1641DB85B49940ED965E67C210C9C4751A7B6C7B46352BDE8189D1CE661291D3E00ED1B110BF1F6CA93B4F387C5B4F9D234B24EDE45F106828DF5E43F4298695F7DF9071893096555CDF0270C84357F2AAD194750ACE5F857C937BA895FA6B85A09AF67030F3DC631AD85A0B7F0923EF1DECF854527E2C085324EFF5A98CCF2CAE2808792178F8B365290420F27250A96037EED76F763683EA33CB4F85F297FFF2AC8A60E289493F70463FEEEBDA8B7790C4B23F27830DA951B283AFD5BBC359E2018C6C250BC7325FECE6BCAE264534188B104C97EE793811DF943D545A8F3F2073122B135B2F87DC46C687FB89A108352E047C54C6A41C357723740867A168F14D852000511F6C12D9B281194339E44E5407AA59EE3527FD8E3A1592E7BD5408AADF1443D5F9A9E745BF1EC8225DD44BC122FAEF456D8AAA800223D75F27140D53FE9AAF5C11EF81B33EA6C38089FBDB5DACC1108CF72B2929167AF3327DBC016D19435D0A13C036DA048E122B1D44822089A90300EE1A1001D5F0F962047F0BF6796BC67D9EA3C004C827558FFE4AC57F09B26D2DD48D7B5358B75084F0648EBA3EBDD762C20586E60CB8AA6DF6474EE140C1B95573DC8ADC3EC4A40B0518FF6D25AB9DDD7F7EB15961863928EA3FD05FF97B61F65AB0A552FA55CBB857BB9B70825C6A4B811D8B79E1DAD30F39BA1053D391F46676F507423229B9229BB32F815BA89DEF54FD8DA0A06A5B9AFBEED3A0B20339D7A587CC3F10F882249EF167CABE431E30CD62DD8BBEC6D810AE65590DCD9312ACC7868F6204688E8BCD278630499E606EC21F7DF4123CD3AA46A1E0EF14DB4106A6077E695A472E14F9466092A2E510F4E5F49AC531F7C43D513362906980B66EF01356DA50259730E3CB46EE1D183142AA81D6745468EF4F4A43861EA9B7035395AC21A92231A41B82CE608C1D2CEF0BFE48392D91A716E5817A81743418210B55F58B55186B3CE46EE8E148D02BC2BE562FDECF06D2487419D8A7BF4DB267F99B64CD25329CAFC397063CC679863CC64F7BAD3AE03DA6CFD6825AE5E34222419882A274EE0CF0C4A4D947C7B4081FF6E874A0D7E232A99504E2E0EA8332A0B9D81362B0E5805395F496D552C23FBAFA50BDCA077850B7EA49ACE6108350AFE3E90D4E35A633CB2DEDBA9E532D2C86EE6E0843829E2661E41BA6C619AB84F2D89E30A6E759DE3E6964BA867BDBBFF732F6151D73AA20E34FDC1B3B6977F13E24C009B2E37FBCBA5BF7DB76A609969786769BAA3EB0AF56CBBE1E480223E63EE221C072D0C66934857E0A5BED85F8F6216B9509B5902CE5866A5D1613BE80DC7B1D2A2E6087A024E7ECB4DE939CDAF7453EDDCF7E3781DBCE3ECEB47C784826453154A8ED9778F7BA9BA4F9930EB440A830B294D94DFB7D6A365AFD525BA53CF299762D7DE99141BA4C6A6AC00B9C9B27DAC1D4A4E046E06AF75615513DB3A667A4A4000000020005582E353039000003313082032D30820215020900EAF78ECEAFD426C6300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303433303139313630355A170D3138303433303139313630355A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100917C1B01D9D15FCD85468BD906DA2C918B1D5160F79A931B9DC884216AE4F42184083F11A3E02DCE4660FCF24ED6EA2976AFD8E572CB823AEF858B4377933D41018901A664A87E63FBFDA15124E14B4531074C152665863CC6267C2D4123499D085BED8161DBBE04CCFDE1E558BA3475B39DD4EF3469832235E319BC0492ED39B45230B05F9D579803DD93D9B5D4F433F6BED741E455681EADD835005FE1FAE94705904160FD51D926B88B6B532AAB8C386D9B736F4EB81F9ECD9DCDECFA03F756147A204017DD5E6F99468B4F50BD95E1C5CBD49B2036E2BB0C4AB526AD12E31964CF4E7EA2966B9637E6AE5A9DEADDEDA7046B76A6CB2C5814A9D9B94B7DAB0203010001300D06092A864886F70D01010B05000382010100402B1B04911B3C4D1FEE7B840CB1CF7FC5E18B0E6FE95B121F23DB5B5019F2D8005ED814F043DDAE3C52AAE762EB2A0A465A44F4F0B0EF456036BB0EEB15ED55EF5D1869DE50702FC876C4E55985C802A52FFE1E1FBA9A7A4675BAD7B2E6D8CE2F3698BDD55065E34CE33A78E8263603E1BA26A05F3B0A1E13024AF5F38C7772923B57D9B4CF2A2FB20C8F6E9A3A9045FB52AC62A42656C022B78627137BFA2025E024936AEE30C34CD1E73BFBAE9A5A0A2AA8E85A1A041DA6C886CC96AA9A452729B76DCBFFA7444A75806BE18428178A5EE5B2278CF4B2AAAB2EE1C4B8AE8B12D0C77906E7BDF05A8E6BCCF45AEEEC464EA36388ACC9A795D7D614F5FE8F5A0005582E353039000003643082036030820248A003020102020900E7BAF66A957188F5300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303433303139313235305A170D3138303433303139313235305A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100DCCA8344007EABD37C0CA96F456054029E157D193A3C3BFAE6B2EB5255708A60A513989305BE701C968CF77A99130C352AA08E0D10C80796453B5FED8967B0F11B079FD813A4E34C02A7708DE9F9C0FD132F155AAC773D28C24981700D8585F2612E3E87884A53416A459AAB0C827106281F51643D6705A4FC8C14166765393249189E82BB49EF2159AD4390CA61F1B366BC0752D6906FCCCD61A24B7BAC5AEB9793AF09DBD732FDDA7E9B460161F99DD6481C8507B6773171610E287BDADE4E29BB62D751B0D4935844967BBFD751A0993B6C5C9A6A36634AEFDB65A41ABA5B9C96C0E2581FDF7EA660F2416545BAC35EAF24D79D2DA66D453FB0B042679B4B0203010001A3533051301D0603551D0E0416041490D43024D13A91CFF8FE655B96A7795EB9A8CFB4301F0603551D2304183016801490D43024D13A91CFF8FE655B96A7795EB9A8CFB4300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101006F01C6F04E73588EE1C2222D5D46DE01FA133B33049543C39360F803E6D0AD49D825A106044AEC02B033C52375A1B73CC2C5C8C709A295614232B2B9EB252850623F9E1BAFF7B683DFC4CCF422D6418F1E5D68F9574572A803BF40DB3AD7AFA45EE14573B16C751E342589DADB50C8D91387559F083B9EF7DA5CB01C127C5E0F6212AEC5C406300A4FB59BC133778EC5EC9538C61ACA8A1EE85C8AA755A42997D11B0CF1D3FB84A9E53B831F8AEE4F3FDFA7F7F0A18AE2910172E404307D9B43AA7596A6315BFBF783CD2F8632DC438D1240EAF796CD91EE8B7B76BB465474E648B243705DD0969A2742BF237D1CD0ACD800EF5FACBE877C9FEBA1593921E0F5B0498249515493987D49A2269B81ADEC2500";

    public static final String TEST_KEYSTORE_PASSWORD = "whatever";

    public static final String TEMP_NAME ="c:/Users/miranda/IdeaProjects/miranda/keystore";
    @Test
    public void testGetKeysOtherException() throws Exception {

        MirandaProperties mirandaProperties = new MirandaProperties();
        String filename = mirandaProperties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);

        createFile(filename, TEST_INVALID_KEYSTORE);

        setupInputStream(TEST_KEYSTORE_PASSWORD);

        com.ltsllc.miranda.ShutdownException shutdownException = null;

        getStartup().setProperties(mirandaProperties);
        try {
            getStartup().getKeys(getStartup().getKeystorePasswordString());
        } catch (com.ltsllc.miranda.ShutdownException e) {
            shutdownException = e;
        }

        assert (shutdownException != null);
    }


    @Test
    public void testGetKeysSuccess() throws Exception {
        setupMiranda();
        MirandaProperties mirandaProperties = new MirandaProperties();
        String filename = mirandaProperties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);

        String hexString = readContents(filename);
        createFile(filename, TEMP_KEY_STORE_CONTENTS);
        setupInputStream(TEST_KEYSTORE_PASSWORD);

        ShutdownException shutdownException = null;

        Miranda.getInstance().setPanicPolicy(getMockPanicPolicy());
        mirandaProperties.setProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS, TEMP_ALIAS);
        getStartup().setProperties(mirandaProperties);
        getStartup().getKeys(TEMP_KEYSTORE_PASSWORD);

        assert (shutdownException == null);
        assert (getStartup().getPublicKey() != null);
        assert (getStartup().getPrivateKey() != null);
    }

}
