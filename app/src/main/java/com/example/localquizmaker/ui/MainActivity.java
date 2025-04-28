package com.example.localquizmaker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localquizmaker.R;

/**
 * Main screen with navigation buttons.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateQuiz = findViewById(R.id.btnCreateQuiz);
        Button btnPlayQuiz = findViewById(R.id.btnPlayQuiz);
        Button btnManageQuiz = findViewById(R.id.btnManageQuiz);

        btnCreateQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateQuizActivity.class));
            }
        });
        btnPlayQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayQuizActivity.class));
            }
        });
        btnManageQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ManageQuizActivity.class));
            }
        });
    }
} 