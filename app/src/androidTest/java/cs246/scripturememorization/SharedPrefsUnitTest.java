package cs246.scripturememorization;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.app.Instrumentation;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class SharedPrefsUnitTest extends InstrumentationTestCase{
    /*
    Tests read and write of shared preferences storing a scripture as a json string
     */
    String testJson;
    Context context;
    Instrumentation instr;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        instr = getInstrumentation();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        context = InstrumentationRegistry.getContext();
    }

    /*
    Tests writing to shared preferences, needs to be done before reading
    */
    @Before
    public void writeDataTest() {
        context = InstrumentationRegistry.getContext();
        Scripture s = new Scripture();
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        testJson = gson.toJson(s);
        assertNotEquals(testJson, null);
        editor.putString("test", testJson);
        editor.apply();
    }
    /*
    Tests reading from shared preferences
    */
    @Test
    public void readStoredData() {
        //uses shared preferences to get shared preferences
        // - converts JSON to Java object
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        assertEquals(testJson, pref.getString("test", null));
    }

    /*
    Tests getting the scripture from shared preferences
     */
    @Test
    public void getStoredScripture() {
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Scripture testScripture = gson.fromJson(pref.getString("test", null), Scripture.class);

        assertNotEquals(null, testScripture);

        assertEquals("Book of Mormon", testScripture.volume);
        assertEquals("1 Nephi", testScripture.book);
        assertEquals(1, testScripture.chapter);
        assertEquals(1, testScripture.verse);
        assertEquals(null, testScripture.dateMemorized);
    }
}
