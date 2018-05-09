package com.example.guest1.stackgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class RulesActivity extends AppCompatActivity{

    private TextView scoreText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        scoreText = findViewById(R.id.scoreTextRules);

        Intent i = getIntent();
        scoreText.setText(i.getStringExtra("SCORE_TEXT"));
    }
}
