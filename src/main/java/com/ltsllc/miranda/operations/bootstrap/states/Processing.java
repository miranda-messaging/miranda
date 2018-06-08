package com.ltsllc.miranda.operations.bootstrap.states;

import com.ltsllc.clcl.*;
import com.ltsllc.commons.io.Util;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.bootstrap.BootstrapOperation;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.bootstrap.BootstrapMessage;
import com.ltsllc.miranda.user.messages.BootstrapResponseMessage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class Processing extends State {
    private static class ReturnValue {
        public PrivateKey truststorePrivateKey;
        public Certificate truststoreCertificate;
    }

    private BootstrapOperation operation;
    private BlockingQueue<Message> requester;
    private BootstrapMessage bootstrapMessage;

    public BootstrapMessage getBootstrapMessage() {
        return bootstrapMessage;
    }

    public void setBootstrapMessage(BootstrapMessage bootstrapMessage) {
        this.bootstrapMessage = bootstrapMessage;
    }

    public void setRequester(BlockingQueue<Message> requester) {
        this.requester = requester;
    }

    public BootstrapOperation getOperation() {
        return operation;
    }

    public void setOperation(BootstrapOperation operation) {
        this.operation = operation;
    }

    public Processing(BootstrapOperation operation, BootstrapMessage bootstrapMessage) {
        this.operation = operation;
        this.requester = bootstrapMessage.getSender();
        setBootstrapMessage(bootstrapMessage);
    }

    @Override
    public State start() {
        try {
            Miranda.getInstance().getUserManager().sendBootstrap(getOperation().getQueue(), this,
                    getBootstrapMessage().getAdminDistinguishedName(), getBootstrapMessage().getAdminPassword());
            ReturnValue returnValue = bootstrapTruststore(
                    Miranda.properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME),
                    getBootstrapMessage().getCaDistinguishedName(),
                    getBootstrapMessage().getCaPassword());
            String pem = returnValue.truststoreCertificate.toPem();
            Util.writeTextFile(Miranda.properties.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_FILE), pem);
            bootstrapKeystore(Miranda.properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE),
                    returnValue.truststorePrivateKey, returnValue.truststoreCertificate,
                    getBootstrapMessage().getNodeDistinguishedName(),
                    getBootstrapMessage().getNodePassword());
            BootstrapResponseMessage bootstrapResponseMessage = new BootstrapResponseMessage(getOperation().getQueue(),
                    this,Results.Success);
            getBootstrapMessage().reply(bootstrapResponseMessage);
        } catch (MirandaException|IOException e) {
            Panic panic = new Panic("Exception trying to bootssrap system", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
        }
        return this;
    }


    public ReturnValue bootstrapTruststore(String filename, DistinguishedName caDistinguishedName,
    String caPassword) throws MirandaException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            java.security.KeyPair jsKeyPair = keyPairGenerator.generateKeyPair();
            KeyPair keyPair = new KeyPair(jsKeyPair);
            PublicKey truststorePublicKey = keyPair.getPublicKey();
            PrivateKey truststorePrivateKey = keyPair.getPrivateKey();
            truststorePrivateKey.setDn(caDistinguishedName);
            truststorePublicKey.setDn(caDistinguishedName);
            CertificateSigningRequest certificateSigningRequest =
                    truststorePublicKey.createCertificateSigningRequest(truststorePrivateKey);
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.YEAR, 10);
            Date tenYearsFromNow = calendar.getTime();
            Certificate certificate = truststorePrivateKey.sign(certificateSigningRequest, now, tenYearsFromNow);
            JavaKeyStore javaKeyStore = new JavaKeyStore();
            javaKeyStore.add("ca", keyPair, null);
            javaKeyStore.add("caCertificate", certificate);
            javaKeyStore.setFilename(filename);
            javaKeyStore.setPasswordString(caPassword);
            javaKeyStore.store();
            ReturnValue returnValue = new ReturnValue();
            returnValue.truststorePrivateKey = truststorePrivateKey;
            returnValue.truststoreCertificate = certificate;
            return returnValue;
        } catch (GeneralSecurityException e) {
            MirandaException mirandaException = new MirandaException("Exception creating keys", e);
            throw mirandaException;
        } catch (EncryptionException e) {
            MirandaException mirandaException = new MirandaException("Exception creating truststore", e);
            throw mirandaException;
        }
    }

    public void bootstrapKeystore(String filename, PrivateKey privateKey, Certificate truststoreCertificate,
                                  DistinguishedName distinguishedName, String nodePassword) throws MirandaException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            java.security.KeyPair jsKeyPair = keyPairGenerator.generateKeyPair();
            KeyPair keyPair = new KeyPair(jsKeyPair);
            PublicKey publicKey = keyPair.getPublicKey();
            PrivateKey privateKey1 = keyPair.getPrivateKey();
            publicKey.setDn(distinguishedName);
            privateKey.setDn(distinguishedName);
            CertificateSigningRequest certificateSigningRequest = publicKey.createCertificateSigningRequest(privateKey1);
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.YEAR, 10);
            Date tenYearsFromNow = calendar.getTime();
            Certificate certificate = privateKey.sign(certificateSigningRequest, now, tenYearsFromNow);
            JavaKeyStore javaKeyStore = new JavaKeyStore();
            javaKeyStore.setPasswordString(nodePassword);
            javaKeyStore.setFilename(filename);
            Certificate[] chain = {certificate};
            javaKeyStore.add("node", keyPair, chain);
            javaKeyStore.store();
        } catch (GeneralSecurityException | EncryptionException e) {
            MirandaException mirandaException = new MirandaException("Exception trying to bootstrap keystore", e);
            throw mirandaException;
        }
    }
}
