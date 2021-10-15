package com.wanhex.anxinpassword;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

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
public class DbInstrumentedTest {

    AppDatabase appDatabase;
    PasswordDao passwordDao;

    @Before
    public void createDb() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appDatabase = Room.databaseBuilder(appContext, AppDatabase.class, "app.db").build();
        passwordDao = appDatabase.passwordDao();

        passwordDao.deleteAll();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void testDbIsEmpty() {
        List<Password> passwordList = passwordDao.getAll();
        assertEquals(0, passwordList.size());
    }

    @Test
    public void testAddOnePasswordCorrectly() {
        Password password = new Password("testsite", "testuser", "testpassword", "testcomments");

        passwordDao.insert(password);

        List<Password> passwordList = passwordDao.getAll();

        assertEquals(1, passwordList.size());
        assertEquals(password.site, passwordList.get(0).site);
        assertEquals(password.username, passwordList.get(0).username);
        assertEquals(password.password, passwordList.get(0).password);
        assertEquals(password.comments, passwordList.get(0).comments);
    }

    @Test
    public void testAddTwoPasswordCorrectly() {
        Password password = new Password("testsite", "testuser", "testpassword", "testcomments");
        Password password2 = new Password("testsite2", "testuser2", "testpassword2", "testcomments2");

        passwordDao.insert(password);
        passwordDao.insert(password2);

        List<Password> passwordList = passwordDao.getAll();

        assertEquals(2, passwordList.size());
        assertEquals(password.site, passwordList.get(0).site);
        assertEquals(password2.site, passwordList.get(1).site);
    }
}