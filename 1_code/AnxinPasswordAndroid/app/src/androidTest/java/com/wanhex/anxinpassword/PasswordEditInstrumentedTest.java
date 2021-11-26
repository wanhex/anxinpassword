package com.wanhex.anxinpassword;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.KeyEvent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PasswordEditInstrumentedTest {

    private Context mAppContext;

    @Before
    public void setUp() {
        MyApp.getPasswordDb().passwordDao().deleteAll();

        mAppContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ActivityScenario.launch(MainActivity.class);

        //prepare
        onView(withId(R.id.add_password)).perform(click());

        //add new password
        onView(withId(R.id.et_title))
                .perform(replaceText("testaccount0"), pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.et_username))
                .perform(replaceText("TestUser0"));
        onView(withId(R.id.et_passwd))
                .perform(replaceText("TestPassword0"));
        onView(withId(R.id.et_comments))
                .perform(replaceText("testcomments0"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());

        //start PasswordEditActivity
        onView(withId(R.id.password_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @After
    public void tearDown() {
        MyApp.getPasswordDb().passwordDao().deleteAll();
    }

    @Test
    public void testPasswordEditWithRightDataInitially() {
        onView(withId(R.id.et_title))
                .check(matches(withText("testaccount0")));
        onView(withId(R.id.et_username))
                .check(matches(withText("TestUser0")));
        onView(withId(R.id.et_passwd))
                .check(matches(withText("TestPassword0")));
        onView(withId(R.id.et_comments))
                .check(matches(withText("testcomments0")));
    }

    @Test
    public void testPasswordEditWithEmptyTitle() {

        //press edit button
        onView(withId(R.id.edit_password)).perform(click());

        //do test, clear title
        onView(withId(R.id.et_title))
                .perform(replaceText(""), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withText("标题不能为空")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordEditWithEmptyUserName() {

        //press edit button
        onView(withId(R.id.edit_password)).perform(click());

        //do test, clear username
        onView(withId(R.id.et_username))
                .perform(replaceText(""), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withText("用户名不能为空")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordEditWithEmptyPassword() {
        //press edit button
        onView(withId(R.id.edit_password)).perform(click());

        //do test, clear password
        onView(withId(R.id.et_passwd))
                .perform(replaceText(""), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());

        //verify
        onView(withText("密码不能为空")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordEditWithFullField() {
        //press edit button
        onView(withId(R.id.edit_password)).perform(click());

        //do test, replace all field
        onView(withId(R.id.et_title))
                .perform(replaceText("testaccount1"), pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.et_username))
                .perform(replaceText("TestUser1"));
        onView(withId(R.id.et_passwd))
                .perform(replaceText("TestPassword1"));
        onView(withId(R.id.et_comments))
                .perform(replaceText("testcomments1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());
        //verify
        onView(withId(R.id.password_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.et_title))
                .check(matches(withText("testaccount1")));
        onView(withId(R.id.et_username))
                .check(matches(withText("TestUser1")));
        onView(withId(R.id.et_passwd))
                .check(matches(withText("TestPassword1")));
        onView(withId(R.id.et_comments))
                .check(matches(withText("testcomments1")));
    }
}