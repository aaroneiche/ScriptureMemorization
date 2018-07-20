package cs246.scripturememorization;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Entry point of program, shows the current scripture and a list of scriptures to be worked on.
 * Has a menu for interacting with the current scripture.
 * Implements item click listener so that we can click on items in the recycler view
 * Implements OnStartDragListener so that we can drag around items in the recycler view
 */
public class MainActivity extends AppCompatActivity implements Main_RecyclerViewAdapter.ItemClickListener, OnStartDragListener {
    private Scripture mScripture;
    private List<Scripture> mScriptureList;
    private TextView mScriptureReference;
    private TextView mScriptureText;
    private TextView mScriptureLastReviewed;
    private TextView mScriptureMemorized;
    private TextView mScripturePercent;
    private ImageView mScriptureMemorizedSticker;
    private OnViewGlobalLayoutListener mScrollListener;
    private Main_RecyclerViewAdapter mScriptureAdapter;
    private ItemTouchHelper mItemTouchHelper;
    DatabaseHelper mDBHelper;
    SQLiteDatabase mDb;

    private static final String TAG = "main_debug";
    private static final int MAX_SCRIPTURE_HEIGHT = 700;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Find frequently updated views
         */
        mScriptureList = new ArrayList<>();
        mScriptureReference = findViewById(R.id.text_scriptureName);
        mScriptureText = findViewById(R.id.text_ScriptureText);
        mScriptureLastReviewed = findViewById(R.id.text_lastReviewed);
        mScriptureMemorized = findViewById(R.id.text_memorized);
        mScripturePercent = findViewById(R.id.text_percent);
        mScriptureMemorizedSticker = findViewById(R.id.image_Memorized);
        /*
        Set up scrollView holding the scripture text with max height.
         */
        ScrollView scrollView = findViewById(R.id.scrollView);
        mScrollListener = new OnViewGlobalLayoutListener(scrollView, MAX_SCRIPTURE_HEIGHT);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(mScrollListener);
        /*
        Recycler view set up and add touch listeners
         */
        RecyclerView rv = findViewById(R.id.rv_scriptures);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mScriptureAdapter = new Main_RecyclerViewAdapter(this, this, mScriptureList);
        mScriptureAdapter.setClickListener(this);
        rv.setAdapter(mScriptureAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                LinearLayoutManager.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mScriptureAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
        /*
         Retrieves stored data
         */
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

        getScripturesFromDatabase();
         /*
         adds a menu button that starts a pop-up menu when tapped.
         */
        final ImageView menu_button = findViewById(R.id.button_menu);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create pop-up menu
                PopupMenu pop = new PopupMenu(MainActivity.this, menu_button);
                //Inflate the popup
                pop.getMenuInflater().inflate(R.menu.main_menu, pop.getMenu());

                //add on click listener
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        menuItemSelected(item.getItemId());
                        return true;
                    }
                });
                pop.show();
            }
        });

        ImageView help_button = findViewById(R.id.button_help);
        final ImageView help_image = findViewById(R.id.image_help);
        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help_image.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        help_image.setVisibility(View.INVISIBLE);
                    }
                }, 8000);
            }
        });

        help_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help_image.setVisibility(View.INVISIBLE);
            }
        });

        Intent intent = getIntent();
        Scripture sc = intent.getParcelableExtra("Scripture");
        if (sc != null) {
            if (mScripture == null) {
                mScripture = sc;
            } else {
                mScriptureList.add(0, mScripture);
                mScriptureAdapter.notifyItemInserted(0);
                mScripture = sc;
            }
        }
        updateScriptureView();
    }

    /**
     * Sets up an onClick Listener for the items in the list that switches the current scripture
     * with the one clicked.
     */
    @Override
    public void onItemClick(View view, int position) {
        Scripture temp = mScripture;
        mScripture = mScriptureList.get(position);
        mScriptureList.set(position, temp);
        mScriptureAdapter.notifyItemChanged(position);
        updateScriptureView();
    }

    /**
     * Handles selections in the pop-up menu
     */
    private void menuItemSelected(int item) {
        switch (item) {
            case R.id.i0:
                getScripture();
                break;
            case R.id.i1:
                removeScripture();
                break;
            case R.id.i2:
                reciteScripture();
                break;
            case R.id.i3:
                fitb();                 //fill in the blank
                break;
            case R.id.i4:
                mScripture.lastReviewed = new Date();
                updateScriptureView();
                break;
            case R.id.i5:
                mScripture.lastReviewed = new Date();
                mScripture.dateMemorized = new Date();
                mScripture.percentCorrect = 100;
                mScripture.memorized = true;
                updateScriptureView();
                Toast.makeText(MainActivity.this, "Well done, you mastered this scripture!", Toast.LENGTH_LONG).show();
                break;
            case R.id.i6:
                getRandomScripture();
                break;
            default:
                break;
        }
    }

    /**
     * starts an activity to fetch a new scripture
     */
    private void getScripture() {
        Intent intent = new Intent(MainActivity.this, GetVolume.class);
        startActivityForResult(intent, 0);
    }

    /**
     * reviews the current scripture
     */
    private void removeScripture() {
        if (mScripture == null) {
            Toast.makeText(MainActivity.this, "No scripture to remove", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mScriptureList.isEmpty()) {
            mScripture = mScriptureList.get(0);
            mScriptureList.remove(0);
            mScriptureAdapter.notifyItemRemoved(0);
            updateScriptureView();
        } else {
            mScripture = null;
            updateScriptureView();
        }
    }

    /**
     * starts an activity to use recitation to practice a scripture
     */
    private void reciteScripture() {
        if (mScripture == null) {
            Toast.makeText(MainActivity.this, "Please add a scripture to recite", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, ReciteActivity.class);
        intent.putExtra("Scripture", mScripture);
        startActivityForResult(intent, 1);
    }

    /**
     * starts an activity to fill-in-the-blank to practice a scripture
     */

    private void fitb() {
        if (mScripture == null) {
            Toast.makeText(MainActivity.this, "Please add a scripture to practice", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, FITBActivity.class);
        intent.putExtra("Scripture", mScripture);
        startActivityForResult(intent, 2);
    }

    private void getRandomScripture() {
        Random random = new Random();
        int randomInt = random.nextInt(41996);
        Scripture s = getScriptureByID(randomInt);
        if (mScripture == null) {
            mScripture = s;
        } else {
            mScriptureList.add(0, mScripture);
            mScriptureAdapter.notifyItemInserted(0);
            mScripture = s;
        }
        updateScriptureView();
    }

    /*
    Handles the returned data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Scripture s = data.getParcelableExtra("Scripture");
                    if (mScripture == null) {
                        mScripture = s;
                    } else {
                        mScriptureList.add(0, mScripture);
                        mScriptureAdapter.notifyItemInserted(0);
                        mScripture = s;
                    }
                    updateScriptureView();
                    break;
                case 1:
                    mScripture = data.getParcelableExtra("Scripture");
                    updateScriptureView();
                    break;
                case 2:
                    mScripture = data.getParcelableExtra("Scripture");
                    updateScriptureView();
                    break;
                default:
                    break;
            }
            saveData();
        } else {
            Log.e(TAG, "result code not okay");
        }
    }

    /**
     * Fills views with data from current scripture.
     */
    private void updateScriptureView() {
        if (mScripture == null) {
            mScriptureReference.setText(getString(R.string.main_ScriptureDefaultText));
            mScriptureText.setVisibility(View.INVISIBLE);
            mScriptureLastReviewed.setVisibility(View.INVISIBLE);
            mScriptureMemorized.setVisibility(View.INVISIBLE);
            mScriptureMemorizedSticker.setVisibility(View.INVISIBLE);
            mScripturePercent.setVisibility(View.INVISIBLE);
        }
        else {
            mScriptureReference.setText(sfHelper.getReference(mScripture));
            mScriptureText.setVisibility(View.VISIBLE);
            mScriptureText.setText(mScripture.text);
            mScripturePercent.setVisibility(View.VISIBLE);
            mScripturePercent.setText(sfHelper.getPercent(mScripture));
            mScrollListener.update();
            if (mScripture.lastReviewed != null) {
                mScriptureLastReviewed.setVisibility(View.VISIBLE);
                mScriptureLastReviewed.setText(sfHelper.getDateReviewed(mScripture.lastReviewed));
            }
            else {
                mScriptureLastReviewed.setVisibility(View.INVISIBLE);
            }
            if (mScripture.dateMemorized != null) {
                mScriptureMemorized.setVisibility(View.VISIBLE);
                mScriptureMemorized.setText( sfHelper.getDateMemorized(mScripture.dateMemorized));
            }
            else {
                mScriptureMemorized.setVisibility(View.INVISIBLE);
            }
            if (mScripture.memorized) {
                mScriptureMemorizedSticker.setImageResource(R.drawable.check);
            }
            else {
                mScriptureMemorizedSticker.setImageResource(R.drawable.box);
            }
        }
        saveData();
    }

    /**
     * saves data for next time.
     */
    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    void saveData() {
        mDb.execSQL("CREATE TABLE IF NOT EXISTS user_data (" +
                "_id INTEGER, verseID INTEGER, lastReviewed TEXT, dateMemorized TEXT, percentCorrect INTEGER, PRIMARY KEY (_id))");
        mDb.delete("user_data", null, null);
        if (mScripture != null) {
            writeScripture(mScripture, 0);
        }
        for (int i = 0; i < mScriptureList.size(); i++) {
            writeScripture(mScriptureList.get(i), i + 1);
        }
    }

    void writeScripture(Scripture s, int id) {
        ContentValues values = new ContentValues();
        if (s != null) {
            values.put("_id", id);
            values.put("verseID", s.verseID);
            if (s.lastReviewed != null) {
                values.put("lastReviewed", s.lastReviewed.toString());
            }
            else {
                values.put("lastReviewed", "null");
            }
            if (s.memorized) {
                values.put("dateMemorized", s.dateMemorized.toString());
            }
            else {
                values.put("dateMemorized", "null");
            }
            values.put("percentCorrect", s.percentCorrect);
        }

        mDb.insert("user_data", null, values);
    }

    void getScripturesFromDatabase() {
        Cursor userData = mDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+ "user_data" +"'", null);
        if (userData != null) {
            if (userData.getCount() > 0) {
                //if table exists, read data
                for (int i = 0; ; i++) {
                    userData = mDb.rawQuery("SELECT * FROM user_data WHERE _id = " + i, null);
                    userData.moveToFirst();
                    Log.d(TAG, "userData size: " + userData.getCount());
                    if (userData.getCount() == 0) {
                        break;
                    }
                    int verse_id = userData.getInt(userData.getColumnIndex("verseID"));
                    String dateMemorized = userData.getString(userData.getColumnIndex("dateMemorized"));
                    String dateReviewed = userData.getString(userData.getColumnIndex("lastReviewed"));
                    int percentCorrect = userData.getInt(userData.getColumnIndex("percentCorrect"));

                    Scripture s = getScriptureByID(verse_id);
                    if (!dateReviewed.equalsIgnoreCase("null")) {
                        s.lastReviewed = new Date(dateReviewed);
                    }
                    if (!dateMemorized.equalsIgnoreCase("null")) {
                        s.dateMemorized = new Date(dateMemorized);
                        s.memorized = true;
                    }
                    if (i == 0) {
                        mScripture = s;
                    } else {
                        mScriptureList.add(s);
                    }
                    s.percentCorrect = percentCorrect;
                }
                mScriptureAdapter.notifyDataSetChanged();
            }
            userData.close();
        }
    }

    Scripture getScriptureByID(int id) {
        Cursor scriptureCursor = mDb.rawQuery("SELECT * FROM scriptures WHERE verse_id = " + id, null);

        scriptureCursor.moveToFirst();

        Log.d(TAG, scriptureCursor.getString(scriptureCursor.getColumnIndex("volume_title")));

        Scripture returnScripture = new Scripture (
                scriptureCursor.getString(scriptureCursor.getColumnIndex("volume_title")),
                scriptureCursor.getString(scriptureCursor.getColumnIndex("book_title")),
                scriptureCursor.getInt(scriptureCursor.getColumnIndex("chapter_number")),
                scriptureCursor.getInt(scriptureCursor.getColumnIndex("verse_number")),
                id,
                scriptureCursor.getString(scriptureCursor.getColumnIndex("scripture_text")));
        scriptureCursor.close();

        return returnScripture;
    }


    /**
     * Starts a drag, signals ItemTouchHelperCallback to flag a drag, which calls
     * Main_RecyclerViewAdapter onItemMove()
     * @param viewHolder The holder of the view to drag.
     */

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
