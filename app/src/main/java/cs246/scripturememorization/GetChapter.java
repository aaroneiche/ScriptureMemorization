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

/* GetChapter is a list activity. It will query the database for all of the chapters in the previously
selected book. All of those chapters will fill a list that is then used to populate
the list view. When a chapter is clicked, it's value is added to the scripture path to be passed
by intent and the user is advanced to the next selection phase.
*/

public class GetChapter extends ListActivity {
    //immutable strings used to construct the queries and normal strings to be defined later for the intent
    private static final String TABLE_NAME = "chapters";
    private static final String BOOK_SELECTOR = "book_id = ";
    private static final String CHAPTER_NUMBER = "chapter_number";
    private static String VOLUME_TITLE;
    private static String BOOK_TITLE;
    private static int VOLUME_ID;
    private static int BOOK_ID;

    //the list views, the chapters arraylist, the chapterIDs arraylist
    private ListView listView;
    private ArrayList<String> chapters;
    private ArrayList<Integer> chapterIDs;

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
        //fill the chapters arraylist and populate the list view with it. Fill the chapterIDs arraylist too.
        fillChapters();
        fillChapterIDs();
        setUpList();
    }

    //Populate the list with the chapters arraylist
    private void setUpList() {
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, chapters));
        listView = getListView();

        /*when a list view item is clicked, the volume title, volume id, the book id, the book title,
        the chapter title, and the chapter id are put into the intent and the next activity is started.*/
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Intent intent = new Intent(getApplicationContext(), GetVerse.class);
                intent.putExtra("volumeID", VOLUME_ID);
                intent.putExtra("volumeTitle", VOLUME_TITLE);
                intent.putExtra("bookID", BOOK_ID);
                intent.putExtra("bookTitle", BOOK_TITLE);
                intent.putExtra("chapterTitle", position);
                intent.putExtra("chapterID", chapterIDs.get(position));
                startActivity(intent);
            }
        });
    }

    //the database is queried and will return all of the chapter names (numbers). Each will be added to the arraylist.
    private void fillChapters() {
        chapters = new ArrayList<String>();
        Cursor chapterCursor = mDb.query(TABLE_NAME, new String[] {CHAPTER_NUMBER}, BOOK_SELECTOR + BOOK_ID, null, null, null, "id");
        chapterCursor.moveToFirst();
        if(!chapterCursor.isAfterLast()) {
            do {
                String name = chapterCursor.getString(0);
                chapters.add(name);
            } while (chapterCursor.moveToNext());
        }
        chapterCursor.close();
    }

    //the database is queried and will return all of the book ids. Each will be added to the arraylist.
    private void fillChapterIDs() {
        chapterIDs = new ArrayList<Integer>();
        Cursor chapterIDCursor = mDb.query(TABLE_NAME, new String[] {"id"}, BOOK_SELECTOR + BOOK_ID, null, null, null, "id");
        chapterIDCursor.moveToFirst();
        if(!chapterIDCursor.isAfterLast()) {
            do {
                Integer id = chapterIDCursor.getInt(0);
                chapterIDs.add(id);
            } while (chapterIDCursor.moveToNext());
        }
        chapterIDCursor.close();
    }

}
