package com.example.localquizmaker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.localquizmaker.R;
import com.example.localquizmaker.database.QuizDbHelper;
import android.database.Cursor;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.example.localquizmaker.ui.adapter.QuizAdapter;
import android.content.Intent;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.google.android.material.textfield.TextInputEditText;

public class ManageQuizActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private QuizDbHelper dbHelper;
    private List<QuizItem> quizList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quiz);
        recyclerView = findViewById(R.id.recyclerViewQuizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new QuizDbHelper(this);
        loadQuizzes();
    }

    private void loadQuizzes() {
        quizList.clear();
        Cursor cursor = dbHelper.getAllQuizzes();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                quizList.add(new QuizItem(id, title));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new QuizAdapter(quizList, new QuizAdapter.OnQuizActionListener() {
            @Override
            public void onEdit(QuizItem quiz) {
                showQuestions(quiz);
            }

            @Override
            public void onDelete(QuizItem quiz) {
                new AlertDialog.Builder(ManageQuizActivity.this)
                    .setTitle("Delete Quiz")
                    .setMessage("Are you sure you want to delete this quiz?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dbHelper.deleteQuiz(quiz.id);
                        loadQuizzes();
                        Toast.makeText(ManageQuizActivity.this, "Quiz deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showQuestions(QuizItem quiz) {
        ArrayList<QuestionItem> questions = new ArrayList<>();
        Cursor cursor = dbHelper.getQuestionsForQuiz(quiz.id);
        while (cursor.moveToNext()) {
            int qid = cursor.getInt(cursor.getColumnIndexOrThrow("question_id"));
            String text = cursor.getString(cursor.getColumnIndexOrThrow("question_text"));
            String o1 = cursor.getString(cursor.getColumnIndexOrThrow("option1"));
            String o2 = cursor.getString(cursor.getColumnIndexOrThrow("option2"));
            String o3 = cursor.getString(cursor.getColumnIndexOrThrow("option3"));
            String o4 = cursor.getString(cursor.getColumnIndexOrThrow("option4"));
            int correct = cursor.getInt(cursor.getColumnIndexOrThrow("correct_option"));
            questions.add(new QuestionItem(qid, text, o1, o2, o3, o4, correct));
        }
        cursor.close();

        if (questions.isEmpty()) {
            Toast.makeText(this, "No questions in this quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] items = new CharSequence[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            items[i] = "Question " + (i + 1) + ": " + questions.get(i).text;
        }

        new AlertDialog.Builder(this)
            .setTitle(quiz.title + " - Select Question to Edit")
            .setItems(items, (dialog, which) -> {
                showEditQuestionDialog(quiz.id, questions.get(which));
            })
            .setPositiveButton("Add New Question", (dialog, which) -> {
                showEditQuestionDialog(quiz.id, null);
            })
            .setNegativeButton("Close", null)
            .show();
    }

    private void showEditQuestionDialog(int quizId, QuestionItem question) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_question, null);
        TextInputEditText inputText = dialogView.findViewById(R.id.editQuestionText);
        TextInputEditText inputOption1 = dialogView.findViewById(R.id.editOption1);
        TextInputEditText inputOption2 = dialogView.findViewById(R.id.editOption2);
        TextInputEditText inputOption3 = dialogView.findViewById(R.id.editOption3);
        TextInputEditText inputOption4 = dialogView.findViewById(R.id.editOption4);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupCorrect);

        if (question != null) {
            inputText.setText(question.text);
            inputOption1.setText(question.o1);
            inputOption2.setText(question.o2);
            inputOption3.setText(question.o3);
            inputOption4.setText(question.o4);
            switch (question.correct) {
                case 1: radioGroup.check(R.id.radioCorrect1); break;
                case 2: radioGroup.check(R.id.radioCorrect2); break;
                case 3: radioGroup.check(R.id.radioCorrect3); break;
                case 4: radioGroup.check(R.id.radioCorrect4); break;
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(question == null ? "Add Question" : "Edit Question")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String qText = inputText.getText().toString().trim();
                String o1 = inputOption1.getText().toString().trim();
                String o2 = inputOption2.getText().toString().trim();
                String o3 = inputOption3.getText().toString().trim();
                String o4 = inputOption4.getText().toString().trim();
                
                int correct;
                int checkedId = radioGroup.getCheckedRadioButtonId();
                if (checkedId == R.id.radioCorrect1) correct = 1;
                else if (checkedId == R.id.radioCorrect2) correct = 2;
                else if (checkedId == R.id.radioCorrect3) correct = 3;
                else if (checkedId == R.id.radioCorrect4) correct = 4;
                else correct = -1;

                if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || correct == -1) {
                    Toast.makeText(this, "Please fill all fields and select the correct answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (question == null) {
                    dbHelper.insertQuestion(quizId, qText, o1, o2, o3, o4, correct);
                    Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.updateQuestion(question.id, qText, o1, o2, o3, o4, correct);
                    Toast.makeText(this, "Question updated", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // Data holders
    public static class QuizItem {
        public int id;
        public String title;
        public QuizItem(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    private static class QuestionItem {
        public int id;
        public String text, o1, o2, o3, o4;
        public int correct;
        public QuestionItem(int id, String text, String o1, String o2, String o3, String o4, int correct) {
            this.id = id;
            this.text = text;
            this.o1 = o1;
            this.o2 = o2;
            this.o3 = o3;
            this.o4 = o4;
            this.correct = correct;
        }
    }
} 