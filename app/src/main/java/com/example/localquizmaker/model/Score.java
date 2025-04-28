package com.example.localquizmaker.model;

/**
 * Model class representing a Score entity.
 */
public class Score {
    private int id;
    private int quizId;
    private int score; // percentage
    private String date; // e.g., "2025-04-28"

    public Score() {}

    public Score(int id, int quizId, int score, String date) {
        this.id = id;
        this.quizId = quizId;
        this.score = score;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
} 