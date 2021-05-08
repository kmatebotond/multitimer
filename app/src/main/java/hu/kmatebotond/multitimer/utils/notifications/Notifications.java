package hu.kmatebotond.multitimer.utils.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.Timer;
import hu.kmatebotond.multitimer.timer.TimerData;
import hu.kmatebotond.multitimer.timer.TimerService;

public class Notifications {
    public static final String TIMER_NOTIFICATION_CHANNEL_ID = "timer_notification_channel_id";
    public static final String TIMER_FINISHED_NOTIFICATION_CHANNEL_ID = "timer_finished_notification_channel_id";

    public static final int TIMER_NOTIFICATION_ID = 1;
    public static final int TIMER_FINISHED_NOTIFICATION_ID = 2;

    public static void createNotificationChannels(Context context) {
        NotificationChannel timerNotificationChannel = new NotificationChannel(TIMER_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.timer_notification_channel), NotificationManager.IMPORTANCE_MIN);
        timerNotificationChannel.setDescription(context.getResources().getString(R.string.timer_notification_channel));

        NotificationChannel timerFinishedNotificationChannel = new NotificationChannel(TIMER_FINISHED_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.timer_finished_notification_channel), NotificationManager.IMPORTANCE_HIGH);
        timerFinishedNotificationChannel.setDescription(context.getResources().getString(R.string.timer_finished_notification_channel));
        timerFinishedNotificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(timerNotificationChannel);
        notificationManager.createNotificationChannel(timerFinishedNotificationChannel);
    }

    public static Notification getTimerNotification(Context context, TimerData timerData) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TIMER_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.outline_access_alarm_24);
        if (timerData == null) {
            builder.setContentTitle(context.getResources().getString(R.string.multiple_timers_notification_title));
        } else {
            builder.setContentTitle(Timer.convertToHoursMinutesSeconds(timerData.getTotalSeconds()));
            builder.setContentText(timerData.getTimerName());

            Intent deleteTimerIntent = new Intent();
            deleteTimerIntent.setAction(TimerService.Receiver.DELETE_TIMER_ACTION);
            PendingIntent deleteTimerPendingIntent = PendingIntent.getBroadcast(context, 0, deleteTimerIntent, 0);

            builder.addAction(R.drawable.outline_delete_24, context.getResources().getString(R.string.delete_timer_action_text), deleteTimerPendingIntent);
        }

        return builder.build();
    }

    public static void sendTimerNotification(Context context, Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(TIMER_NOTIFICATION_ID, notification);
    }

    public static void sendTimerFinishedNotification(Context context, TimerData timerData) {
        String timerName = timerData.getTimerName();
        String time = Timer.convertToHoursMinutesSeconds(timerData.getMaxSeconds());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TIMER_FINISHED_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.outline_access_alarm_24);
        builder.setContentTitle((timerName.equals("") ? "" : (timerName + " - ")) + time);
        builder.setContentText(context.getResources().getString(R.string.timer_finished));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(TIMER_FINISHED_NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context context, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }
}
