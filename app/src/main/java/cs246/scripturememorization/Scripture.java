package cs246.scripturememorization;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * This class holds a scripture, it's reference and it's verse id for looking it up in our database.
 * It has been set-up to use the parcelable interface to be passed easily from activity to activity.
 */
public class Scripture implements Parcelable{
    public String volume;       /** Old Testiment, new Testiment, etc... **/
    public String book;         /** Genesis, Exodus, etc... **/
    public int chapter;
    public int verse;
    public int verseID;         /** unique ID for scripture in database **/
    public String text;
    public boolean memorized;
    Date dateMemorized;
    Date lastReviewed;
    int percentCorrect;

    /**
     * Parcelable constructor - used to rebuild the scripture after being passed as an intent.
     * @param in
     */

    public Scripture(Parcel in) {
        volume = in.readString();
        book = in.readString();
        chapter = in.readInt();
        verse = in.readInt();
        verseID = in.readInt();
        text = in.readString();
        memorized = in.readInt() == 1;
        percentCorrect = in.readInt();
        String temp = in.readString();
        if (temp.equals("null")) {
            dateMemorized = null;
        }
        else {
            dateMemorized = new Date(temp);
        }
        temp = in.readString();
        if (temp.equals("null")) {
            lastReviewed = null;
        }
        else {
            lastReviewed = new Date(temp);
        }
    }

    /**
     * Defaults to 1 Nephi 1:1
     */
    public Scripture() {
        volume = "Book of Mormon";
        book = "1 Nephi";
        chapter = 1;
        verse = 1;
        verseID = 31103;
        text = "I, Nephi having been born of goodly parents, therefore I was taught somewhat in all the learning of my father; and having seen many afflictions in the course of my days, nevertheless, having been highly favored of the Lord in all my days; yea, having had a great knowledge of the goodness and the mysteries of God, therefore I make a record of my proceedings in my days.";
        memorized = false;
        dateMemorized = null;
        lastReviewed = null;
        percentCorrect = 0;
    }

    /**
     * Non-default constructor, for testing different scriptures.
     * @param volume
     * @param book
     * @param chapter
     * @param verse
     * @param text
     */
    public Scripture(String volume, String book, int chapter, int verse, int verseID, String text) {
        this.volume = volume;
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
        this.verseID = verseID;
        this.text = text;
        memorized = false;
        dateMemorized = null;
        lastReviewed = null;
        percentCorrect = 0;
    }

    @Override
    public String toString() {
        return(String.format("%s %s:%s passed:%b", book, chapter, verse, memorized));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Scripture createFromParcel(Parcel source) {
            return new Scripture(source);
        }

        public Scripture[] newArray(int size) {
            return new Scripture[size];
        }
    };

    /**
     * Packs the scripture up to be passed in an intent.
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(volume);
        dest.writeString(book);
        dest.writeInt(chapter);
        dest.writeInt(verse);
        dest.writeInt(verseID);
        dest.writeString(text);
        if (memorized) {
            dest.writeInt(1);
        }
        else {
            dest.writeInt(0);
        }
        dest.writeInt(percentCorrect);
        if (dateMemorized != null) {
            dest.writeString(dateMemorized.toString());
        }
        else {
            dest.writeString("null");
        }

        if (lastReviewed != null) {
            dest.writeString(lastReviewed.toString());
        }
        else {
            dest.writeString("null");
        }
    }
}
