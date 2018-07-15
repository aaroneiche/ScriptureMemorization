package cs246.scripturememorization;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class sfHelperTest {

    //This is somewhat pointless, I was just checking the formatting and practicing Unit Tests

    @Test
    public void getReference() {
        Scripture s = new Scripture();
        assertEquals (sfHelper.getReference(s), "1 Nephi 1:1");
    }

    @Test
    public void testTextToList() {
        //this should take text and convert each word into an item in a list
        String text = "one, two, three";
        List<String> list = sfHelper.textToList(text);
        assertEquals(list.get(0), "one, ");
        assertEquals(list.get(1), "two, ");
        assertEquals(list.get(2), "three");
    }
}