package hu.kmatebotond.multitimer.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.TimerService;
import hu.kmatebotond.multitimer.ui.timer.TimerAdapter;
import hu.kmatebotond.multitimer.utils.notifications.Notifications;

public class MainActivity extends AppCompatActivity {
    public static final int SET_TIMER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Notifications.createNotificationChannels(this);

        RecyclerView timers = findViewById(R.id.timers);
        timers.setAdapter(new TimerAdapter(this));
        timers.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        startService(new Intent(this, TimerService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_TIMER_REQUEST_CODE) {
            if (resultCode == SetTimerActivity.SET_TIMER_RESULT_CODE) {
                Intent intent = new Intent();
                intent.setAction(TimerService.Receiver.ADD_TIMER_ACTION);
                intent.putExtras(data);

                sendBroadcast(intent);
            }
        }
    }
}