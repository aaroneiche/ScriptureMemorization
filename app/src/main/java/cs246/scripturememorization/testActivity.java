package cs246.scripturememorization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class testActivity extends AppCompatActivity {

    private Scripture s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        s = new Scripture();//intent.getParcelableExtra("Scripture");
        TextView text = findViewById(R.id.textView2);
        text.setText(s.toString());


        Button button  = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                Pack up Scripture and send it back
                 */
                Intent intent = new Intent ();
                intent.putExtra("Scripture", s);
                setResult(testActivity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
