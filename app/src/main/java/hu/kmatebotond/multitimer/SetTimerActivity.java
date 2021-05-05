package hu.kmatebotond.multitimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

public class SetTimerActivity extends AppCompatActivity {
    public static final int SET_TIMER_RESULT_CODE = 1;
    public static final String SET_TIMER_TIMER_NAME = "SET_TIMER_TIMER_NAME";
    public static final String SET_TIMER_TOTAL_SECONDS = "SET_TIMER_TOTAL_SECONDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timer);

        @SuppressLint("DefaultLocale")
        NumberPicker.Formatter formatter = value -> String.format("%02d", value);

        NumberPicker setHours = findViewById(R.id.setHours);
        setHours.setMinValue(Timer.MIN);
        setHours.setMaxValue(Timer.HOURS_MAX);
        setHours.setFormatter(formatter);
        NumberPicker setMinutes = findViewById(R.id.setMinutes);
        setMinutes.setMinValue(Timer.MIN);
        setMinutes.setMaxValue(Timer.MINUTES_MAX);
        setMinutes.setFormatter(formatter);
        NumberPicker setSeconds = findViewById(R.id.setSeconds);
        setSeconds.setMinValue(Timer.MIN);
        setSeconds.setMaxValue(Timer.SECONDS_MAX);
        setSeconds.setFormatter(formatter);

        Button setTimer = findViewById(R.id.setTimer);
        setTimer.setOnClickListener(e -> {
            EditText setTimerName = findViewById(R.id.setTimerName);
            int totalSeconds = setHours.getValue() * 60 * 60 + setMinutes.getValue() * 60 + setSeconds.getValue();

            Intent intent = new Intent();
            intent.putExtra(SET_TIMER_TIMER_NAME, setTimerName.getText().toString());
            intent.putExtra(SET_TIMER_TOTAL_SECONDS, totalSeconds);

            setResult(SET_TIMER_RESULT_CODE, intent);
            finish();
        });
    }
}
