package hu.kmatebotond.multitimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = MainActivity.class.toString();

    public static final int SET_TIMER_REQUEST_CODE = 1;

    private RecyclerView timers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Button addTimer = findViewById(R.id.addTimer);
        addTimer.setOnClickListener(e -> {
            Intent intent = new Intent(this, SetTimerActivity.class);
            startActivityForResult(intent, SET_TIMER_REQUEST_CODE);
        });

        timers = findViewById(R.id.timers);
        timers.setAdapter(new TimerAdapter(this));
        timers.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_TIMER_REQUEST_CODE) {
            if (resultCode == SetTimerActivity.SET_TIMER_RESULT_CODE) {
                String timerName = data.getExtras().getString(SetTimerActivity.SET_TIMER_TIMER_NAME);
                int totalSeconds = data.getExtras().getInt(SetTimerActivity.SET_TIMER_TOTAL_SECONDS);

                TimerAdapter adapter = (TimerAdapter) timers.getAdapter();
                adapter.getTimers().add(new Timer(timerName, totalSeconds));

                adapter.notifyDataSetChanged();
            }
        }
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(getResources().getString(R.string.app_name));

        AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();
        audioAttributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        audioAttributesBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION);

        notificationChannel.setSound(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM), audioAttributesBuilder.build());
        notificationChannel.setVibrationPattern(new long[] {0, 500, 500, 500, 500, 500, 500, 500, 500});

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}