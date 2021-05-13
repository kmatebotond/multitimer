package io.github.kmatebotond.multitimer.utils.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import io.github.kmatebotond.multitimer.R;
import io.github.kmatebotond.multitimer.timer.TimerData;
import io.github.kmatebotond.multitimer.timer.TimerService;

public class Notifications {
    private static final String TIMER_FINISHED_NOTIFICATION_CLEARED_ACTION = "Notifications.TIMER_FINISHED_NOTIFICATION_CLEARED_ACTION";

    public static final String TIMER_NOTIFICATION_CHANNEL_ID = "Notifications.TIMER_NOTIFICATION_CHANNEL_ID";
    public static final String TIMER_FINISHED_NOTIFICATION_CHANNEL_ID = "Notifications.TIMER_FINISHED_NOTIFICATION_CHANNEL_ID";

    public static final int TIMER_NOTIFICATION_ID = 1;
    public static final int TIMER_FINISHED_NOTIFICATION_ID = 2;

    public static Ringtone ringtone;

    public static void createNotificationChannels(Context context) {
        NotificationChannel timerNotificationChannel = new NotificationChannel(TIMER_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.timer_notification_channel), NotificationManager.IMPORTANCE_MIN);
        timerNotificationChannel.setDescription(context.getResources().getString(R.string.timer_notification_channel));

        NotificationChannel timerFinishedNotificationChannel = new NotificationChannel(TIMER_FINISHED_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.timer_finished_notification_channel), NotificationManager.IMPORTANCE_HIGH);
        timerFinishedNotificationChannel.setDescription(context.getResources().getString(R.string.timer_finished_notification_channel));
        timerFinishedNotificationChannel.setSound(null, null);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(timerNotificationChannel);
        notificationManager.createNotificationChannel(timerFinishedNotificationChannel);

        Uri ringtoneUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.happy_bells);
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TIMER_FINISHED_NOTIFICATION_CLEARED_ACTION);
        context.registerReceiver(new Receiver(), filter);
    }

    public static Notification getTimerNotification(Context context, TimerData timerData) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TIMER_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.outline_hourglass_bottom_24);
        if (timerData == null) {
            builder.setContentTitle(context.getResources().getString(R.string.multiple_timers_notification_title));
        } else {
            builder.setContentTitle(timerData.getFormattedTotalSeconds());
            builder.setContentText(timerData.getTimerName());
            Intent deleteTimerAction = new Intent();
            deleteTimerAction.setAction(TimerService.DELETE_TIMER_ACTION);
            deleteTimerAction.putExtra(TimerService.TIMER_INDEX_EXTRA, 0);
            builder.addAction(R.drawable.outline_delete_24, context.getResources().getString(R.string.delete_timer_action_text), PendingIntent.getBroadcast(context, 0, deleteTimerAction, 0));
        }

        return builder.build();
    }

    public static void sendTimerNotification(Context context, Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(TIMER_NOTIFICATION_ID, notification);
    }

    public static void sendTimerFinishedNotification(Context context, TimerData timerData) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TIMER_FINISHED_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.outline_hourglass_bottom_24);
        String timerName = timerData.getTimerName();
        String maxSeconds = timerData.getFormattedMaxSeconds();
        builder.setContentTitle((timerName.isEmpty() ? "" : (timerName + " - ")) + maxSeconds);
        builder.setContentText(context.getResources().getString(R.string.timer_finished_notification_text));
        Intent timerFinishedNotificationClearedAction = new Intent();
        timerFinishedNotificationClearedAction.setAction(TIMER_FINISHED_NOTIFICATION_CLEARED_ACTION);
        builder.setDeleteIntent(PendingIntent.getBroadcast(context, 0, timerFinishedNotificationClearedAction, 0));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(TIMER_FINISHED_NOTIFICATION_ID, builder.build());

        ringtone.stop();
        ringtone.play();
    }

    public static void cancelNotification(Context context, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }

    private static class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TIMER_FINISHED_NOTIFICATION_CLEARED_ACTION)) {
                ringtone.stop();
            }
        }
    }
}
