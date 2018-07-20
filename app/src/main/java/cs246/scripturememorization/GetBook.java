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


public class GetBook extends ListActivity {
    private static final String TABLE_NAME = "books";
    private static final String VOLUME_SELECTOR = "volume_id = ";
    private static String VOLUME_TITLE;
    private static final String BOOK_NAME = "book_title";
    private static int VOLUME_ID;

    private ListView listView;
    private ArrayList<String> books;
    private ArrayList<Integer> bookIDs;

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
        fillBooks();
        fillBookIDs();
        setUpList();
    }

    private void setUpList() {
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, books));
        listView = getListView();

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
