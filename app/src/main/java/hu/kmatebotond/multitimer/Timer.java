package hu.kmatebotond.multitimer;

import android.os.CountDownTimer;

public class Timer {
    public static final int MIN = 0;
    public static final int HOURS_MAX = 23;
    public static final int MINUTES_MAX = 59;
    public static final int SECONDS_MAX = 59;

    private String timerName = "";
    private int hours = MIN;
    private int minutes = MIN;
    private int seconds = MIN;
    private boolean running = false;

    private CountDownTimer timer;

    public void start(TimerAdapter adapter) {
        int totalSeconds = (hours * 60 * 60) + (minutes * 60) + seconds;

        timer = new CountDownTimer(totalSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsUntilFinished = (int) (millisUntilFinished / 1000);
                hours = secondsUntilFinished / 60 / 60;
                secondsUntilFinished -= hours * 60 * 60;
                minutes = secondsUntilFinished / 60;
                seconds = secondsUntilFinished - minutes * 60;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                running = false;

                adapter.notifyDataSetChanged();
            }
        }.start();
        running = true;

        adapter.notifyDataSetChanged();
    }
    public void stop(TimerAdapter adapter) {
        timer.cancel();
        running = false;

        adapter.notifyDataSetChanged();
    }

    public String getTimerName() {
        return timerName;
    }
    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }
    public int getHours() {
        return hours;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }
    public int getMinutes() {
        return minutes;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    public int getSeconds() {
        return seconds;
    }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
}
