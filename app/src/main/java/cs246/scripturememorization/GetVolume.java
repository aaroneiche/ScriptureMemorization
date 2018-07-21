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

/* GetVolume is a list activity. It will query the database for all of the volumes in the LDS canon
including the The Old Testament, The New Testament, The Book of Mormon, Doctrine and Covenants,
and The Pearl of Great Price. All of those volumes will fill a list that is then used to populate
the list view. When a volume is clicked, it's value is added to the scripture path to be passed
by intent and the user is advanced to the next selection phase.
*/

public class GetVolume extends ListActivity {
    //immutable strings used to construct the queries
    private static final String TABLE_NAME = "volumes";
    private static final String VOLUME_NAME = "volume_title";

    //the list view and the volumes arraylist
    private ListView listView;
    private ArrayList<String> volumes;

    //connect to the databse
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
        //fill the volumes arraylist and populate the list view with it
        fillVolumes();
        setUpList();
    }

    //Populate the list with the volumes arraylist
    private void setUpList() {
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, volumes));
        listView = getListView();

        /*whe a list view item is clicked, the volume title and volume id are put into the intent
        and the next activity is started.*/
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id) {
                Intent intent = new Intent(getApplicationContext(), GetBook.class);
                intent.putExtra("volumeTitle", volumes.get(position));
                intent.putExtra("volumeID", position);
                startActivity(intent);
            }
        });
    }

    //the database is queried and will return all of the volumes. Each volume will be added.
    private void fillVolumes() {
        volumes = new ArrayList<String>();
        Cursor volumeCursor = mDb.query(TABLE_NAME, new String[] {VOLUME_NAME},null, null, null, null, "id");
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
