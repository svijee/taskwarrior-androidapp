package org.svij.taskwarriorapp.services;

import java.util.ArrayList;

import org.svij.taskwarriorapp.R;
import org.svij.taskwarriorapp.activities.TasksActivity;
import org.svij.taskwarriorapp.db.TaskDatabase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationService extends Service {

	public int onStartCommand(Intent intent, int flags, int startId) {

		TaskDatabase data = new TaskDatabase(getApplicationContext());
		ArrayList<String> tasks = data.getDueTasks();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		if (prefs.getBoolean("notifications_due_task", true)) {

			if (tasks.size() > 0) {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(
						this)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(
								tasks.size()
										+ getResources().getString(
												R.string.notification_title))
						.setContentText(
								tasks.size()
										+ getResources()
												.getString(
														R.string.notification_content_text));
				builder.setAutoCancel(true);

				String alarms = prefs.getString(
						"notifications_due_task_ringtone", "default ringtone");
				Uri uri = Uri.parse(alarms);

				builder.setSound(uri);

				if (prefs.getBoolean("notifications_due_task_vibrate", true)) {
					builder.setDefaults(Notification.DEFAULT_ALL);
				} else {
					builder.setDefaults(Notification.DEFAULT_SOUND);
				}

				Intent resultIntent = new Intent(this, TasksActivity.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				stackBuilder.addParentStack(TasksActivity.class);
				stackBuilder.addNextIntent(resultIntent);

				PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

				builder.setContentIntent(resultPendingIntent);

				NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(0, builder.build());
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
