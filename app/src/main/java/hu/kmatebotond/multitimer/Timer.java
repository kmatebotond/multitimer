package hu.kmatebotond.multitimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Timer {
    public static final int NOTIFICATION_ID = 1;

    public static final int MIN = 0;
    public static final int HOURS_MAX = 23;
    public static final int MINUTES_MAX = 59;
    public static final int SECONDS_MAX = 59;

    private final Timer self = this;

    private final String timerName;
    private final int maxSeconds;
    private int totalSeconds;
    private boolean running = false;
    private boolean neverStarted = true;

    private CountDownTimer timer;

    public Timer(String timerName, int totalSeconds) {
        this.timerName = timerName;

        maxSeconds = totalSeconds;
        this.totalSeconds = totalSeconds;
    }

    public void start(Context context, TimerAdapter adapter) {
        timer = new CountDownTimer((totalSeconds + (neverStarted ? 1 : 0)) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                totalSeconds = (int) (millisUntilFinished / 1000);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                createNotification(context);

                stopAndRemove(adapter);
            }
        }.start();
        running = true;
        neverStarted = false;
    }

    public void stop() {
        timer.cancel();
        running = false;
    }

    public void stopAndRemove(TimerAdapter adapter) {
        stop();

        adapter.getTimers().remove(self);
        adapter.notifyDataSetChanged();
    }

    private void createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.baseline_access_alarm_24);
        builder.setContentTitle((timerName.equals("") ? "" : (timerName + " - ")) + convertToHoursMinutesSeconds(maxSeconds));
        builder.setContentText(context.getResources().getString(R.string.timer_finished));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
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
    public boolean isRunning() {
        return running;
    }
    public boolean isNeverStarted() {
        return neverStarted;
    }
}
