package io.github.kmatebotond.multitimer.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.github.kmatebotond.multitimer.R;
import io.github.kmatebotond.multitimer.timer.Timer;
import io.github.kmatebotond.multitimer.timer.TimerData;
import io.github.kmatebotond.multitimer.timer.TimerService;

public class SetTimerActivity extends AppCompatActivity {
    private static final String SET_HOURS_SAVED_INSTANCE_STATE = "SetTimerActivity.SET_HOURS_SAVED_INSTANCE_STATE";
    private static final String SET_MINUTES_SAVED_INSTANCE_STATE = "SetTimerActivity.SET_MINUTES_SAVED_INSTANCE_STATE";
    private static final String SET_SECONDS_SAVED_INSTANCE_STATE = "SetTimerActivity.SET_SECONDS_SAVED_INSTANCE_STATE";

    public static boolean IS_RUNNING = false;

    private NumberPicker setHours;
    private NumberPicker setMinutes;
    private NumberPicker setSeconds;
    private ImageView cancelSetTimer;

    private boolean canBack;

    private final Receiver receiver = new Receiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timer);

        IS_RUNNING = true;

        NumberPicker.Formatter formatter = value -> String.format("%02d", value);
        setHours = findViewById(R.id.activitySetTimer_setHours);
        setHours.setMinValue(Timer.MIN);
        setHours.setMaxValue(Timer.HOURS_MAX);
        setHours.setFormatter(formatter);
        setMinutes = findViewById(R.id.activitySetTimer_setMinutes);
        setMinutes.setMinValue(Timer.MIN);
        setMinutes.setMaxValue(Timer.MINUTES_MAX);
        setMinutes.setFormatter(formatter);
        setSeconds = findViewById(R.id.activitySetTimer_setSeconds);
        setSeconds.setMinValue(Timer.MIN);
        setSeconds.setMaxValue(Timer.SECONDS_MAX);
        setSeconds.setFormatter(formatter);

        cancelSetTimer = findViewById(R.id.activitySetTimer_cancelSetTimer);
        cancelSetTimer.setOnClickListener(v -> finish());

        ImageView setTimer = findViewById(R.id.activitySetTimer_setTimer);
        setTimer.setOnClickListener(v -> {
            int totalSeconds = setHours.getValue() * 60 * 60 + setMinutes.getValue() * 60 + setSeconds.getValue();
            if (totalSeconds != 0) {
                Intent addTimerAction = new Intent();
                addTimerAction.setAction(TimerService.ADD_TIMER_ACTION);
                EditText timerName = findViewById(R.id.activitySetTimer_timerName);
                String timerNameText = timerName.getText() + "";
                TimerData timerData = new TimerData(timerNameText, totalSeconds, totalSeconds, false);
                addTimerAction.putExtra(TimerService.TIMER_DATA_EXTRA, timerData);
                sendBroadcast(addTimerAction);
                finish();
            } else {
                Toast.makeText(this, R.string.timer_set_to_zero_text, Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.TIMER_COUNT);
        registerReceiver(receiver, filter);

        TimerService.sendRequestTimerCountActionBroadcast(this);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        setHours.setValue(savedInstanceState.getInt(SET_HOURS_SAVED_INSTANCE_STATE));
        setMinutes.setValue(savedInstanceState.getInt(SET_MINUTES_SAVED_INSTANCE_STATE));
        setSeconds.setValue(savedInstanceState.getInt(SET_SECONDS_SAVED_INSTANCE_STATE));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SET_HOURS_SAVED_INSTANCE_STATE, setHours.getValue());
        outState.putInt(SET_MINUTES_SAVED_INSTANCE_STATE, setMinutes.getValue());
        outState.putInt(SET_SECONDS_SAVED_INSTANCE_STATE, setSeconds.getValue());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            IS_RUNNING = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        TimerService.sendRequestTimerCountActionBroadcast(this);
        if (canBack) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.can_back_false_text, Toast.LENGTH_SHORT).show();
        }
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TimerService.TIMER_COUNT)) {
                int count = intent.getIntExtra(TimerService.TIMER_COUNT_EXTRA, 0);
                if (count == 0) {
                    cancelSetTimer.setVisibility(View.GONE);
                    canBack = false;
                } else {
                    canBack = true;
                }
            }
        }
    }
}
