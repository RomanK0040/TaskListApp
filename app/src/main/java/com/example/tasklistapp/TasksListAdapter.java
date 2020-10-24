package com.example.tasklistapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasklistapp.dbutils.TaskDbUtils;
import com.example.tasklistapp.model.Task;

import java.util.List;

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.TaskViewHolder> {
    List<Task> mTasksList;

    Context mContext;

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView taskCard;
        TextView taskTitleView;
        TextView taskDetailsView;
        TextView taskDateView;
        CheckBox taskDoneView;

        private Task mTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            taskCard = itemView.findViewById(R.id.task_view);
            taskTitleView = itemView.findViewById(R.id.title_view);
            taskDetailsView = itemView.findViewById(R.id.details_view);
            taskDateView = itemView.findViewById(R.id.date_view);
            taskDoneView = itemView.findViewById(R.id.done_checkbox);
        }


        @Override
        public void onClick(View v) {
            Intent updateIntent = TaskActivity.newIntent(v.getContext(), mTask.getId());
            v.getContext().startActivity(updateIntent);
        }

        public void bindTask(Task task) {
            mTask = task;
        }
    }

    public TasksListAdapter(List<Task> tasksList) {
        mTasksList = tasksList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_card, parent, false);

        mContext = parent.getContext();
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final Task task = mTasksList.get(position);
        holder.bindTask(task);
        holder.taskTitleView.setText(task.getTitle());
        holder.taskDetailsView.setText(task.getDescription());

        holder.taskDateView.setText(DateFormat.getMediumDateFormat(mContext).format(task.getDeadline()));
        holder.taskDoneView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setDone(isChecked);
               TaskDbUtils.getUtils(mContext).updateTask(task);
            }
        });
        holder.taskDoneView.setChecked(task.isDone());

    }

    @Override
    public int getItemCount() {
        return mTasksList.size();
    }

}
