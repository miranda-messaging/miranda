/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.common.util;


import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletInputStream;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;


/**
 * Created by Clark on 2/3/2017.
 */
public class Utils {
    public static PrivateKey loadKey(String filename, String passwordString, String alias) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = null;
        FileInputStream fileInputStream = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fileInputStream = new FileInputStream(filename);
            keyStore.load(fileInputStream, passwordString.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(alias, passwordString.toCharArray());
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return privateKey;
    }


    public static X509Certificate loadCertificate(String filename, String passwordString, String alias)
            throws GeneralSecurityException, IOException {
        X509Certificate certificate = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fileInputStream, passwordString.toCharArray());
            certificate = (X509Certificate) keyStore.getCertificate(alias);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return certificate;
    }


    public static KeyStore loadKeyStore(String filename, String passwordString)
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(fileInputStream, passwordString.toCharArray());
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return keyStore;
    }


    public static void closeIgnoreExceptions(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeIgnoreExceptions(Writer writer) {
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeIgnoreExceptions(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(Socket socket) {
        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(Reader r) {
        if (null != r)
            try {
                r.close();
            } catch (IOException e) {

            }
    }

    public static String exceptionToString(Throwable throwable) {
        String result = null;

        if (throwable != null) {
            PrintWriter printWriter = null;
            try {
                StringWriter stringWriter = new StringWriter();
                printWriter = new PrintWriter(stringWriter);
                throwable.printStackTrace(printWriter);
                printWriter.close();
                result = stringWriter.toString();
            } finally {
                closeIgnoreExceptions(printWriter);
            }
        }

        return result;
    }

    public static String closeReturnExceptions(InputStream inputStream) {
        String messages = null;

        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                messages = "Exception closing input stream\n";
                messages += exceptionToString(e);
            }
        }

        return messages;
    }

    public static String closeReturnExceptions(OutputStream outputStream) {
        String messages = null;

        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                messages = "Exception closing output stream\n";
                messages += exceptionToString(e);
            }
        }

        return messages;
    }

    public static String closeReturnExceptions(Socket socket) {
        String messages = null;

        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {
                messages = "Exception trying to close socket";
                messages += exceptionToString(e);
            }
        }

        return messages;
    }

    private static final int BUFFER_SIZE = 8192;

    public static byte[] calculateSha1(FileInputStream fileInputStream)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte buffer[] = new byte[BUFFER_SIZE];
        int bytesRead;

        do {
            bytesRead = fileInputStream.read(buffer);
            messageDigest.update(buffer);
        } while (BUFFER_SIZE == bytesRead);

        return messageDigest.digest();
    }


    private static char[] DIGITS = "0123456789ABCDEF".toCharArray();

    public static String byteToHexString(byte b) {
        StringBuffer sb = new StringBuffer();

        int value = b & 0xf0;
        value = value >> 4;
        sb.append(DIGITS[value]);

        value = b & 0xf;
        sb.append(DIGITS[value]);

        return sb.toString();
    }


    public static String bytesToString(byte[] bytes) {
        StringWriter stringWriter = new StringWriter();

        for (byte b : bytes) {
            stringWriter.append(byteToHexString(b));
        }

        return stringWriter.toString();
    }


    public static String inputStreamToHexString(InputStream inputStream) throws IOException {
        StringWriter stringWriter = new StringWriter();

        int b = inputStream.read();
        while (-1 != b) {
            String stringByte = byteToHexString((byte) b);
            stringWriter.write(stringByte);
            b = inputStream.read();
        }

        stringWriter.close();
        return stringWriter.toString();
    }


    public static String readInputStream(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = null;
        StringWriter stringWriter = null;

        try {
            stringWriter = new StringWriter();
            inputStreamReader = new InputStreamReader(inputStream);

            int c = inputStreamReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = inputStreamReader.read();
            }

            return stringWriter.toString();
        } finally {
            Utils.closeIgnoreExceptions(inputStreamReader);
            Utils.closeIgnoreExceptions(inputStream);
            Utils.closeIgnoreExceptions(stringWriter);
        }
    }


    public static String cipherStreamToString(CipherInputStream cipherInputStream) throws IOException {
        StringWriter stringWriter = new StringWriter();

        int b = cipherInputStream.read();
        while (-1 != b) {
            String stringByte = byteToHexString((byte) b);
            stringWriter.write(stringByte);
            b = cipherInputStream.read();
        }

        stringWriter.close();
        return stringWriter.toString();

    }


    public static byte[] hexStringToBytes(String hexString) throws IOException {
        StringReader reader = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        reader = new StringReader(hexString);

        char[] buffer = new char[2];

        int bytesRead = reader.read(buffer);

        while (-1 != bytesRead) {
            byte b = toByte(buffer);
            byteArrayOutputStream.write(b);
            bytesRead = reader.read(buffer);
        }

        return byteArrayOutputStream.toByteArray();
    }


    public static byte toByte(char[] buffer) {
        int value = toNibble(buffer[0]);
        value = value << 4;
        int temp = 0xF & toNibble(buffer[1]);
        value = value | temp;

        return (byte) value;
    }


    public static int toNibble(char c) {
        if (c >= '0' && c <= '9')
            return (c - '0');
        else if (c >= 'A' && c <= 'F')
            return (c - 'A' + 10);
        else {
            throw new IllegalArgumentException();
        }
    }


    public static String calculateSha1(byte[] buffer) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = null;

        messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(buffer);

        String digest = bytesToString(messageDigest.digest());
        return digest;
    }


    public static String calculateSha1(String s) throws NoSuchAlgorithmException {
        byte[] buffer = s.getBytes();
        return calculateSha1(buffer);
    }


    public static TrustManagerFactory createTrustManagerFactory(String filename, String passwordString)
            throws IOException, GeneralSecurityException {
        FileInputStream fileInputStream = null;
        TrustManagerFactory trustManagerFactory = null;

        try {
            fileInputStream = new FileInputStream(filename);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fileInputStream, passwordString.toCharArray());

            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return trustManagerFactory;
    }

    public static KeyManagerFactory createKeyManagerFactoy(String filename, String password)
            throws IOException, GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = null;

        KeyStore keyStore = loadKeyStore(filename, password);
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());

        return keyManagerFactory;
    }

    public static SSLContext createSocketServerSslContext(String serverFilename, String serverPassword, String serverAlias,
                                                          String trustStoreFilename, String trustStorePassword, String trustStoreAlias)
            throws GeneralSecurityException, IOException {
        PrivateKey key = loadKey(serverFilename, serverPassword, serverAlias);
        X509Certificate certificate = loadCertificate(trustStoreFilename, trustStorePassword, trustStoreAlias);

        SSLContext sslContext = SSLContext.getDefault();
        return sslContext;
    }

    public static String hexStringToString(String hexString) throws IOException {
        byte[] bytes = hexStringToBytes(hexString);
        return new String(bytes);
    }

    public static byte[] toBytes(long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(value);
        return byteBuffer.array();
    }

    public static PublicKey pemStringToPublicKey(String pemString) throws IOException {
        StringReader stringReader = new StringReader(pemString);
        
        PEMParser pemParser = new PEMParser(stringReader);
        SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
        return converter.getPublicKey(subjectPublicKeyInfo);
    }

    public static String publicKeyToPemString(PublicKey publicKey) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PEMWriter pemWriter = new PEMWriter(stringWriter);
        pemWriter.writeObject(publicKey);
        pemWriter.close();
        return stringWriter.toString();
    }

    public static String createPublicKeyPem() throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return publicKeyToPemString(keyPair.getPublic());
    }

    public static void writeAsPem(String filename, PublicKey publicKey) throws IOException {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filename);
            PEMWriter pemWriter = new PEMWriter(fileWriter);
            pemWriter.writeObject(publicKey);
            pemWriter.close();
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    /*
    public static String toPem (PublicKey publicKey, String password) {

    }

    public static void writeAsPem(String filename, PublicKey publicKey, String password) {
        String pemString = toPem(publicKey, password);
        writeTextFile(filename, pemString);
    }
    */

    public static void writeAsPem(String filename, PrivateKey privateKey) throws IOException {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filename);
            PEMWriter pemWriter = new PEMWriter(fileWriter);
            pemWriter.writeObject(privateKey);
            pemWriter.close();
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    public static byte[] rsaEncrypt(PublicKey publicKey, byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText);
    }

    public static void writeAsPem(String filename, java.security.cert.Certificate certificate) throws IOException {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filename);
            PEMWriter pemWriter = new PEMWriter(fileWriter);
            pemWriter.writeObject(certificate);
            pemWriter.close();
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    public static String readAsString(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("the file, " + filename + ", does not exist");
        }

        StringWriter stringWriter = new StringWriter();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);

            int c = fileReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = fileReader.read();
            }
        } finally {
            Utils.closeIgnoreExceptions(fileReader);
        }

        return stringWriter.toString();
    }

    public static PublicKey pemStringToPublicKey(String pemString, String password) throws IOException {
        if (password == null)
            return pemStringToPublicKey(pemString);
        else {
            // String pemStringPlainText = decrypytPem(pemString, password);
            // return pemStringToPublicKey(pemStringPlainText);
            return null;
        }
    }

    public static PublicKey convertPemStringToPublicKey (String pemString, String password) {
        return null;
    }

    public static PublicKey readPublicKeyFromPemFile(String filename) throws IOException {
        String pemFileContents = readAsString(filename);
        StringReader stringReader = new StringReader(pemFileContents);
        PEMParser pemParser = new PEMParser(stringReader);
        SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        converter.setProvider(new BouncyCastleProvider());
        return converter.getPublicKey(subjectPublicKeyInfo);
    }

    public static byte[] readBytes(ServletInputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int b = inputStream.read();
        while (-1 != b) {
            byteArrayOutputStream.write(b);
            b = inputStream.read();
        }

        return byteArrayOutputStream.toByteArray();
    }
