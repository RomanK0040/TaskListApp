package com.example.tasklistapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.tasklistapp.dbutils.TaskDbUtils;
import com.example.tasklistapp.model.Task;

import java.util.Date;

/**
 * This activity needs to provide user interface to create new task or update and delete current one.
 */
public class TaskActivity extends AppCompatActivity implements DeadlinePickerDialog.DeadlineListener {
    public static final String EXTRA_TASK_ID = "com.example.tasklistapp.task_id";

    public static final String DEADLINE_DIALOG = "DeadlineDialog";
    public static final String DEADLINE_KEY = "DeadlineKey";

    public static final String TASK_ACTION = "task_action";
    public static final String NEW_TASK = "new_task";
    public static final String UPDATE_TASK = "update_task";
    public static String taskActivityAction;


    private EditText mTaskTitleField;
    private EditText mTaskDetailField;
    private TextView mTaskDeadlineView;
    private ImageButton mDeadlineSelectButton;


    private TaskDbUtils mTaskDbUtils;

    private Task mTask;

    public static Intent newIntent(Context packageContext, String id) {
        Intent intent = new Intent(packageContext, TaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        String id = getIntent().getStringExtra(EXTRA_TASK_ID);
        if (id != null) {
            mTask = TaskDbUtils.getUtils(this).getTask(id);
            taskActivityAction = UPDATE_TASK;
        } else {
            mTask = new Task();
            taskActivityAction = NEW_TASK;
        }


        mTaskTitleField = findViewById(R.id.task_title_field);
        mTaskTitleField.setText(mTask.getTitle());
        mTaskTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mTaskDetailField = findViewById(R.id.task_detail_field);
        mTaskDetailField.setText(mTask.getDescription());
        mTaskDetailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTaskDeadlineView = findViewById(R.id.task_deadline_view);
        if (mTask.getDeadline() != null) {
            mTaskDeadlineView.setText(DateFormat.getMediumDateFormat(this).format(mTask.getDeadline()));
        }

        mDeadlineSelectButton = findViewById(R.id.deadline_select_button);
        mDeadlineSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DeadlinePickerDialog deadlineDialog = new DeadlinePickerDialog();
                deadlineDialog.show(manager, DEADLINE_DIALOG);
            }
        });

        if (savedInstanceState != null) {
            Date savedDeadline = (Date) savedInstanceState.getSerializable(DEADLINE_KEY);
            mTask.setDeadline(savedDeadline);
            if (savedDeadline != null) {
                mTaskDeadlineView.setText(DateFormat.getMediumDateFormat(this).format(savedDeadline));
            }
            taskActivityAction = savedInstanceState.getString(TASK_ACTION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.task_save_item:
                if (taskActivityAction.equals(NEW_TASK)) {
                    saveNewTask();
                } else if (taskActivityAction.equals(UPDATE_TASK)) {
                    updateTask();
                }
                return true;
            case R.id.task_delete_item:
                deleteTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DEADLINE_KEY, mTask.getDeadline());
        outState.putString(TASK_ACTION, taskActivityAction);
    }

    @Override
    public void onDeadlineSelected(Date deadline) {
        mTaskDeadlineView.setText(DateFormat.getMediumDateFormat(this).format(deadline));
        mTask.setDeadline(deadline);
    }

    private void saveNewTask() {
        long newRowId = 0;

        if (mTask.getTitle() != null) {
            mTask.setCreationDate(new Date());
            if (mTask.getDeadline() == null) {
                mTask.setDeadline(new Date());
            }
            mTaskDbUtils = TaskDbUtils.getUtils(TaskActivity.this);
            newRowId = mTaskDbUtils.addNewTask(mTask);
        }
        if (newRowId == -1) {
            Toast.makeText(TaskActivity.this, "Error of adding new task", Toast.LENGTH_SHORT).show();
        }
        returnToList();
    }

    private void updateTask() {
        int updatedRows = 0;

        if (mTask.getTitle() != null) {
            mTaskDbUtils = TaskDbUtils.getUtils(TaskActivity.this);
            updatedRows = mTaskDbUtils.updateTask(mTask);
        }
        returnToList();
    }

    private void deleteTask() {
        int deletedRows = 0;

        if (mTask.getTitle() != null) {
            mTaskDbUtils = TaskDbUtils.getUtils(TaskActivity.this);
            deletedRows = mTaskDbUtils.deleteTask(mTask);
        }
        returnToList();
    }

    private void returnToList() {
        onBackPressed();
    }

    private boolean isValid() {
        //consider to use this method to check form before call database utils
        return false;
    }
}
