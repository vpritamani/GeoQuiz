package android.bignerdranch.com;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                showToastAtTop(R.string.correct_toast);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                showToastAtTop(R.string.incorrect_toast);
            }
        });

    }

    // Helper method - receives input of what to display, then displays the toast at the top of the screen
    private void showToastAtTop(int toDisplay){
        Toast toast = Toast.makeText(QuizActivity.this, toDisplay, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,200);
        toast.show();
    }
}