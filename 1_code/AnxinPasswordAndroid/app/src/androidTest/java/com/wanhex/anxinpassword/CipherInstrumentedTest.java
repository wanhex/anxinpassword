package com.wanhex.anxinpassword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.wanhex.anxinpassword.cipher.AESEncrypt;
import com.wanhex.anxinpassword.cipher.CipherUtil;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;
import com.wanhex.anxinpassword.db.PasswordDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
        String testDataEnc = AESEncrypt.encrypt(testData, testPassword);

        //verify
        assertNotEquals(testData, testDataEnc);
    }

    @Test
    public void testAESDecrypt() {
        //prepare data
        String testData = "testdata";
        String testPassword = "testpassword";

        //do test
        String testDataEnc = AESEncrypt.encrypt(testData, testPassword);
        String testDataDec = AESEncrypt.decrypt(testDataEnc, testPassword);

        //verify
        assertEquals(testData, testDataDec);
    }

    @Test
    public void testGetMainDbPassword() {
        //prepare data
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //do test
        String mainDbPassword = new String(CipherUtil.getMainDbPassword(appContext));

        //verify
        assertNotNull(mainDbPassword);
        assertFalse(mainDbPassword.isEmpty());

        //do test
        String mainDbPassword2 = new String(CipherUtil.getMainDbPassword(appContext));
        //verfify
        assertEquals(mainDbPassword, mainDbPassword2);
    }

}