package com.example.localquizmaker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localquizmaker.R;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.localquizmaker.database.QuizDbHelper;
import com.example.localquizmaker.model.Question;
import java.util.ArrayList;

/**
 * Activity for creating a new quiz and adding questions.
 */
public class CreateQuizActivity extends AppCompatActivity {
    private EditText editQuizTitle, editQuestion, editOption1, editOption2, editOption3, editOption4;
    private RadioGroup radioGroupCorrect;
    private Button btnAddQuestion, btnSaveQuiz;
    private ArrayList<Question> questionList = new ArrayList<>();
    private QuizDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        editQuizTitle = findViewById(R.id.editQuizTitle);
        editQuestion = findViewById(R.id.editQuestion);
        editOption1 = findViewById(R.id.editOption1);
        editOption2 = findViewById(R.id.editOption2);
        editOption3 = findViewById(R.id.editOption3);
        editOption4 = findViewById(R.id.editOption4);
        radioGroupCorrect = findViewById(R.id.radioGroupCorrect);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnSaveQuiz = findViewById(R.id.btnSaveQuiz);
        dbHelper = new QuizDbHelper(this);

        btnAddQuestion.setOnClickListener(v -> addQuestion());
        btnSaveQuiz.setOnClickListener(v -> saveQuiz());
    }

    private void addQuestion() {
        String questionText = editQuestion.getText().toString().trim();
        String option1 = editOption1.getText().toString().trim();
        String option2 = editOption2.getText().toString().trim();
        String option3 = editOption3.getText().toString().trim();
        String option4 = editOption4.getText().toString().trim();
        int checkedId = radioGroupCorrect.getCheckedRadioButtonId();
        int correctOption = -1;
        if (checkedId == R.id.radioOption1) correctOption = 1;
        else if (checkedId == R.id.radioOption2) correctOption = 2;
        else if (checkedId == R.id.radioOption3) correctOption = 3;
        else if (checkedId == R.id.radioOption4) correctOption = 4;

        if (questionText.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty() || correctOption == -1) {
            Toast.makeText(this, "Please fill all question fields and select the correct answer.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Add to temporary list
        questionList.add(new Question(0, 0, questionText, option1, option2, option3, option4, correctOption));
        Toast.makeText(this, "Question added.", Toast.LENGTH_SHORT).show();
        // Clear fields
        editQuestion.setText("");
        editOption1.setText("");
        editOption2.setText("");
        editOption3.setText("");
        editOption4.setText("");
        radioGroupCorrect.clearCheck();
    }

    private void saveQuiz() {
        String quizTitle = editQuizTitle.getText().toString().trim();
        if (quizTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a quiz title.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (questionList.isEmpty()) {
            Toast.makeText(this, "Please add at least one question.", Toast.LENGTH_SHORT).show();
            return;
        }
        long quizId = dbHelper.insertQuiz(quizTitle);
        if (quizId == -1) {
            Toast.makeText(this, "Failed to save quiz.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Question q : questionList) {
            dbHelper.insertQuestion((int)quizId, q.getQuestionText(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getCorrectOption());
        }
        Toast.makeText(this, "Quiz saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
} 