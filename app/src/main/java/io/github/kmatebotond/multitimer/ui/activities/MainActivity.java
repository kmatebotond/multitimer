package io.github.kmatebotond.multitimer.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.kmatebotond.multitimer.R;
import io.github.kmatebotond.multitimer.timer.TimerData;
import io.github.kmatebotond.multitimer.timer.TimerService;
import io.github.kmatebotond.multitimer.ui.timer.TimerAdapter;
import io.github.kmatebotond.multitimer.utils.notifications.Notifications;

public class MainActivity extends AppCompatActivity {
    private RecyclerView timers;
    private TimerAdapter adapter;

    private final Receiver receiver = new Receiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timers = findViewById(R.id.activityMain_timers);
        adapter = new TimerAdapter(this);
        timers.setAdapter(adapter);
        timers.setLayoutManager(new LinearLayoutManager(this));
        timers.setItemAnimator(null);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.ON_TIMER_ADDED);
        filter.addAction(TimerService.ON_UPDATE_ALL);
        filter.addAction(TimerService.ON_UPDATE);
        filter.addAction(TimerService.ON_TIMER_DELETED);
        filter.addAction(TimerService.TIMER_COUNT);
        registerReceiver(receiver, filter);

        startService(new Intent(this, TimerService.class));
        TimerService.sendRequestTimerCountActionBroadcast(this);

        Notifications.createNotificationChannels(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        timers.setVisibility(View.VISIBLE);
    }

    public void startSetTimerActivityForResult() {
        Intent setTimerRequest = new Intent(this, SetTimerActivity.class);
        startActivityForResult(setTimerRequest, 0);
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
                case TimerService.TIMER_COUNT: {
                    int count = intent.getIntExtra(TimerService.TIMER_COUNT_EXTRA, 0);
                    if (!SetTimerActivity.IS_RUNNING) {
                        if (count == 0) {
                            startSetTimerActivityForResult();
                        } else {
                            timers.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                }
            }
        }
    }
}