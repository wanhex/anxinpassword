package com.wanhex.anxinpassword;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.IsNot.not;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PasswordDeleteInstrumentedTest {

    private Context mAppContext;

    @Before
    public void setUp() {
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

        onView(withId(R.id.add_password)).perform(click());
        onView(withId(R.id.et_title))
                .perform(replaceText("testaccountx"), pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.et_username))
                .perform(replaceText("TestUserx"));
        onView(withId(R.id.et_passwd))
                .perform(replaceText("TestPasswordx"));
        onView(withId(R.id.et_comments))
                .perform(replaceText("testcommentsx"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.save_password)).perform(click());

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPasswordDeleteOk() {

        //need close big bang in smartisan os.
        onView(withId(R.id.password_list)).perform(waitForMillis(500), actionOnItemAtPosition(0, longClick()));

        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.password_list)).perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.et_title))
                .check(matches(withText(not("testaccountx"))));
    }

    private ViewAction waitForMillis(long millis) {

        return  new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Wait for $millis milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}