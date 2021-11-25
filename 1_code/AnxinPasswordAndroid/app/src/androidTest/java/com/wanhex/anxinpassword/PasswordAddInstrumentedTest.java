package com.wanhex.anxinpassword;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.EspressoKey;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.wanhex.anxinpassword.clouddisk.BaiduOAuthActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PasswordAddInstrumentedTest {

    private Context mAppContext;

    @Before
    public void setUp() {
        mAppContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddPasswordWithFullField() {
        //prepare
        onView(withId(R.id.add_password)).perform(click());

        //do test
        onView(withId(R.id.et_title))
                .perform(replaceText("testaccount0"), pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.et_username))
                .perform(replaceText("TestUser0"));
        onView(withId(R.id.et_passwd))
                .perform(replaceText("TestPassword0"));
        onView(withId(R.id.et_comments))
                .perform(replaceText("testcomments"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withId(R.id.password_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.et_title))
                .check(matches(withText("testaccount0")));
        onView(withId(R.id.et_username))
                .check(matches(withText("TestUser0")));
        onView(withId(R.id.et_passwd))
                .check(matches(withText("TestPassword0")));
        onView(withId(R.id.et_comments))
                .check(matches(withText("testcomments")));

    }

    @Test
    public void testAddPasswordWithoutComment() {
        //prepare
        onView(withId(R.id.add_password)).perform(click());

        //do test
        onView(withId(R.id.et_title))
                .perform(replaceText("testaccountt"), pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.et_username))
                .perform(replaceText("TestUser1"));
        onView(withId(R.id.et_passwd))
                .perform(replaceText("TestPassword1"));

        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withId(R.id.password_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.et_title))
                .check(matches(withText("testaccountt")));
        onView(withId(R.id.et_username))
                .check(matches(withText("TestUser1")));
        onView(withId(R.id.et_passwd))
                .check(matches(withText("TestPassword1")));

    }


    @Test
    public void testAddPasswordWithEmptyTitle() {
        //prepare
        onView(withId(R.id.add_password)).perform(click());

        //do test
        onView(withId(R.id.et_username))
                .perform(replaceText("TestUser1"));
        onView(withId(R.id.et_passwd))
                .perform(replaceText("TestPassword1"));

        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withText("标题不能为空")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));

    }

    @Test
    public void testAddPasswordWithEmptyUserName() {
        //prepare
        onView(withId(R.id.add_password)).perform(click());

        //do test
        onView(withId(R.id.et_title))
                .perform(replaceText("TestTitle"));
        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withText("用户名不能为空")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));

    }

    @Test
    public void testAddPasswordWithEmptyPassword() {
        //prepare
        onView(withId(R.id.add_password)).perform(click());

        //do test
        onView(withId(R.id.et_title))
                .perform(replaceText("TestTitle"));
        onView(withId(R.id.et_username))
                .perform(replaceText("TestPassword"));
        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withText("密码不能为空")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));

    }
}