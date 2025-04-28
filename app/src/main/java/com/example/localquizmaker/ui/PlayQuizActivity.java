package com.example.localquizmaker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localquizmaker.R;
import com.example.localquizmaker.database.QuizDbHelper;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.View;
import android.view.LayoutInflater;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.AlertDialog;
import android.widget.EditText;
import android.view.ViewGroup;

/**
 * Activity for playing a selected quiz.
 */
public class PlayQuizActivity extends AppCompatActivity {
    private QuizDbHelper dbHelper;
    private List<QuizItem> quizList = new ArrayList<>();
    private List<QuestionItem> questionList = new ArrayList<>();
    private int currentQuestion = 0;
    private int correctCount = 0;
    private int selectedQuizId = -1;
    private ListView listViewQuizzes;
    private TextView textQuestion;
    private RadioGroup radioGroupOptions;
    private RadioButton radioOption1, radioOption2, radioOption3, radioOption4;
    private Button btnSubmitAnswer, btnNextQuestion;
    private ImageView imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new QuizDbHelper(this);
        showQuizList();
    }

    private void showQuizList() {
        listViewQuizzes = new ListView(this);
        setContentView(listViewQuizzes);
        loadQuizzes();
    }

    private void loadQuizzes() {
        quizList.clear();
        Cursor cursor = dbHelper.getAllQuizzes();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            quizList.add(new QuizItem(id, title));
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (QuizItem q : quizList) adapter.add(q.title);
        listViewQuizzes.setAdapter(adapter);
        listViewQuizzes.setOnItemClickListener((parent, view, position, id) -> {
            selectedQuizId = quizList.get(position).id;
            startQuiz(selectedQuizId);
        });
    }

    private void startQuiz(int quizId) {
        setContentView(R.layout.activity_play_quiz);
        textQuestion = findViewById(R.id.textQuestion);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        radioOption1 = findViewById(R.id.radioOption1);
        radioOption2 = findViewById(R.id.radioOption2);
        radioOption3 = findViewById(R.id.radioOption3);
        radioOption4 = findViewById(R.id.radioOption4);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        imageResult = findViewById(R.id.imageResult);
        questionList.clear();
        Cursor cursor = dbHelper.getQuestionsForQuiz(quizId);
        while (cursor.moveToNext()) {
            int qid = cursor.getInt(cursor.getColumnIndexOrThrow("question_id"));
            String text = cursor.getString(cursor.getColumnIndexOrThrow("question_text"));
            String o1 = cursor.getString(cursor.getColumnIndexOrThrow("option1"));
            String o2 = cursor.getString(cursor.getColumnIndexOrThrow("option2"));
            String o3 = cursor.getString(cursor.getColumnIndexOrThrow("option3"));
            String o4 = cursor.getString(cursor.getColumnIndexOrThrow("option4"));
            int correct = cursor.getInt(cursor.getColumnIndexOrThrow("correct_option"));
            questionList.add(new QuestionItem(qid, text, o1, o2, o3, o4, correct));
        }
        cursor.close();
        if (questionList.isEmpty()) {
            Toast.makeText(this, "No questions in this quiz.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Collections.shuffle(questionList);
        currentQuestion = 0;
        correctCount = 0;
        showQuestion();
        btnSubmitAnswer.setOnClickListener(v -> checkAnswer());
        btnNextQuestion.setOnClickListener(v -> {
            currentQuestion++;
            if (currentQuestion < questionList.size()) {
                showQuestion();
            } else {
                showFinalScore();
            }
        });
    }

    private void showQuestion() {
        QuestionItem q = questionList.get(currentQuestion);
        textQuestion.setText(q.text);
        radioOption1.setText(q.o1);
        radioOption2.setText(q.o2);
        radioOption3.setText(q.o3);
        radioOption4.setText(q.o4);
        radioGroupOptions.clearCheck();
        btnSubmitAnswer.setEnabled(true);
        btnNextQuestion.setEnabled(false);
        if (imageResult != null) {
            imageResult.setVisibility(View.GONE);
        }
    }

    private void checkAnswer() {
        QuestionItem q = questionList.get(currentQuestion);
        int checkedId = radioGroupOptions.getCheckedRadioButtonId();
        int selected = -1;
        if (checkedId == R.id.radioOption1) selected = 1;
        else if (checkedId == R.id.radioOption2) selected = 2;
        else if (checkedId == R.id.radioOption3) selected = 3;
        else if (checkedId == R.id.radioOption4) selected = 4;
        if (selected == -1) {
            Toast.makeText(this, "Select an answer.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageResult != null) {
            imageResult.setVisibility(View.VISIBLE);
            if (selected == q.correct) {
                imageResult.setImageResource(R.drawable.ic_correct);
                correctCount++;
            } else {
                imageResult.setImageResource(R.drawable.ic_wrong);
            }
        }
        btnSubmitAnswer.setEnabled(false);
        btnNextQuestion.setEnabled(true);
    }

    private void showFinalScore() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quiz_score, null);
        TextView textScore = dialogView.findViewById(R.id.textScore);
        TextView textCorrectCount = dialogView.findViewById(R.id.textCorrectCount);
        Button btnDone = dialogView.findViewById(R.id.btnDone);

        int percent = (int) ((correctCount * 100.0f) / questionList.size());
        textScore.setText(percent + "%");
        textCorrectCount.setText(String.format("Correct Answers: %d/%d", correctCount, questionList.size()));

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create();

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    public static class QuizItem {
        public int id;
        public String title;
        public QuizItem(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }
    public static class QuestionItem {
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