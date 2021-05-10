package hu.kmatebotond.multitimer.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.TimerData;
import hu.kmatebotond.multitimer.timer.TimerService;
import hu.kmatebotond.multitimer.ui.timer.TimerAdapter;
import hu.kmatebotond.multitimer.utils.notifications.Notifications;

public class MainActivity extends AppCompatActivity {
    public static final int SET_TIMER_REQUEST_CODE = 1;

    private TimerAdapter adapter;

    private final Receiver receiver = new Receiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView timers = findViewById(R.id.timers);
        adapter = new TimerAdapter(this);
        timers.setAdapter(adapter);
        timers.setLayoutManager(new LinearLayoutManager(this));
        timers.setItemAnimator(null);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.ON_TIMER_ADDED);
        filter.addAction(TimerService.ON_UPDATE_ALL);
        filter.addAction(TimerService.ON_UPDATE);
        filter.addAction(TimerService.ON_TIMER_DELETED);
        registerReceiver(receiver, filter);

        Notifications.createNotificationChannels(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        startService(new Intent(this, TimerService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_TIMER_REQUEST_CODE) {
            if (resultCode == SetTimerActivity.SET_TIMER_RESULT_CODE) {
                Intent addTimerActionIntent = new Intent();
                addTimerActionIntent.setAction(TimerService.ADD_TIMER_ACTION);

                TimerData timerData = (TimerData) data.getSerializableExtra(SetTimerActivity.TIMER_DATA_EXTRA);
                addTimerActionIntent.putExtra(TimerService.TIMER_DATA_EXTRA, timerData);

                sendBroadcast(addTimerActionIntent);
            }
        }
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case TimerService.ON_UPDATE_ALL: {
                    List<TimerData> timerDatas = (List<TimerData>) intent.getSerializableExtra(TimerService.TIMER_DATA_EXTRA);
                    adapter.setTimerDatas(timerDatas);
                    adapter.notifyDataSetChanged();

                    break;
                }
                case TimerService.ON_UPDATE: {
                    int i = intent.getIntExtra(TimerService.TIMER_INDEX_EXTRA, 0);
                    TimerData timerData = (TimerData) intent.getSerializableExtra(TimerService.TIMER_DATA_EXTRA);
                    adapter.getTimerDatas().set(i, timerData);
                    adapter.notifyItemChanged(i);

                    break;
                }
                case TimerService.ON_TIMER_ADDED: {
                    TimerData timerData = (TimerData) intent.getSerializableExtra(TimerService.TIMER_DATA_EXTRA);
                    List<TimerData> timerDatas = adapter.getTimerDatas();
                    timerDatas.add(timerData);
                    adapter.notifyItemInserted(timerDatas.size() - 1);

                    break;
                }
                case TimerService.ON_TIMER_DELETED: {
                    int i = intent.getIntExtra(TimerService.TIMER_INDEX_EXTRA, 0);
                    adapter.getTimerDatas().remove(i);
                    adapter.notifyItemRemoved(i);

                    break;
                }
            }
        }
    }
}