package com.wanhex.anxinpassword;

import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.web.webdriver.DriverAtoms;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.wanhex.anxinpassword.cipher.AESUtil;
import com.wanhex.anxinpassword.cipher.DBPasswordUtil;
import com.wanhex.anxinpassword.cipher.KeyStoreUtil;
import com.wanhex.anxinpassword.cipher.RandomUtil;
import com.wanhex.anxinpassword.clouddisk.BaiduNetDiskSettings;
import com.wanhex.anxinpassword.clouddisk.BaiduOAuthActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BaiduYunInstrumentedTest {

    private Context mAppContext;

    @Before
    public void setUp() {
        mAppContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetGetSwitch() {
        //prepare data

        //do test
        BaiduNetDiskSettings.setSyncSwitch(mAppContext, true);
        boolean switchStatus = BaiduNetDiskSettings.getSyncSwitch(mAppContext, false);

        //verify
        assertTrue(switchStatus);
    }

    @Test
    public void testSetGetBaiduName() {
        //prepare data
        String testBaiduName = "wanhex";

        //do test
        BaiduNetDiskSettings.setBaiduName(mAppContext, testBaiduName);
        String baiduName = BaiduNetDiskSettings.getBaiduName(mAppContext);

        //verify
        assertEquals(testBaiduName, baiduName);
    }

    @Test
    public void testSetGetBaiduAccessToken() {
        //prepare data
        String testBaiduAccessToken = "=====================================";

        //do test
        BaiduNetDiskSettings.setAccessToken(mAppContext, testBaiduAccessToken);
        String baiduAccessToken = BaiduNetDiskSettings.getAccessToken(mAppContext);

        //verify
        assertEquals(testBaiduAccessToken, baiduAccessToken);

        //tearDown
        BaiduNetDiskSettings.setAccessToken(mAppContext, "");
    }

    @Test
    public void testLoginBaiduAccountWithPasswordWrong() {

        //prepare test data
//        String baiduName = "hongxiangwan";
//        String baiduPassword = "xxxxxxxxxxxxxx";
//
//        String userNameViewId = "PASSP__1__username";
//        String userPasswordViewId = "PASSP__1__password";
//        String submitViewId = "PASSP__1__submit";
        String errorMsgViewId = "PASSP__1__msgWrapper";

        ActivityScenario.launch(BaiduOAuthActivity.class);

        onWebView().forceJavascriptEnabled();
//        onWebView().withElement(findElement(Locator.ID, userNameViewId))
//                .perform(DriverAtoms.webKeys(baiduName));
//        onWebView().withElement(findElement(Locator.ID, userPasswordViewId))
//                .perform(DriverAtoms.webKeys(baiduPassword));
//        onWebView().withElement(findElement(Locator.ID, submitViewId))
//                .perform(DriverAtoms.webClick());

        String ttt = onWebView().withElement(findElement(Locator.ID, errorMsgViewId))
                .perform(getText()).get();

        int count = 0;
        while(ttt.isEmpty() || ttt.contains("验证码")) {
            ttt = onWebView().withElement(findElement(Locator.ID, errorMsgViewId))
                    .perform(getText()).get();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            if (count >= 20) {
                break;
            }
        }

        onWebView().withElement(findElement(Locator.ID, errorMsgViewId))
                // Verify that the response page contains the entered text
                .check(webMatches(getText(), containsString("帐号或密码错")));

    }

    @Test
    @Ignore
    public void testLoginBaiduAccountWithPassword() {

        //prepare test data

        String errorMsgViewId = "PASSP__1__msgWrapper";

        ActivityScenario.launch(BaiduOAuthActivity.class);

        onWebView().forceJavascriptEnabled();
//        onWebView().withElement(findElement(Locator.ID, userNameViewId))
//                .perform(DriverAtoms.webKeys(baiduName));
//        onWebView().withElement(findElement(Locator.ID, userPasswordViewId))
//                .perform(DriverAtoms.webKeys(baiduPassword));
//        onWebView().withElement(findElement(Locator.ID, submitViewId))
//                .perform(DriverAtoms.webClick());

        onWebView().withElement(findElement(Locator.XPATH, "h1[contains(text(),'OAuth2.0')]"))
                .perform(getText()).get();

        String accessToken = BaiduNetDiskSettings.getAccessToken(mAppContext);;

        assertTrue(null != accessToken && accessToken.isEmpty());


    }
}