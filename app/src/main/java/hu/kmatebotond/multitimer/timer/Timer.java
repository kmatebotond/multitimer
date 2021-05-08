package hu.kmatebotond.multitimer.timer;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

public class Timer {
    public static final int MIN = 0;
    public static final int HOURS_MAX = 23;
    public static final int MINUTES_MAX = 59;
    public static final int SECONDS_MAX = 59;

    private final int id;
    private final String timerName;
    private final int maxSeconds;
    private int totalSeconds;
    private boolean running;

    private CountDownTimer timer;

    private final List<TimerListener> listeners = new ArrayList<>();

    public Timer(TimerData timerData) {
        this.id = timerData.getId();
        this.timerName = timerData.getTimerName();
        this.maxSeconds = timerData.getMaxSeconds();
        this.totalSeconds = timerData.getTotalSeconds();
        this.running = timerData.isRunning();

        if (running) {
            start();
        }
    }

    public void start() {
        if (!running) {
            timer = new CountDownTimer((totalSeconds + (maxSeconds == totalSeconds ? 1 : 0)) * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    totalSeconds = (int) (millisUntilFinished / 1000);

                    for (TimerListener l : listeners) {
                        l.onTick();
                    }
                }

                @Override
                public void onFinish() {
                    for (TimerListener l : listeners) {
                        l.onFinish();
                    };
                }
            };
            timer.start();
            running = true;
        }
    }

    public void pause() {
        if (running) {
            timer.cancel();
            running = false;
        }
    }

    public TimerData getTimerData() {
        return new TimerData(id, timerName, maxSeconds, totalSeconds, running);
    }
    public int getId() {
        return id;
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
    public void addTimerListener(TimerListener listener) {
        listeners.add(listener);
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

    public interface TimerListener {
        void onTick();
        void onFinish();
    }
}
