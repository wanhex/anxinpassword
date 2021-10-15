package com.wanhex.anxinpassword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testAddNPasswordCorrectly() {
        Password password = new Password("testsite", "testuser", "testpassword", "testcomments");
        Password password2 = new Password("testsite2", "testuser2", "testpassword2", "testcomments2");
        Password password3 = new Password("testsite3", "testuser3", "testpassword3", "testcomments3");
        Password password4 = new Password("testsite4", "testuser4", "testpassword4", "testcomments4");

        passwordDao.insert(password);
        passwordDao.insert(password2);
        passwordDao.insert(password3);
        passwordDao.insert(password4);

        List<Password> passwordList = passwordDao.getAll();

        assertEquals(4, passwordList.size());
        assertEquals(password.site, passwordList.get(0).site);
        assertEquals(password2.site, passwordList.get(1).site);
        assertEquals(password3.site, passwordList.get(2).site);
        assertEquals(password4.site, passwordList.get(3).site);
    }

    @Test
    public void testDelPasswordCorrectly() {
        //prepare data
        Password password = new Password("testsite", "testuser", "testpassword", "testcomments");
        Password password2 = new Password("testsite2", "testuser2", "testpassword2", "testcomments2");
        Password password3 = new Password("testsite3", "testuser3", "testpassword3", "testcomments3");
        Password password4 = new Password("testsite4", "testuser4", "testpassword4", "testcomments4");
        passwordDao.insert(password);
        passwordDao.insert(password2);
        passwordDao.insert(password3);
        passwordDao.insert(password4);

        List<Password> passwordList = passwordDao.getAll();
        assertEquals(4, passwordList.size());

        Password passwordToDel = passwordList.get(2);

        //do test
        passwordDao.delete(passwordToDel);

        //verify
        passwordList = passwordDao.getAll();
        assertEquals(3, passwordList.size());
        assertNotEquals(passwordToDel.site, passwordList.get(0).site);
        assertNotEquals(passwordToDel.site, passwordList.get(1).site);
        assertNotEquals(passwordToDel.site, passwordList.get(2).site);
    }

    @Test
    public void testUpdatePasswordCorrectly() {
        //prepare data
        Password password = new Password("testsite", "testuser", "testpassword", "testcomments");
        Password password2 = new Password("testsite2", "testuser2", "testpassword2", "testcomments2");
        Password password3 = new Password("testsite3", "testuser3", "testpassword3", "testcomments3");
        Password password4 = new Password("testsite4", "testuser4", "testpassword4", "testcomments4");
        passwordDao.insert(password);
        passwordDao.insert(password2);
        passwordDao.insert(password3);
        passwordDao.insert(password4);

        List<Password> passwordList = passwordDao.getAll();
        assertEquals(4, passwordList.size());

        Password passwordToUpdate = passwordList.get(2);
        passwordToUpdate.password = "testpassword_update";

        //do test
        passwordDao.update(passwordToUpdate);

        //verify
        passwordList = passwordDao.getAll();
        assertEquals(4, passwordList.size());

        Password passwordHasUpdated = passwordDao.findBySite(passwordToUpdate.site).get(0);
        assertEquals(passwordToUpdate.site, passwordHasUpdated.site);
    }

    @Test
    public void testGetAllCorrectly() {
        //prepare data
        Password password = new Password("testsite", "testuser", "testpassword", "testcomments");
        Password password2 = new Password("testsite2", "testuser2", "testpassword2", "testcomments2");
        Password password3 = new Password("testsite3", "testuser3", "testpassword3", "testcomments3");
        Password password4 = new Password("testsite4", "testuser4", "testpassword4", "testcomments4");
        passwordDao.insert(password);
        passwordDao.insert(password2);
        passwordDao.insert(password3);
        passwordDao.insert(password4);

        //do test
        List<Password> passwordList = passwordDao.getAll();

        //verify
        assertEquals(4, passwordList.size());

        boolean isDescByTimestamp = false;
        if (passwordList.get(0).timeStamp >= passwordList.get(1).timeStamp
        && passwordList.get(1).timeStamp >= passwordList.get(2).timeStamp
        && passwordList.get(2).timeStamp >= passwordList.get(3).timeStamp) {
            isDescByTimestamp = true;
        }

        assertTrue(isDescByTimestamp);

    }
}