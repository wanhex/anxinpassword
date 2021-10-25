package com.wanhex.anxinpassword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Base64;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.wanhex.anxinpassword.cipher.AESUtil;
import com.wanhex.anxinpassword.cipher.DBPasswordUtil;
import com.wanhex.anxinpassword.cipher.KeyStoreUtil;
import com.wanhex.anxinpassword.cipher.KeyguardVerifyUtil;
import com.wanhex.anxinpassword.cipher.RandomUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CipherInstrumentedTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAESEncrypt() {
        //prepare data
        String testData = "testdata";
        String testPassword = "testpassword";

        //do test
        String testDataEnc = AESUtil.encrypt(testData, testPassword);

        //verify
        assertNotEquals(testData, testDataEnc);
    }

    @Test
    public void testAESDecrypt() {
        //prepare data
        String testData = "testdata";
        String testPassword = "testpassword";

        //do test
        String testDataEnc = AESUtil.encrypt(testData, testPassword);
        String testDataDec = AESUtil.decrypt(testDataEnc, testPassword);

        //verify
        assertEquals(testData, testDataDec);
    }

    @Test
    public void testGetMainDbPassword() {
        //prepare data
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //do test
        String mainDbPassword = new String(DBPasswordUtil.getMainDbPassword(appContext));

        //verify
        assertNotNull(mainDbPassword);
        assertFalse(mainDbPassword.isEmpty());

        //do test
        String mainDbPassword2 = new String(DBPasswordUtil.getMainDbPassword(appContext));
        //verfify
        assertEquals(mainDbPassword, mainDbPassword2);
    }

    @Test
    @Ignore
    public void testKeyStoreWorkCorrectly() throws Exception {
        //prepare data
        String plainText = "plllllllllllllllllllllain";

        //before test
        KeyStoreUtil.createKey();

        //do test
        byte[] dataEncrypted = KeyStoreUtil.encrypt(plainText);
        String dataEncryptedBase64 = Base64.encodeToString(dataEncrypted, Base64.DEFAULT);

        String plainTextDecrypted = new String(KeyStoreUtil.decrypt(dataEncryptedBase64));

        //verfify
        assertEquals(plainText, plainTextDecrypted);
    }

    @Test
    public void testGenRandomPassworCorrectly() {
        //prepare data

        //before test

        //do test
        String randomPassword = RandomUtil.getNumLargeSmallLetter(8);

        //verfify
        assertEquals(8, randomPassword.length());
    }

//    @Test
//    public void testKeyguardVerifyCorrectly() {
//        //prepare data
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//
//        //before test
//
//        //do test
//        KeyguardVerifyUtil.setOnKeyguardVerifiedListener(appContext, new KeyguardVerifyUtil.OnKeyguardVerifiedListener() {
//            @Override
//            public void onKeyguardVerifyResult(boolean keyguardVerified) {
//
//            }
//        });
//
//        //verfify
//    }

}