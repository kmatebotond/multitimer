package hu.kmatebotond.multitimer.timer;

import java.io.Serializable;

public class TimerData implements Serializable {
    private final int id;
    private final String timerName;
    private final int maxSeconds;
    private final int totalSeconds;
    private final boolean running;

    public TimerData(int id, String timerName, int maxSeconds, int totalSeconds, boolean running) {
        this.id = id;
        this.timerName = timerName;
        this.maxSeconds = maxSeconds;
        this.totalSeconds = totalSeconds;
        this.running = running;
    }

    public TimerData(String timerName, int maxSeconds, int totalSeconds, boolean running) {
        this.id = System.identityHashCode(this);
        this.timerName = timerName;
        this.maxSeconds = maxSeconds;
        this.totalSeconds = totalSeconds;
        this.running = running;
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
}
