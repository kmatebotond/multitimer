package hu.kmatebotond.multitimer.timer.data;

import java.io.Serializable;

public class TimerData implements Serializable {
    public final String timerName;
    public final int maxSeconds;
    public final int totalSeconds;

    public TimerData(String timerName, int maxSeconds, int totalSeconds) {
        this.timerName = timerName;
        this.maxSeconds = maxSeconds;
        this.totalSeconds = totalSeconds;
    }
}
