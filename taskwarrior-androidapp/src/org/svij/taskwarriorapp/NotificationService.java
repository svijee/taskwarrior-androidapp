package org.svij.taskwarriorapp;

import java.util.ArrayList;

import org.svij.taskwarriorapp.db.TaskDataSource;

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

		TaskDataSource datasource = new TaskDataSource(getApplicationContext());
		ArrayList<String> tasks = datasource.getDueTasks();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		if (prefs.getBoolean("notifications_due_task", true)) {

			if (tasks.size() > 0) {
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
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
				mBuilder.setAutoCancel(true);

				String alarms = prefs.getString(
						"notifications_due_task_ringtone", "default ringtone");
				Uri uri = Uri.parse(alarms);

				mBuilder.setSound(uri);

				if (prefs.getBoolean("notifications_due_task_vibrate", true)) {
					mBuilder.setDefaults(Notification.DEFAULT_ALL);
				} else {
					mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				}

				Intent resultIntent = new Intent(this, TasksActivity.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				stackBuilder.addParentStack(TasksActivity.class);
				stackBuilder.addNextIntent(resultIntent);

				PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

				mBuilder.setContentIntent(resultPendingIntent);

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				mNotificationManager.notify(0, mBuilder.build());

			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
