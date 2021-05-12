package hu.kmatebotond.multitimer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.Timer;
import hu.kmatebotond.multitimer.timer.TimerData;
import hu.kmatebotond.multitimer.timer.TimerService;

public class SetTimerActivity extends AppCompatActivity {
    public static final int RESULT_CODE = 1;

    private static final String SET_HOURS_SAVED_INSTANCE_STATE = "SetTimerActivity.SET_HOURS_SAVED_INSTANCE_STATE";
    private static final String SET_MINUTES_SAVED_INSTANCE_STATE = "SetTimerActivity.SET_MINUTES_SAVED_INSTANCE_STATE";
    private static final String SET_SECONDS_SAVED_INSTANCE_STATE = "SetTimerActivity.SET_SECONDS_SAVED_INSTANCE_STATE";

    private NumberPicker setHours;
    private NumberPicker setMinutes;
    private NumberPicker setSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timer);

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

        ImageView cancelSetTimer = findViewById(R.id.activitySetTimer_cancelSetTimer);
        cancelSetTimer.setOnClickListener(v -> finish());

        ImageView setTimer = findViewById(R.id.activitySetTimer_setTimer);
        setTimer.setOnClickListener(v -> {
            int totalSeconds = setHours.getValue() * 60 * 60 + setMinutes.getValue() * 60 + setSeconds.getValue();
            if (totalSeconds != 0) {
                Intent result = new Intent();
                EditText timerName = findViewById(R.id.activitySetTimer_timerName);
                String timerNameText = timerName.getText() + "";
                result.putExtra(TimerService.TIMER_DATA_EXTRA, new TimerData(timerNameText, totalSeconds, totalSeconds, false));
                setResult(RESULT_CODE, result);
                finish();
            } else {
                Toast.makeText(this, R.string.timer_set_to_zero_text, Toast.LENGTH_SHORT).show();
            }
        });
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
}
