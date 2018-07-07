package cs246.scripturememorization;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.*;


public class StoreDataUnitTest {

    @Test
    public void writeDataTest() {
        String filename = "numbers.txt";
        File file = new File(filename);
        assertTrue(file.canRead());
    }

    @Test
    public void readStoredData(){
        //calls "readData" method to get stored information
        // - converts JSON to Java object

        // testScripture = readData();
        // readData was removed in favor of using SharedPreferences
        // see androidTest/.../SharedPrefsUnitTest

        Scripture testScripture = new Scripture();

        assertEquals("Book of Mormon",testScripture.volume);
        assertEquals("1 Nephi", testScripture.book);
        assertEquals("1", testScripture.chapter);
        assertEquals("1", testScripture.verse);
        assertEquals("I Nephi, having been born of goodly parents", testScripture.text);

    }
}
