package com.example.tasklistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tasklistapp.dbutils.TaskDbUtils;
import com.example.tasklistapp.model.Task;
import com.example.tasklistapp.services.TaskReminderService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * This is the main activity of the app
 * the activity represents list of tasks added by user and a way to add a new one
 * The user can adjust a list by selection of one of specific filters provided in options menu
 */
public class TaskListActivity extends AppCompatActivity {

    public static final String FILTER_KEY = "filterKey";
    public static final String FILTER_KEY_ACTIVE = "active";
    public static final String FILTER_KEY_TODAY = "today";
    public static final String FILTER_KEY_ALL = "all";

    private String appliedFilter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView emptyListLabel;

    public static Intent newIntent(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        emptyListLabel = findViewById(R.id.empty_list_label);

        recyclerView = findViewById(R.id.tasks_recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton newTaskButton = findViewById(R.id.new_task_btn);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        updateTasksList();

        TaskReminderService.setServiceAlarm(this, true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTasksList();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_list_filter, menu);
        if (appliedFilter.equals(FILTER_KEY_ACTIVE)) menu.findItem(R.id.active_task_filter).setChecked(true);
        else if (appliedFilter.equals(FILTER_KEY_TODAY)) menu.findItem(R.id.today_task_filter).setChecked(true);
        else menu.findItem(R.id.all_task_filter).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.active_task_filter:
                if (!item.isChecked()) item.setChecked(true);
                applyFilter(FILTER_KEY_ACTIVE);
                return true;
            case R.id.today_task_filter:
                if (!item.isChecked()) item.setChecked(true);
                applyFilter(FILTER_KEY_TODAY);
                return true;
            case R.id.all_task_filter:
                if (!item.isChecked()) item.setChecked(true);
                applyFilter(FILTER_KEY_ALL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateTasksList() {
        TaskDbUtils dbUtils = TaskDbUtils.getUtils(TaskListActivity.this);
        appliedFilter = PreferenceManager.getDefaultSharedPreferences(TaskListActivity.this)
                .getString(FILTER_KEY, FILTER_KEY_ALL);
        List<Task> tasks;

        if (appliedFilter.equals(FILTER_KEY_ACTIVE)) {
            tasks = dbUtils.getActiveTasks();
        } else if (appliedFilter.equals(FILTER_KEY_TODAY)) {
            tasks = dbUtils.getTodayTasks();
        } else {
            tasks = dbUtils.getAllTasks();
        }

        if (tasks.size() == 0) {
            emptyListLabel.setVisibility(View.VISIBLE);
        } else {
            emptyListLabel.setVisibility(View.GONE);
        }
            mAdapter = new TasksListAdapter(tasks);
            recyclerView.setAdapter(mAdapter);
    }

    private void addNewTask() {
        Intent newTaskIntent = TaskActivity.newIntent(TaskListActivity.this, null);
        startActivity(newTaskIntent);
    }


    private void applyFilter(String filterKey) {
        switch (filterKey) {
            case FILTER_KEY_ACTIVE:
                PreferenceManager.getDefaultSharedPreferences(TaskListActivity.this)
                        .edit()
                        .putString(FILTER_KEY, FILTER_KEY_ACTIVE)
                        .apply();
                break;
            case FILTER_KEY_TODAY:
                PreferenceManager.getDefaultSharedPreferences(TaskListActivity.this)
                        .edit()
                        .putString(FILTER_KEY, FILTER_KEY_TODAY)
                        .apply();
                break;
            default:
                PreferenceManager.getDefaultSharedPreferences(TaskListActivity.this)
                        .edit()
                        .putString(FILTER_KEY, FILTER_KEY_ALL)
                        .apply();
        }

        updateTasksList();
    }

}