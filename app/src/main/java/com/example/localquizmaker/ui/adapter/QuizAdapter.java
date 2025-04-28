package com.example.localquizmaker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.localquizmaker.R;
import com.example.localquizmaker.ui.ManageQuizActivity.QuizItem;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    public interface OnQuizActionListener {
        void onEdit(QuizItem quiz);
        void onDelete(QuizItem quiz);
    }
    private List<QuizItem> quizList;
    private OnQuizActionListener listener;
    
    public QuizAdapter(List<QuizItem> quizList, OnQuizActionListener listener) {
        this.quizList = quizList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizItem quiz = quizList.get(position);
        holder.textQuizTitle.setText(quiz.title);
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(quiz));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(quiz));
    }
    
    @Override
    public int getItemCount() {
        return quizList.size();
    }
    
    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView textQuizTitle;
        MaterialButton btnEdit, btnDelete;
        
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuizTitle = itemView.findViewById(R.id.textQuizTitle);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 