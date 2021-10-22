package com.bawp.trivia;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bawp.trivia.data.Repository;
import com.bawp.trivia.databinding.ActivityMainBinding;
import com.bawp.trivia.model.Question;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity {

    List<Question> questionList;
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    Boolean noFraude= false;
    int score=0;
    int pastHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportActionBar().hide();
        SharedPreferences preferences= getSharedPreferences("QUESTIONINDEX", MODE_PRIVATE);
        currentQuestionIndex= preferences.getInt("valueofI", 0);

        SharedPreferences preferences1= getSharedPreferences("SSS",MODE_PRIVATE);
        String t = preferences1.getString("SSS",String.valueOf(0));
        pastHighScore= Integer.valueOf(t);

        questionList = new Repository().getQuestions(questionArrayList -> {
                    binding.questionTV.setText(questionArrayList.get(currentQuestionIndex)
                            .getAnswer());

                    updateCounter(questionArrayList);
                }

        );


        binding.nextBtn.setOnClickListener(view -> {
            noFraude= false;
            currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
            updateQuestion();

        });
        binding.trueBtn.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();

        });
        binding.falseBtn.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();

        });

        binding.goDirectToAnyQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.goDirectToAnyQuestionED.getText().toString().isEmpty()){
                    binding.goDirectToAnyQuestionED.setError("Please enter any question index!");
                }else {
                    currentQuestionIndex= Integer.parseInt(String.valueOf(binding.goDirectToAnyQuestionED.getText()));
                    binding.goDirectToAnyQuestionED.setText("");
                    noFraude= false;
                    updateQuestion();
                }
            }
        });


        aboutScore();


    }

    private void aboutScore() {

        String textformate= "Your highest score: "+ pastHighScore+"\n"+ "Current score: "+ String.valueOf(score);

        if (score> pastHighScore){
            pastHighScore= score;
            Snackbar.make(binding.cardView, "Congrats!, You have broken your past high score.",Snackbar.LENGTH_LONG).show();
            textformate= "Current score: "+ String.valueOf(score);
        }

        binding.scoreTv.setText(textformate);

    }

    private void checkAnswer(boolean userChoseCorrect) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if (userChoseCorrect == answer) {
            if (!noFraude){
                noFraude= true;
                score += 100;
                aboutScore();
            }

            snackMessageId = R.string.correct_answer;
            fadeAnimation();

        } else {
            if (!noFraude){
                noFraude= true;
                score -= 50;
                aboutScore();
            }
            snackMessageId = R.string.incorrect;
            shakeAnimation();
        }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT)
                .show();

    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.outOftotal.setText(String.format(getString(R.string.text_formatted),
                currentQuestionIndex, questionArrayList.size()));
    }

    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTV.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTV.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void updateQuestion() {

        SharedPreferences preferences= getSharedPreferences("QUESTIONINDEX", MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putInt("valueofI", currentQuestionIndex);
        editor.apply();
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTV.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTV.setTextColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTV.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    protected void onPause() {
        SharedPreferences preferences2= getSharedPreferences("SSS", MODE_PRIVATE);
        SharedPreferences.Editor editor1= preferences2.edit();
        editor1.putString("SSS", String.valueOf(score));
        editor1.apply();
        super.onPause();

    }

}