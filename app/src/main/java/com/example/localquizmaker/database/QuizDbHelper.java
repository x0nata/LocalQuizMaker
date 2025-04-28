package com.example.localquizmaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * SQLiteOpenHelper for managing quizzes, questions, and scores tables.
 */
public class QuizDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "localquizmaker.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_QUIZZES = "quizzes";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_SCORES = "scores";

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create quizzes table
        db.execSQL("CREATE TABLE " + TABLE_QUIZZES + " (" +
                "quiz_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL);");

        // Create questions table
        db.execSQL("CREATE TABLE " + TABLE_QUESTIONS + " (" +
                "question_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_id INTEGER, " +
                "question_text TEXT NOT NULL, " +
                "option1 TEXT NOT NULL, " +
                "option2 TEXT NOT NULL, " +
                "option3 TEXT NOT NULL, " +
                "option4 TEXT NOT NULL, " +
                "correct_option INTEGER NOT NULL, " +
                "FOREIGN KEY(quiz_id) REFERENCES " + TABLE_QUIZZES + "(quiz_id));");

        // Create scores table
        db.execSQL("CREATE TABLE " + TABLE_SCORES + " (" +
                "score_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_id INTEGER, " +
                "name TEXT, " +
                "score INTEGER NOT NULL, " +
                "date TEXT NOT NULL, " +
                "FOREIGN KEY(quiz_id) REFERENCES " + TABLE_QUIZZES + "(quiz_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZZES);
        onCreate(db);
    }

    // Insert a new quiz and return its ID
    public long insertQuiz(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        return db.insert(TABLE_QUIZZES, null, values);
    }

    // Insert a new question for a quiz
    public long insertQuestion(int quizId, String questionText, String option1, String option2, String option3, String option4, int correctOption) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_id", quizId);
        values.put("question_text", questionText);
        values.put("option1", option1);
        values.put("option2", option2);
        values.put("option3", option3);
        values.put("option4", option4);
        values.put("correct_option", correctOption);
        return db.insert(TABLE_QUESTIONS, null, values);
    }

    // Get all quizzes
    public Cursor getAllQuizzes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_QUIZZES, null, null, null, null, null, null);
    }

    // Delete a quiz and its questions
    public void deleteQuiz(int quizId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, "quiz_id=?", new String[]{String.valueOf(quizId)});
        db.delete(TABLE_QUIZZES, "quiz_id=?", new String[]{String.valueOf(quizId)});
    }

    // Get all questions for a quiz
    public Cursor getQuestionsForQuiz(int quizId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_QUESTIONS, null, "quiz_id=?", new String[]{String.valueOf(quizId)}, null, null, null);
    }

    // Update quiz title
    public int updateQuizTitle(int quizId, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        return db.update(TABLE_QUIZZES, values, "quiz_id=?", new String[]{String.valueOf(quizId)});
    }

    // Update a question
    public int updateQuestion(int questionId, String questionText, String option1, String option2, String option3, String option4, int correctOption) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question_text", questionText);
        values.put("option1", option1);
        values.put("option2", option2);
        values.put("option3", option3);
        values.put("option4", option4);
        values.put("correct_option", correctOption);
        return db.update(TABLE_QUESTIONS, values, "question_id=?", new String[]{String.valueOf(questionId)});
    }

    // Delete a question
    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, "question_id=?", new String[]{String.valueOf(questionId)});
    }

    // Insert a score with name
    public void insertScore(int quizId, String name, int score, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_id", quizId);
        values.put("name", name);
        values.put("score", score);
        values.put("date", date);
        db.insert(TABLE_SCORES, null, values);
    }

    // Get all scores (with name)
    public Cursor getAllScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SCORES, null, null, null, null, null, "score_id DESC");
    }
} 