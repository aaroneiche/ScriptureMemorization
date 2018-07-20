package cs246.scripturememorization;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.util.Log;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.SQLException;

import java.util.ArrayList;
import java.io.IOException;


public class GetVolume extends ListActivity {
    private static final String TABLE_NAME = "volumes";
    private static final String VOLUME_NAME = "volume_title";

    private ListView listView;
    private ArrayList<String> volumes;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_volume);

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
        fillVolumes();
        setUpList();
    }

    private void setUpList() {
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, volumes));
        listView = getListView();

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id) {
                Intent intent = new Intent(getApplicationContext(), GetBook.class);
                intent.putExtra("volumeTitle", volumes.get(position));
                intent.putExtra("volumeID", position);
                startActivity(intent);
            }
        });
    }

    private void fillVolumes() {
        volumes = new ArrayList<String>();
        Cursor volumeCursor = mDb.query(TABLE_NAME,
                new String[] {VOLUME_NAME},null, null, null, null, "id");
        volumeCursor.moveToFirst();
        if(!volumeCursor.isAfterLast()) {
            do {
                String name = volumeCursor.getString(0);
                volumes.add(name);
            } while (volumeCursor.moveToNext());
        }
        volumeCursor.close();
    }

}
