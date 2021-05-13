package io.github.kmatebotond.multitimer.timer;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

public class Timer {
    public static final int MIN = 0;
    public static final int HOURS_MAX = 23;
    public static final int MINUTES_MAX = 59;
    public static final int SECONDS_MAX = 59;

    private final TimerData timerData;
    private CountDownTimer timer;

    private final List<TimerListener> listeners = new ArrayList<>();

    public Timer(TimerData timerData) {
        this.timerData = timerData;

        if (timerData.isRunning()) {
            start();
        }
    }

    public void start() {
        if (!timerData.isRunning()) {
            timer = new CountDownTimer(timerData.getTotalSeconds() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerData.setTotalSeconds((int) Math.round(millisUntilFinished / 1000.0));

                    for (TimerListener l : listeners) {
                        l.onTick();
                    }
                }

                @Override
                public void onFinish() {
                    for (TimerListener l : listeners) {
                        l.onFinish();
                    }
                }
            };
            timer.start();
            timerData.setRunning(true);
        }
    }

    public void pause() {
        if (timerData.isRunning()) {
            timer.cancel();
            timerData.setRunning(false);
        }
    }

    public TimerData getTimerData() {
        return timerData;
    }
    public void addTimerListener(TimerListener listener) {
        listeners.add(listener);
    }

    public interface TimerListener {
        void onTick();
        void onFinish();
    }
}
