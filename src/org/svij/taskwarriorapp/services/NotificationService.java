/**
 * taskwarrior for android – a task list manager
 *
 * Copyright (c) 2012-2014 Sujeevan Vijayakumaran
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * allcopies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * http://www.opensource.org/licenses/mit-license.php
 *
 */

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
