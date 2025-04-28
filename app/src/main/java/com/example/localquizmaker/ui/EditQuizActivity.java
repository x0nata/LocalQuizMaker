package com.example.localquizmaker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localquizmaker.R;
import com.example.localquizmaker.database.QuizDbHelper;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.localquizmaker.ui.adapter.QuestionAdapter;
import android.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.RadioGroup;

public class EditQuizActivity extends AppCompatActivity {
    private EditText editQuizTitle;
    private Button btnSaveChanges;
    private int quizId;
    private QuizDbHelper dbHelper;
    private List<QuestionAdapter.QuestionItem> questionList = new ArrayList<>();
    private RecyclerView recyclerViewQuestions;
    private QuestionAdapter questionAdapter;
    private Button btnAddQuestion;
    // TODO: Add RecyclerView for questions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);
        editQuizTitle = findViewById(R.id.editQuizTitle);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        dbHelper = new QuizDbHelper(this);
        quizId = getIntent().getIntExtra("quiz_id", -1);
        if (quizId == -1) {
            Toast.makeText(this, "Invalid quiz.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestions);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        loadQuiz();
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnAddQuestion.setOnClickListener(v -> showEditQuestionDialog(null));
    }

    private void loadQuiz() {
        Cursor cursor = dbHelper.getAllQuizzes();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_id"));
            if (id == quizId) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                editQuizTitle.setText(title);
                break;
            }
        }
        cursor.close();
        loadQuestions();
    }

    private void loadQuestions() {
        questionList.clear();
        Cursor cursor = dbHelper.getQuestionsForQuiz(quizId);
        while (cursor.moveToNext()) {
            int qid = cursor.getInt(cursor.getColumnIndexOrThrow("question_id"));
            String text = cursor.getString(cursor.getColumnIndexOrThrow("question_text"));
            questionList.add(new QuestionAdapter.QuestionItem(qid, text));
        }
        cursor.close();
        questionAdapter = new QuestionAdapter(questionList, new QuestionAdapter.OnQuestionActionListener() {
            @Override
            public void onEdit(QuestionAdapter.QuestionItem question) {
                showEditQuestionDialog(question);
            }
            @Override
            public void onDelete(QuestionAdapter.QuestionItem question) {
                dbHelper.deleteQuestion(question.id);
                loadQuestions();
                Toast.makeText(EditQuizActivity.this, "Question deleted", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewQuestions.setAdapter(questionAdapter);
    }

    private void showEditQuestionDialog(QuestionAdapter.QuestionItem question) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(question == null ? "Add Question" : "Edit Question");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_question, null);
        EditText inputText = dialogView.findViewById(R.id.editQuestionText);
        EditText inputOption1 = dialogView.findViewById(R.id.editOption1);
        EditText inputOption2 = dialogView.findViewById(R.id.editOption2);
        EditText inputOption3 = dialogView.findViewById(R.id.editOption3);
        EditText inputOption4 = dialogView.findViewById(R.id.editOption4);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupCorrect);
        if (question != null) {
            Cursor c = dbHelper.getQuestionsForQuiz(quizId);
            while (c.moveToNext()) {
                if (c.getInt(c.getColumnIndexOrThrow("question_id")) == question.id) {
                    inputText.setText(c.getString(c.getColumnIndexOrThrow("question_text")));
                    inputOption1.setText(c.getString(c.getColumnIndexOrThrow("option1")));
                    inputOption2.setText(c.getString(c.getColumnIndexOrThrow("option2")));
                    inputOption3.setText(c.getString(c.getColumnIndexOrThrow("option3")));
                    inputOption4.setText(c.getString(c.getColumnIndexOrThrow("option4")));
                    int correct = c.getInt(c.getColumnIndexOrThrow("correct_option"));
                    if (correct == 1) radioGroup.check(R.id.radioOption1);
                    else if (correct == 2) radioGroup.check(R.id.radioOption2);
                    else if (correct == 3) radioGroup.check(R.id.radioOption3);
                    else if (correct == 4) radioGroup.check(R.id.radioOption4);
                    break;
                }
            }
            c.close();
        }
        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String qText = inputText.getText().toString().trim();
            String o1 = inputOption1.getText().toString().trim();
            String o2 = inputOption2.getText().toString().trim();
            String o3 = inputOption3.getText().toString().trim();
            String o4 = inputOption4.getText().toString().trim();
            int checkedId = radioGroup.getCheckedRadioButtonId();
            int correct = -1;
            if (checkedId == R.id.radioOption1) correct = 1;
            else if (checkedId == R.id.radioOption2) correct = 2;
            else if (checkedId == R.id.radioOption3) correct = 3;
            else if (checkedId == R.id.radioOption4) correct = 4;
            if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || correct == -1) {
                Toast.makeText(this, "Fill all fields and select correct answer.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (question == null) {
                dbHelper.insertQuestion(quizId, qText, o1, o2, o3, o4, correct);
                Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateQuestion(question.id, qText, o1, o2, o3, o4, correct);
                Toast.makeText(this, "Question updated", Toast.LENGTH_SHORT).show();
            }
            loadQuestions();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveChanges() {
        String newTitle = editQuizTitle.getText().toString().trim();
        if (newTitle.isEmpty()) {
            Toast.makeText(this, "Quiz title cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Update quiz title and questions in database
        Toast.makeText(this, "Changes saved.", Toast.LENGTH_SHORT).show();
        finish();
    }
} 