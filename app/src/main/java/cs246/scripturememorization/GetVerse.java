package cs246.scripturememorization;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

/* GetVerse is a list activity. It will query the database for all of the verses in the previously
selected chapter. All of those verses will fill a list that is then used to populate
the list view. When a verse is clicked, it's value is added to the scripture path to be passed
by intent and the user is advanced to the next selection phase.
*/

public class GetVerse extends ListActivity {
    //immutable strings used to construct the queries and normal strings to be defined later for the intent
    private static final String TABLE_NAME = "verses";
    private static final String CHAPTER_SELECTOR = "chapter_id = ";
    private static final String VERSE_NUMBER = "verse_number";
    private static String VOLUME_TITLE;
    private static String BOOK_TITLE;
    private static int CHAPTER_TITLE;
    private static int VOLUME_ID;
    private static int BOOK_ID;
    private static int CHAPTER_ID;
    private static int VERSE_TITLE;
    private static int VERSE_ID;
    private static String VERSE_TEXT;

    //the list view, the verses arraylist, the verseIDs arraylist, and the verseTexts arraylist.
    private ListView listView;
    private ArrayList<String> verses;
    private ArrayList<Integer> verseIDs;
    private ArrayList<String> verseTexts;

    private Scripture s;

    //connect to the databse
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_volume);

        Intent intent = getIntent();
        VOLUME_ID = intent.getExtras().getInt("volumeID");
        VOLUME_TITLE = intent.getExtras().getString("volumeTitle");

        BOOK_ID = intent.getExtras().getInt("bookID");
        BOOK_TITLE = intent.getExtras().getString("bookTitle");

        CHAPTER_ID = intent.getExtras().getInt("chapterID");
        CHAPTER_TITLE = intent.getExtras().getInt("chapterTitle") + 1;

        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        /*fill the chapters arraylist and populate the list view with it.
        Fill the verseIDs and verseTexts arraylists too. */
        fillVerses();
        fillVerseIDs();
        fillVerseTexts();
        setUpList();
    }

    //Populate the list with the verses arraylist
    private void setUpList() {
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, verses));
        listView = getListView();

        /*when a list view item is clicked, the volume title, volume id, the book id, the book title,
        the chapter title, the chapter id, the verse name (number), the verse id, and the verse text
        are all put into the intent and then we go back to the main activity!*/
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                VERSE_TITLE = position + 1;
                VERSE_ID = verseIDs.get(position);
                VERSE_TEXT = verseTexts.get(position);
                Log.d("The scripture path is", "" + VOLUME_TITLE + " " + BOOK_TITLE + " " + CHAPTER_TITLE + " " +  VERSE_TITLE + " and the verse ID is " + VERSE_ID + " and the verse text is " + VERSE_TEXT);

                s = new Scripture(VOLUME_TITLE, BOOK_TITLE, CHAPTER_TITLE, VERSE_TITLE, VERSE_ID, VERSE_TEXT);

                intent.putExtra("Scripture", s);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
    }

    //the database is queried and will return all of the verse names (numbers). Each will be added to the arraylist.
    private void fillVerses() {
        verses = new ArrayList<String>();
        Cursor verseCursor = mDb.query(TABLE_NAME, new String[] {VERSE_NUMBER}, CHAPTER_SELECTOR + CHAPTER_ID, null, null, null, "id");
        verseCursor.moveToFirst();
        if(!verseCursor.isAfterLast()) {
            do {
                String name = verseCursor.getString(0);
                verses.add(name);
            } while (verseCursor.moveToNext());
        }
        verseCursor.close();
    }

    //the database is queried and will return all of the verse ids. Each will be added to the arraylist.
    private void fillVerseIDs() {
        verseIDs = new ArrayList<Integer>();
        Cursor verseIDCursor = mDb.query(TABLE_NAME, new String[] {"id"}, CHAPTER_SELECTOR + CHAPTER_ID, null, null, null, "id");
        verseIDCursor.moveToFirst();
        if(!verseIDCursor.isAfterLast()) {
            do {
                Integer id = verseIDCursor.getInt(0);
                verseIDs.add(id);
            } while (verseIDCursor.moveToNext());
        }
        verseIDCursor.close();
    }

    //the database is queried and will return all of the verse text values. Each will be added to the arraylist.
    private void fillVerseTexts() {
        verseTexts = new ArrayList<String>();
        Cursor verseTextCursor = mDb.query(TABLE_NAME, new String[] {"scripture_text"}, CHAPTER_SELECTOR + CHAPTER_ID, null, null, null, "id");
        verseTextCursor.moveToFirst();
        if(!verseTextCursor.isAfterLast()) {
            do {
                String text = verseTextCursor.getString(0);
                verseTexts.add(text);
            } while (verseTextCursor.moveToNext());
        }
        verseTextCursor.close();
    }

}
