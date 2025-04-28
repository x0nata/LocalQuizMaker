package com.example.localquizmaker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    public interface OnQuestionActionListener {
        void onEdit(QuestionItem question);
        void onDelete(QuestionItem question);
    }
    public static class QuestionItem {
        public int id;
        public String text;
        public QuestionItem(int id, String text) {
            this.id = id;
            this.text = text;
        }
    }
    private List<QuestionItem> questionList;
    private OnQuestionActionListener listener;
    public QuestionAdapter(List<QuestionItem> questionList, OnQuestionActionListener listener) {
        this.questionList = questionList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new QuestionViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionItem question = questionList.get(position);
        holder.textQuestion.setText(question.text);
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(question));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(question));
    }
    @Override
    public int getItemCount() {
        return questionList.size();
    }
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView textQuestion;
        Button btnEdit, btnDelete;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(android.R.id.text1);
            btnEdit = new Button(itemView.getContext());
            btnEdit.setText("Edit");
            btnDelete = new Button(itemView.getContext());
            btnDelete.setText("Delete");
            ((ViewGroup) itemView).addView(btnEdit);
            ((ViewGroup) itemView).addView(btnDelete);
        }
    }
} 