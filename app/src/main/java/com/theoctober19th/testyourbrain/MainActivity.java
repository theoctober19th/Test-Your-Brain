package com.theoctober19th.testyourbrain;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView timerTextView, scoreTextView, questionTextView, resultTextView, animationTextView;
    GridLayout questionGridLayout;
    ConstraintLayout gameLayout;
    Button resetButton, goButton;
    CircularProgressBar timerProgressCircle;

    Handler handler;
    Runnable runnable;


    CountDownTimer countDownTimer;

    int timeLimit = 120;
    static int score = 0;
    static int questionCount = 0;

    int remainingTime = timeLimit;

    int APP_JUST_STARTED = 0;
    int GAME_RUNNING = 1;
    int TIMER_OUT = 2;
    int gameState = APP_JUST_STARTED;

    int answerAtPosition;
    int correctAnswersInARow = 0;

    boolean wasPreviousAnswerCorrect = false;
    boolean shouldIncreaseTime = false;

    public void generateNewQuestion(){
        Random random = new Random();
        int numA = random.nextInt(400)+ 50;
        int numB = random.nextInt(400) + 50;
        questionTextView.setText(numA + " + " + numB);

        answerAtPosition = random.nextInt(4);

        for(int i=0; i<4; i++){
            Button button = (Button) questionGridLayout.getChildAt(i);;
            if(i==answerAtPosition){
                button.setText(Integer.toString(numA + numB));
            }else{
                int foo;
                do{
                   foo =random.nextInt(20) + numA + numB - 10;
                }while(foo == numA+numB);
                button.setText(Integer.toString(foo));
            }
        }
        // return answerAtPosition;
    }

    public void onResetButtonClicked(View view){
        if(gameState == APP_JUST_STARTED){
            gameState = GAME_RUNNING;
            startGame();

        }else if(gameState == TIMER_OUT){
            gameState = APP_JUST_STARTED;
            resetGame();
        }else if(gameState == GAME_RUNNING){
            gameState = APP_JUST_STARTED;
            displayScores();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetButton = (Button) findViewById(R.id.resetButton);
        //initialize scoreTextView
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        updateScore();

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        questionGridLayout = (GridLayout) findViewById(R.id.questionGridLayout);
        //initialize timertextview
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        timerTextView.setText(DateUtils.formatElapsedTime(timeLimit));

        goButton = (Button) findViewById(R.id.goButton);
        timerProgressCircle = (CircularProgressBar) findViewById(R.id.timerProgressCircle);

        gameLayout = (ConstraintLayout) findViewById(R.id.gameLayout);

        animationTextView = (TextView) findViewById(R.id.animationTextView);

        gameLayout.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        resultTextView.setVisibility(View.INVISIBLE);
    }

    public void startGame(){
        score = 0;
        questionCount = 0;
        updateScore();

        goButton.setVisibility(View.INVISIBLE);
        gameLayout.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
        resetButton.setText("QUIT");
        resultTextView.setText("");
        startTimer(timeLimit*1000);

        generateNewQuestion();
    }

    public void stopGame(){

        gameLayout.setVisibility(View.INVISIBLE);
        handler.removeCallbacks(runnable);
        resetButton.setText("Start Again");
    }

    public void updateScore(){
        scoreTextView.setText(score + " / " + questionCount);
    }

    public void onAnswerSelected(View view) throws InterruptedException {
        if(answerAtPosition == questionGridLayout.indexOfChild(view)){
            resultTextView.setText("Correct! :)");
            score++;
            if(wasPreviousAnswerCorrect){
                correctAnswersInARow++;
                if(correctAnswersInARow >= 5) {
                    shouldIncreaseTime = true;
                    correctAnswersInARow -= 5;
                }
            }
            wasPreviousAnswerCorrect = true;
        }else{
            resultTextView.setText("WRONG ! :(");
            wasPreviousAnswerCorrect = false;
        }
        questionCount++;
        updateScore();
        generateNewQuestion();
    }

    private void increaseTimer(long remainingTime) {
        countDownTimer.cancel();
        startTimer(remainingTime+10*1000);
    }


    public void resetGame(){

    }

    public void displayScores(){
        stopGame();
        String resultText = "SCORE: " + String.valueOf(score);
        resetButton.setVisibility(View.VISIBLE);
        resultTextView.setText(resultText);
        gameState = APP_JUST_STARTED;
    }


    public void startTimer(long time){
        remainingTime = timeLimit;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(remainingTime == 0){
                    gameState = TIMER_OUT;
                    displayScores();
                }else {

                    if (shouldIncreaseTime){
                        handler.postDelayed(this, 5000);

                        animationTextView.setX(100);
                        animationTextView.setY(100);
                        animationTextView.setVisibility(View.VISIBLE);
                        animationTextView.animate().scaleXBy(3).scaleYBy(3).setDuration(400).translationX(10).translationY(10).setDuration(400);

                        shouldIncreaseTime = false;
                    }
                    else {
                        animationTextView.setVisibility(View.INVISIBLE);
                        resultTextView.animate().scaleX(1).scaleY(1).setDuration(300);
                        handler.postDelayed(this, 1000);
                    }

                    timerTextView.setText(DateUtils.formatElapsedTime(remainingTime ));
                    remainingTime--;
                    timerProgressCircle.setProgressWithAnimation(100 * remainingTime / timeLimit, 800);
                }
            }
        };
        handler.post(runnable);
    }
    /*
    public void startTimer(long time){
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                /*if(shouldIncreaseTime) {
                    shouldIncreaseTime = false;
                    //increaseTimer(l);
                    cancel();
                    startTimer(l + 10*1000);
                }
                timerTextView.setText(DateUtils.formatElapsedTime(l / 1000 +1));
                timerProgressCircle.setProgressWithAnimation(100*l/(timeLimit*1000), 800);
            }

            @Override
            public void onFinish() {
                gameState = TIMER_OUT;
                displayScores();
            }
        }.start();
    }
    */
}
