package com.example.tasklistapp.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tasklistapp.R;
import com.example.tasklistapp.TaskListActivity;
import com.example.tasklistapp.dbutils.TaskDbUtils;
import com.example.tasklistapp.model.Task;

import java.util.Calendar;
import java.util.List;


/**
 * this service needs to launch daily reminder concerning tasks for day
 * notifications scheduled every day at 9 a.m.
 */
public class TaskReminderService extends Service {
    private static final String TAG = "TaskReminderService";

    private static final int NOTIFICATION_ID = 888;
    private static final String CHANNEL_Id = "tasklistapp.services.001";

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            String message;
            List<Task> todayTasks = TaskDbUtils.getUtils(getApplicationContext()).getTodayTasks();
            Resources resources = getResources();
            int count = todayTasks.size();

            message = resources.getQuantityString(R.plurals.tasks_plural, count, count);

            createNotificationChannel();
            showNotification(message);

            stopSelf(msg.arg1);
        }
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, TaskReminderService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(System.currentTimeMillis());
        startDate.set(Calendar.HOUR_OF_DAY, 9);

        Intent i = TaskReminderService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    startDate.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void  createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "TasksNotification";
            String description = "today tasks notification chanel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_Id, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String notificationMessage) {
        Resources resources = getResources();
        Intent i = TaskListActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_Id)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.today_tasks_notification))
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}