/*
    public static X509Certificate sign(PrivateKey caPrivate, PublicKey signeePublic,
                                       X500Name issuer, Date notValidBefore, Date notValidAfter, BigInteger serialNumber,
                                       X500Name subject)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException, IOException,
            OperatorCreationException, CertificateException {

        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
                .find("SHA1withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder()
                .find(sigAlgId);

        AsymmetricKeyParameter foo = PrivateKeyFactory.createKey(caPrivate.getEncoded());
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(signeePublic.getEncoded());

        org.bouncycastle.asn1.x500.X500Name bcIssuer = new org.bouncycastle.asn1.x500.X500Name(issuer.getName());
        org.bouncycastle.asn1.x500.X500Name bcSubject = new org.bouncycastle.asn1.x500.X500Name(subject.getName());
        X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(bcIssuer, serialNumber,
                notValidBefore, notValidAfter, bcSubject, keyInfo);

        ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
                .build(foo);

        X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
        Certificate certificate = holder.toASN1Structure();

        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

        InputStream is1 = new ByteArrayInputStream(certificate.getEncoded());
        X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
        is1.close();
        return theCert;
    }
    */

    public static String toStacktrace (Throwable t) {
        PrintWriter printWriter = null;
        try {
            StringWriter stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
            printWriter.close();
            return stringWriter.toString();
        } finally {
            closeIgnoreExceptions(printWriter);
        }
    }

    public static String readTextFile (String filename) throws IOException {
        StringWriter stringWriter = new StringWriter();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
            int c = fileReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = fileReader.read();
            }
        } finally {
            closeIgnoreExceptions(fileReader);
        }

        return fileReader.toString();
    }

    public static void writeTextFile (String filename, String content) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(content);
        } finally {
            closeIgnoreExceptions(fileWriter);
        }
    }

    public static PublicKey toPublicKey (String pem) throws IOException {
        StringReader stringReader = new StringReader(pem);
        PEMParser parser = new PEMParser(stringReader);
        SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) parser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        converter.setProvider(new BouncyCastleProvider());
        return converter.getPublicKey(subjectPublicKeyInfo);
    }

    public static PrivateKey toPrivateKey (String pem) throws IOException, GeneralSecurityException {
        StringReader stringReader = new StringReader(pem);
        PEMParser parser = new PEMParser(stringReader);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = (PKCS8EncodedKeySpec) parser.readObject();
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(pkcs8EncodedKeySpec);
    }

    public static String toPem (PublicKey publicKey) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PEMWriter pemWriter = new PEMWriter(stringWriter);
        pemWriter.writeObject(publicKey);
        pemWriter.close();
        return stringWriter.toString();
    }

    public static String toPem (PrivateKey privateKey) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PEMWriter pemWriter = new PEMWriter(stringWriter);
        pemWriter.writeObject(privateKey);
        pemWriter.close();
        return stringWriter.toString();
    }
}
