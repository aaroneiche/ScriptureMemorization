package cs246.scripturememorization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Entry point of program, shows the current scripture and a list of scriptures to be worked on.
 * Has a menu for interacting with the current scripture.
 */
public class MainActivity extends AppCompatActivity implements Main_RecyclerViewAdapter.ItemClickListener {
    private Scripture scripture;
    private List<Scripture> scriptureList;
    private TextView scriptureReference;
    private TextView scriptureText;
    private TextView scriptureLastReviewed;
    private TextView scriptureMemorized;
    private TextView scripturePercent;
    private ImageView scriptureMemorizedSticker;
    private Main_RecyclerViewAdapter scriptureAdapter;
    private Gson _gson;

    private static final String TAG = "main_debug";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scriptureList = new ArrayList<>();
        scriptureReference = findViewById(R.id.text_scriptureName);
        scriptureText = findViewById(R.id.text_ScriptureText);
        scriptureLastReviewed = findViewById(R.id.text_lastReviewed);
        scriptureMemorized = findViewById(R.id.text_memorized);
        scripturePercent = findViewById(R.id.text_percent);
        scriptureMemorizedSticker = findViewById(R.id.image_Memorized);
        RecyclerView rv = findViewById(R.id.rv_scriptures);
        rv.setLayoutManager(new LinearLayoutManager(this));
        scriptureAdapter = new Main_RecyclerViewAdapter(this, scriptureList);
        scriptureAdapter.setClickListener(this);
        rv.setAdapter(scriptureAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                LinearLayoutManager.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(scriptureAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        _gson = new Gson();

        /*
         * Retrieves stored data
         */

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int sCount = pref.getInt("Scripture_Count", 0);
        if (sCount > 0)
            scripture = _gson.fromJson(pref.getString("s_" + 0, null ), Scripture.class);
        for (int i = 1; i < sCount; i++)
        {
            Scripture s = _gson.fromJson(pref.getString("s_" + i, null ), Scripture.class);
            if (s != null) {
                scriptureList.add(s);
            }
            scriptureAdapter.notifyDataSetChanged();
        }

         /*
         * adds a menu button that starts a pop-up menu when tapped.
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
        updateScriptureView();
    }

    /**
     * Sets up an onClick Listener for the items in the list that switches the current scripture
     * with the one clicked.
     */
    @Override
    public void onItemClick(View view, int position) {
        Scripture temp = scripture;
        scripture = scriptureList.get(position);
        scriptureList.set(position, temp);
        scriptureAdapter.notifyItemChanged(position);
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
                //fill in the blank
                break;
            case R.id.i4:
                scripture.lastReviewed = new Date();
                updateScriptureView();
                break;
            case R.id.i5:
                scripture.lastReviewed = new Date();
                scripture.dateMemorized = new Date();
                scripture.percentCorrect = 100;
                scripture.memorized = true;
                updateScriptureView();
                Toast.makeText(MainActivity.this, "Well done, you mastered this scripture!", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    /**
     * starts an activity to fetch a new scripture
     */
    private void getScripture() {
        Intent intent = new Intent(MainActivity.this, testActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * reviews the current scripture
     */
    private void removeScripture() {
        if (scripture == null) {
            Toast.makeText(MainActivity.this, "No scripture to remove", Toast.LENGTH_LONG).show();
            return;
        }
        if (!scriptureList.isEmpty()) {
            scripture = scriptureList.get(0);
            scriptureList.remove(0);
            scriptureAdapter.notifyItemRemoved(0);
            updateScriptureView();
        } else {
            scripture = null;
            updateScriptureView();
        }
    }

    /**
     * starts an activity to use recitation to practice a scripture
     */
    private void reciteScripture() {
        if (scripture == null) {
            Toast.makeText(MainActivity.this, "Please add a scripture to recite", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, ReciteActivity.class);
        intent.putExtra("Scripture", scripture);
        startActivityForResult(intent, 1);
    }

    /**
     * starts an activity to fill-in-the-blank to practice a scripture
     */

    private void fitb() {
        if (scripture == null) {
            Toast.makeText(MainActivity.this, "Please add a scripture to practice", Toast.LENGTH_LONG).show();
            return;
        }
        //Intent intent = new Intent(MainActivity.this, FITBActivity.class);
        //intent.putExtra("Scripture", scripture);
        //startActivityForResult(intent, 2);
    }

    /**
    Handles the returned data from the different activities
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Scripture s = data.getParcelableExtra("Scripture");
            if (scripture == null) {
                scripture = s;
            } else {
                scriptureList.add(0, scripture);
                scriptureAdapter.notifyItemInserted(0);
                scripture = s;
            }
            updateScriptureView();
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            scripture = data.getParcelableExtra("Scripture");
            updateScriptureView();
        } else {
            Log.e(TAG, "result code not okay");
        }
    }

    /**
     * Fills views with data from current scripture.
     */
    private void updateScriptureView() {
        if (scripture == null) {
            scriptureReference.setText(getString(R.string.main_ScriptureDefaultText));
            scriptureText.setVisibility(View.INVISIBLE);
            scriptureLastReviewed.setVisibility(View.INVISIBLE);
            scriptureMemorized.setVisibility(View.INVISIBLE);
            scriptureMemorizedSticker.setVisibility(View.INVISIBLE);
            scripturePercent.setVisibility(View.INVISIBLE);
        }
        else {
            scriptureReference.setText(sfHelper.getReference(scripture));
            scriptureText.setVisibility(View.VISIBLE);
            scriptureText.setText(scripture.text);
            scripturePercent.setVisibility(View.VISIBLE);
            scripturePercent.setText(sfHelper.getPercent(scripture));
            if (scripture.lastReviewed != null) {
                scriptureLastReviewed.setVisibility(View.VISIBLE);
                scriptureLastReviewed.setText(sfHelper.getDateReviewed(scripture.lastReviewed));
            }
            else {
                scriptureLastReviewed.setVisibility(View.INVISIBLE);
            }
            if (scripture.dateMemorized != null) {
                scriptureMemorized.setVisibility(View.VISIBLE);
                scriptureMemorized.setText( sfHelper.getDateMemorized(scripture.dateMemorized));
            }
            else {
                scriptureMemorized.setVisibility(View.INVISIBLE);
            }
            if (scripture.memorized) {
                scriptureMemorizedSticker.setImageResource(R.drawable.check);
            }
            else {
                scriptureMemorizedSticker.setImageResource(R.drawable.box);
            }
        }
    }

    /**
     * saves data for next time.
     */
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        if (scripture != null) {
            editor.putInt("Scripture_Count", 1 + scriptureList.size());
            editor.putString("s_0", _gson.toJson(scripture));
            for (int i = 0; i < scriptureList.size(); i++) {
                editor.putString("s_" + (i + 1), _gson.toJson(scriptureList.get(i)));
            }
        } else
            editor.putInt("Scripture_Count", 0);
        editor.apply();
    }
}
