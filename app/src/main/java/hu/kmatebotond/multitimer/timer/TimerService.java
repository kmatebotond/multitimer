package hu.kmatebotond.multitimer.timer;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.kmatebotond.multitimer.ui.activities.SetTimerActivity;
import hu.kmatebotond.multitimer.utils.notifications.Notifications;

public class TimerService extends Service {
    public static final String UPDATE_ACTION = "update_action";
    public static final String TIMER_DATAS = "timer_datas";

    private final List<Timer> timers = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();

        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.Receiver.ADD_TIMER_ACTION);
        filter.addAction(TimerService.Receiver.DELETE_TIMER_ACTION);
        filter.addAction(TimerService.Receiver.START_TIMER_ACTION);
        filter.addAction(TimerService.Receiver.PAUSE_TIMER_ACTION);
        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getCorrectTimerNotification(Context context, TimerData timerData) {
        Notification notification;
        if (timers.size() > 1) {
            notification = Notifications.getTimerNotification(context, null);
        } else {
            notification = Notifications.getTimerNotification(context, timerData);
        }

        return notification;
    }

    private void stopForegroundIfTimersIsEmpty() {
        if (timers.isEmpty()) {
            stopForeground(true);
        }
    }

    private void sendUpdateBroadcast() {
        Intent intent = new Intent();
        intent.setAction(UPDATE_ACTION);

        List<TimerData> timerDatas = new ArrayList<>();
        for (Timer t : timers) {
            timerDatas.add(t.getTimerData());
        }

        intent.putExtra(TIMER_DATAS, (Serializable) timerDatas);

        sendBroadcast(intent);
    }

    public class Receiver extends BroadcastReceiver {
        public static final String ADD_TIMER_ACTION = "add_timer_action";
        public static final String DELETE_TIMER_ACTION = "delete_timer_action";
        public static final String DELETE_TIMER_ACTION_INDEX = "delete_timer_action_index";
        public static final String START_TIMER_ACTION = "start_timer_action";
        public static final String START_TIMER_ACTION_INDEX = "start_timer_action_index";
        public static final String PAUSE_TIMER_ACTION = "pause_timer_action";
        public static final String PAUSE_TIMER_ACTION_INDEX = "pause_timer_action_index";

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ADD_TIMER_ACTION:
                    TimerData timerData = (TimerData) intent.getSerializableExtra(SetTimerActivity.TIMER_DATA);
                    Timer timer = new Timer(timerData);
                    timer.addTimerListener(new Timer.TimerListener() {
                        @Override
                        public void onTick() {
                            sendUpdateBroadcast();

                            Notifications.sendTimerNotification(context, getCorrectTimerNotification(context, timer.getTimerData()));
                        }

                        @Override
                        public void onFinish() {
                            timers.remove(timer);

                            sendUpdateBroadcast();

                            Notifications.sendTimerFinishedNotification(context, timerData);

                            stopForegroundIfTimersIsEmpty();
                        }
                    });
                    timers.add(timer);

                    startForeground(Notifications.TIMER_NOTIFICATION_ID, getCorrectTimerNotification(context, timerData));

                    timer.start();

                    break;
                case DELETE_TIMER_ACTION: {
                    int i = intent.getIntExtra(DELETE_TIMER_ACTION_INDEX, 0);

                    timers.get(i).pause();
                    timers.remove(i);

                    sendUpdateBroadcast();

                    stopForegroundIfTimersIsEmpty();

                    break;
                }
                case START_TIMER_ACTION: {
                    int i = intent.getIntExtra(START_TIMER_ACTION_INDEX, 0);

                    timers.get(i).start();

                    break;
                }
                case PAUSE_TIMER_ACTION: {
                    int i = intent.getIntExtra(PAUSE_TIMER_ACTION_INDEX, 0);

                    timers.get(i).pause();

                    sendUpdateBroadcast();

                    break;
                }
            }
        }
    }
}
