package io.github.kmatebotond.multitimer.timer;

import java.io.Serializable;

public class TimerData implements Serializable {
    private final int id;
    private String timerName;
    private int maxSeconds;
    private int totalSeconds;
    private boolean running;

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
    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }
    public int getMaxSeconds() {
        return maxSeconds;
    }
    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }
    public int getTotalSeconds() {
        return totalSeconds;
    }
    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
    public String getFormattedMaxSeconds() {
        return getFormattedTime(maxSeconds);
    }
    public String getFormattedTotalSeconds() {
        return getFormattedTime(totalSeconds);
    }
    private String getFormattedTime(int seconds) {
        int hours = seconds / 60 / 60;
        seconds -= hours * 60 * 60;
        int minutes = seconds / 60;
        seconds -= minutes * 60;

        String hoursString = String.format("%02d", hours);
        String minutesString = String.format("%02d", minutes);
        String secondsString = String.format("%02d", seconds);
        return hoursString + ":" + minutesString + ":" + secondsString;
    }
}
