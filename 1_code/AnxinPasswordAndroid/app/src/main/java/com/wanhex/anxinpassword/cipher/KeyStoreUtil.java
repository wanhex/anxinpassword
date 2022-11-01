package com.wanhex.anxinpassword.cipher;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class KeyStoreUtil {
    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String RSA_MODE_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String RSA_MODE_PKCS1 = "RSA/ECB/PKCS1Padding";
    private static final String KEY_ALIAS = "DB_MAIN_PASSWORD";

    public static byte[] encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_MODE_PKCS1);
        KeyStore keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);
        PublicKey publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext.getBytes());
    }

    public static byte[] decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_MODE_PKCS1);
        KeyStore keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, null);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
    }

    public static void createKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore);
        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setKeySize(2048)
                        .build());
        keyPairGenerator.generateKeyPair();
    }
}

