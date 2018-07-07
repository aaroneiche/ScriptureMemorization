package cs246.scripturememorization;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class sfHelperTest {

    //This is somewhat pointless, I was just checking the formatting and practicing Unit Tests

    @Test
    public void getReference() {
        Scripture s = new Scripture();
        assertEquals (sfHelper.getReference(s), "1 Nephi 1:1");
    }
}