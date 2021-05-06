package hu.kmatebotond.multitimer.timer.data;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.activities.MainActivity;
import hu.kmatebotond.multitimer.timer.TimerAdapter;
import hu.kmatebotond.multitimer.timer.TimerBroadcastReceiver;

public class Timer {
    public static final int NOTIFICATION_ID = 1;

    public static final int MIN = 0;
    public static final int HOURS_MAX = 23;
    public static final int MINUTES_MAX = 59;
    public static final int SECONDS_MAX = 59;

    private final String timerName;
    private final int maxSeconds;
    private int totalSeconds;
    private boolean running = false;

    private CountDownTimer timer;

    private final Context context;
    private final TimerAdapter adapter;

    public Timer(TimerData data, Context context, TimerAdapter adapter) {
        this.timerName = data.timerName;
        this.maxSeconds = data.maxSeconds;
        this.totalSeconds = data.totalSeconds;

        this.context = context;
        this.adapter = adapter;

        start();
    }

    public void start() {
        if (timer != null) {
            timer.cancel(); // just in case...
        }
        timer = new CountDownTimer((totalSeconds + (maxSeconds == totalSeconds ? 1 : 0)) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                totalSeconds = (int) (millisUntilFinished / 1000);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                createNotification(context);

                stopAndRemove();
            }
        }.start();
        running = true;
    }

    public void stop() {
        timer.cancel();
        running = false;

        adapter.notifyDataSetChanged();
    }

    public void stopAndRemove() {
        adapter.getTimers().remove(this);

        stop();
    }

    private void createNotification(Context context) {
        Intent alarmIntent = new Intent(context, TimerBroadcastReceiver.class);
        alarmIntent.setAction(TimerBroadcastReceiver.ALARM_ACTION);
        context.sendBroadcast(alarmIntent);

        Intent finishIntent = new Intent(context, TimerBroadcastReceiver.class);
        finishIntent.setAction(TimerBroadcastReceiver.FINISH_ACTION);
        PendingIntent finishPendingIntent = PendingIntent.getBroadcast(context, 0, finishIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.baseline_access_alarm_24);
        builder.setContentTitle((timerName.equals("") ? "" : (timerName + " - ")) + convertToHoursMinutesSeconds(maxSeconds));
        builder.setContentText(context.getResources().getString(R.string.timer_finished));
        builder.setDeleteIntent(finishPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public TimerData getTimerData() {
        return new TimerData(timerName, maxSeconds, totalSeconds);
    }

    public static String convertToHoursMinutesSeconds(int seconds) {
        int hours = seconds / 60 / 60;
        seconds -= hours * 60 * 60;
        int minutes = seconds / 60;
        seconds -= minutes * 60;

        @SuppressLint("DefaultLocale")
        String hoursString = String.format("%02d", hours);
        @SuppressLint("DefaultLocale")
        String minutesString = String.format("%02d", minutes);
        @SuppressLint("DefaultLocale")
        String secondsString = String.format("%02d", seconds);

        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    public String getTimerName() {
        return timerName;
    }
    public int getMaxSeconds() {
        return maxSeconds;
    }
    public int getTotalSeconds() {
        return totalSeconds;
    }
    public boolean isRunning() {
        return running;
    }
}
