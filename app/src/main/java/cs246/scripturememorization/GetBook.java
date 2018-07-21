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

/* GetBook is a list activity. It will query the database for all of the books in the previously
selected volume. All of those books will fill a list that is then used to populate
the list view. When a book is clicked, it's value is added to the scripture path to be passed
by intent and the user is advanced to the next selection phase.
*/

public class GetBook extends ListActivity {
    //immutable strings used to construct the queries and normal strings to be defined later for the intent
    private static final String TABLE_NAME = "books";
    private static final String VOLUME_SELECTOR = "volume_id = ";
    private static String VOLUME_TITLE;
    private static final String BOOK_NAME = "book_title";
    private static int VOLUME_ID;

    //the list views, the volumes arraylist, the bookIDs arraylist
    private ListView listView;
    private ArrayList<String> books;
    private ArrayList<Integer> bookIDs;

    //connect to the databse
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_volume);

        Intent intent = getIntent();
        VOLUME_ID = intent.getExtras().getInt("volumeID") + 1;
        VOLUME_TITLE = intent.getExtras().getString("volumeTitle");

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
        //fill the books arraylist and populate the list view with it. Fill the bookIDs arraylist too.
        fillBooks();
        fillBookIDs();
        setUpList();
    }

    //Populate the list with the books arraylist
    private void setUpList() {
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, books));
        listView = getListView();

        /*when a list view item is clicked, the volume title, volume id, the book id, and the book title
        are put into the intent and the next activity is started.*/
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Intent intent = new Intent(getApplicationContext(), GetChapter.class);
                intent.putExtra("volumeID", VOLUME_ID);
                intent.putExtra("volumeTitle", VOLUME_TITLE);
                intent.putExtra("bookID", bookIDs.get(position));
                intent.putExtra("bookTitle", books.get(position));
                startActivity(intent);
            }
        });
    }

    //the database is queried and will return all of the book names. Each will be added to the arraylist.
    private void fillBooks() {
        books = new ArrayList<String>();
        Cursor bookCursor = mDb.query(TABLE_NAME, new String[] {BOOK_NAME},VOLUME_SELECTOR + VOLUME_ID, null, null, null, "id");
        bookCursor.moveToFirst();
        if(!bookCursor.isAfterLast()) {
            do {
                String name = bookCursor.getString(0);
                books.add(name);
            } while (bookCursor.moveToNext());
        }
        bookCursor.close();
    }

    //the database is queried and will return all of the book ids. Each will be added to the arraylist.
    private void fillBookIDs() {
        bookIDs = new ArrayList<Integer>();
        Cursor bookIDCursor = mDb.query(TABLE_NAME, new String[] {"id"},VOLUME_SELECTOR + VOLUME_ID, null, null, null, "id");
        bookIDCursor.moveToFirst();
        if(!bookIDCursor.isAfterLast()) {
            do {
                Integer id = bookIDCursor.getInt(0);
                bookIDs.add(id);
            } while (bookIDCursor.moveToNext());
        }
        bookIDCursor.close();
    }

}
