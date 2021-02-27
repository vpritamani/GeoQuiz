package android.bignerdranch.com;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;


    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    boolean[] questionAnswered = new boolean[mQuestionBank.length];
    int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        for(int i = 0; i < questionAnswered.length; i++){
            // at the start, none of the question have been answered yet
            questionAnswered[i] = false;
        }

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if(questionAnswered[mCurrentIndex] == false) { // if question hasn't been answer then check
                    checkAnswer(true);
                }
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if(questionAnswered[mCurrentIndex] == false) { // if question hasn't been answer then check
                    checkAnswer(false);
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 0) { // makes sure that the current index will not become negative
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                else{
                    // if current index is zero and previous is pressed, get the last of the questions
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                mIsCheater = false;
                updateQuestion();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        // when checkAnswer has been called, dont allow another answer
        questionAnswered[mCurrentIndex] = true;

        int messageResId = 0;

        if(mIsCheater){
            messageResId = R.string.judgment_toast;
        }
        else {
            if (userPressedTrue == answerIsTrue) {
                // correct answers variable goes up 1 with a correct answer
                correctAnswers++;
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        showToast(messageResId);

        if(allQuestionsAnswered()){
            // find percentage using logic, then show the toast with the percentage score at the bottom of the screen
            // final percentage will be an int instead of double so it looks better
            // if the goal is to show as a double with decimal points, the line below my current setup is how I would do that
            int percentage = (int) (100.0 * (double) correctAnswers / (double) questionAnswered.length);
            // double percentage = 100.0 * (double) correctAnswers / (double) questionAnswered.length;
            Toast toast = Toast.makeText(QuizActivity.this, percentage + "%", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,0);
            toast.show();
        }
    }

    private boolean allQuestionsAnswered(){
        boolean toReturn = true;

        // if one or more of the questions haven't been answered, return false
        for(int i = 0; i < questionAnswered.length; i++){
            if(questionAnswered[i] == false){
                toReturn = false;
            }
        }

        return toReturn;
    }

    // Helper method - receives input of what to display, then displays the toast at the top of the screen
    private void showToast(int toDisplay){
        Toast toast = Toast.makeText(QuizActivity.this, toDisplay, Toast.LENGTH_SHORT);
        // yOffset set to 200 so the toast is below the purple 'title bar'
        toast.setGravity(Gravity.TOP,0,200);
        toast.show();
    }
}