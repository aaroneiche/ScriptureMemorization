package cs246.scripturememorization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fill in the blank game, asks the user how much of the scripture should be hidden,
 * then plays the game, updates lastReviewed and memorized/dateMemorized if played
 * with the scripture completely hidden and no wrong answers.
 *
 * Logic:
 * OnCreate starts game with 'how well do you know this scripture' and a calls startMenu to add a
 *    list of buttons with options for how much of the scripture is hidden.
 *
 * OnClick PercentClick - when you click one of the above buttons it sets how much is to be
 *    hidden and starts setUpGame()
 *
 * SetUpGame turns the scripture into a list of words, it builds a second list for the hidden words,
 *    then calls updateScripture to show the scripture with the blanks in place and updateButtons
 *    to set the first 9 hidden words in a grid of buttons.
 *
 * OnClick wordClick - drives the game from here, it handles guesses, removing hidden words from
 *    the list as they are guessed and updates the scripture and remaining buttons accordingly.
 *
 * updateButtons - when the list of hidden words is empty updateButtons calls endGame();
 *
 * endGame - Shows that you finished the scripture and your score.
 */

public class FITBActivity extends AppCompatActivity {
    private Scripture scripture;
    private List<word> words;
    private List<word> hiddenWords;
    private int percentHidden = 50;
    private TextView scriptureView;
    private TableLayout buttons;
    private final String TAG = "fitb";
    private int wrongAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitb);
        scriptureView = findViewById(R.id.text_scripture);
        buttons = findViewById(R.id.tl_buttons);
        Intent intent = getIntent();
        scripture = intent.getParcelableExtra("Scripture");
        startMenu();
    }

    /*
    add a menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.add(0, 0, 0, "Return to main menu");
        return true;
    }

    /*
    handle input from menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 0:
                exit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    builds a menu to select how much of the scripture should be hidden
     */
    private void startMenu() {
        TableRow row = new TableRow(this);
        for (int i = 25; i <= 100; i += 25) {
            Button b = new Button(this);
            b.setText(String.format(Locale.ENGLISH,"%d %%",i));
            b.setOnClickListener(new percentClick(i));
            row.addView(b);
        }
        buttons.addView(row);
    }

    /**
     builds the list of words, sets which are hidden,
     and updates the scripture and button view to start the game
     */
    private void setUpGame() {
        Log.d(TAG, "set up game");
        buttons.removeAllViews();
        List<String> text = sfHelper.textToList(scripture.text);
        List<Boolean> hiding = new ArrayList<>();
        for (int i = 0; i <= percentHidden / 10; i++) {
            hiding.add(true);
        }
        while (hiding.size() <= 10) {
            hiding.add(false);
        }
        Collections.shuffle(hiding);
        int hIndex = 0;
        words = new ArrayList<>();
        hiddenWords = new ArrayList<>();
        for (int i = 0; i < text.size(); i++) {
            word w = new word(text.get(i));
            w.hidden = hiding.get(hIndex++);
            if (hIndex >= 10) {
                hIndex = 0;
                Collections.shuffle(hiding);
            }
            if (w.hidden) {
                hiddenWords.add(w);
            }
            words.add(w);
        }
        updateScripture();
        updateButtons();
    }

    /**
     Finishes everything up
     */
    private void endGame() {
        scripture.lastReviewed = new Date();
        //figure out how much of the scripture was put back together correctly
        double math = (double)(words.size() - wrongAnswers) / words.size() * percentHidden;
        int correct = (int)math;
        Log.d(TAG, "Words: " + words.size() + " Incorrect: " + wrongAnswers + " Percent: " + correct);
        if (scripture.percentCorrect < correct) {
            scripture.percentCorrect = correct;
        }

        if (correct == 100) {
            scripture.memorized = true;
            scripture.dateMemorized = new Date();
            scriptureView.setText(R.string.fitb_endMessage);
        }

        else {
            scriptureView.setText(String.format(Locale.ENGLISH, "Well done, you got %d %%", correct));
        }
        buttons.removeAllViews();
        TableRow row = new TableRow(this);
        Button b = new Button(this);
        b.setText("Main Menu");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        row.addView(b);
        buttons.addView(row);
    }

    /**
     returns to the main menu
     */
    private void exit() {
        Intent intent = new Intent ();
        intent.putExtra("Scripture", scripture);
        setResult(FITBActivity.RESULT_OK, intent);
        finish();
    }

    /**
     * updates the text of the scripture, starts with lots of blanks and updates
     * every time a word is guessed to show progress.
     */
    private void updateScripture() {
        SpannableStringBuilder builder = new SpannableStringBuilder("");

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).hidden) {
                ForegroundColorSpan black = new ForegroundColorSpan(0xFF000000);
                int start = builder.length();
                builder.append("______ ");
                builder.setSpan(black, start, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            else if (words.get(i).correct) {
                ForegroundColorSpan black = new ForegroundColorSpan(0xFF000000);
                int start = builder.length();
                builder.append(words.get(i).word);
                builder.setSpan(black, start, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            else {
                ForegroundColorSpan red = new ForegroundColorSpan(0xFFFF0000);
                int start = builder.length();
                builder.append(words.get(i).word);
                builder.setSpan(red, start, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        scriptureView.setText(builder, TextView.BufferType.SPANNABLE);
    }

    /**
     * buttons are used to hold the next 9 (or less) words that are currently blanks,
     * the buttons are generated then added to a table in a random order.
     * Buttons with the same word are interchangeable.
     */
    private void updateButtons() {
        List<TextView> buttons = new ArrayList<>();
        List<Integer> random = new ArrayList<>();
        int size = hiddenWords.size();

        if (size == 0) {
            endGame();
        }

        //get all the buttons
        if (size > 9) {
            size = 9;
        }

        for (int i = 0; i < size; i++) {
            Button b = new Button(this);
            b.setText(hiddenWords.get(i).word);
            b.setOnClickListener(new wordClick(hiddenWords.get(i).word));
            buttons.add(b);
            random.add(i);
        }

        //put all the buttons in the buttons in a random order
        this.buttons.removeAllViews();
        Collections.shuffle(random);
        int r = 0;
        while (size > 0) {
            int rowSize = size;
            if (rowSize > 3) {
                rowSize = 3;
            }
            TableRow row = new TableRow(this);
            for (int i = 0; i < rowSize; i++) {
                row.addView(buttons.get(random.get(r++)));
            }
            this.buttons.addView(row);
            size -= rowSize;
        }
    }

    /**
     * holds the word to be placed on a button, determines whether or not the button should be hidden,
     * or if it has been guessed, whether or not the guess was correct.
     */

    private class word {
        public boolean correct;
        public boolean hidden;
        public String word;

        word(String w) {
            this.word = w;
            hidden = false;
            correct = true;
        }
    }

    /**
     * on click checks if the button's word is the same as the next word needed in the sequence.
     * If it is, it is a correct answer, and the word is updated.  If not wrongAnswer is incremented,
     * and the word is updated and marked wrong.
     * The scripture and buttons set is then updated.
     */
    private class wordClick implements View.OnClickListener {
        private String word;

        wordClick(String w) {
            word = w;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "index: " + word);

            if (word.equals(hiddenWords.get(0).word)) {
                hiddenWords.get(0).correct = true;
            }
            else {
                hiddenWords.get(0).correct = false;
                wrongAnswers++;
            }
            hiddenWords.get(0).hidden = false;
            hiddenWords.remove(0);

            updateScripture();
            updateButtons();
        }
    }

    /**
     * used on buttons at the beginning of the activity to set the percentage of the scripture
     * that should be hidden.
     */
    private class percentClick implements View.OnClickListener {
        private int percent;

        percentClick(int percent) {
            this.percent = percent;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "button clicked: " + percent);
            percentHidden = percent;
            setUpGame();
        }
    }
}
