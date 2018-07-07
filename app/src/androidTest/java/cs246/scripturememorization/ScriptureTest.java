package cs246.scripturememorization;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class ScriptureTest {

    @Test
    public void writeToParcel() {
        Scripture scripture = new Scripture();
        assertEquals(scripture.volume, "Book of Mormon");
        assertEquals(scripture.book, "1 Nephi");
        assertEquals(scripture.chapter, 1);
        assertEquals(scripture.verse, 1);
        assertEquals(scripture.verseID, 31103);
        assertEquals(scripture.text, "I, Nephi having been born of goodly parents, therefore I was taught somewhat in all the learning of my father; and having seen many afflictions in the course of my days, nevertheless, having been highly favored of the Lord in all my days; yea, having had a great knowledge of the goodness and the mysteries of God, therefore I make a record of my proceedings in my days.");
        assertEquals(scripture.memorized, false);
        assertEquals(scripture.dateMemorized, null);
        assertEquals(scripture.lastReviewed, null);
        assertEquals(scripture.percentCorrect, 0);

        Parcel parcel = Parcel.obtain();
        scripture.writeToParcel(parcel, scripture.describeContents());
        parcel.setDataPosition(0);

        Scripture fromParcel = (Scripture) Scripture.CREATOR.createFromParcel(parcel);
        assertEquals(fromParcel.volume, "Book of Mormon");
        assertEquals(fromParcel.book, "1 Nephi");
        assertEquals(fromParcel.chapter, 1);
        assertEquals(fromParcel.verse, 1);
        assertEquals(fromParcel.verseID, 31103);
        assertEquals(fromParcel.text, "I, Nephi having been born of goodly parents, therefore I was taught somewhat in all the learning of my father; and having seen many afflictions in the course of my days, nevertheless, having been highly favored of the Lord in all my days; yea, having had a great knowledge of the goodness and the mysteries of God, therefore I make a record of my proceedings in my days.");
        assertEquals(fromParcel.memorized, false);
        assertEquals(fromParcel.dateMemorized, null);
        assertEquals(fromParcel.lastReviewed, null);
        assertEquals(fromParcel.percentCorrect, 0);
    }
}