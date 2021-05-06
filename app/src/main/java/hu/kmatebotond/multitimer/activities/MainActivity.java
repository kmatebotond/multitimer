package hu.kmatebotond.multitimer.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.TimerAdapter;
import hu.kmatebotond.multitimer.timer.data.Timer;
import hu.kmatebotond.multitimer.timer.data.TimerData;

public class MainActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = MainActivity.class.toString();

    public static final int SET_TIMER_REQUEST_CODE = 1;

    private RecyclerView timers;
    private TimerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        timers = findViewById(R.id.timers);
        adapter = new TimerAdapter(this);
        timers.setAdapter(adapter);
        timers.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_TIMER_REQUEST_CODE) {
            if (resultCode == SetTimerActivity.SET_TIMER_RESULT_CODE) {
                TimerData timerData = (TimerData) data.getExtras().getSerializable(SetTimerActivity.TIMER_DATA);
                adapter.getTimers().add(new Timer(timerData, this, adapter));
            }
        }
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(getResources().getString(R.string.app_name));

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public void startSetTimerActivityForResult() {
        Intent intent = new Intent(this, SetTimerActivity.class);
        startActivityForResult(intent, SET_TIMER_REQUEST_CODE);
    }
}