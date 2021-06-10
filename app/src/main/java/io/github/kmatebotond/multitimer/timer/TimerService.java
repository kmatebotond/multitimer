package io.github.kmatebotond.multitimer.timer;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.kmatebotond.multitimer.utils.notifications.Notifications;

public class TimerService extends Service {
    public static final String REQUEST_UPDATE_ALL_ACTION = "TimerService.REQUEST_UPDATE_ALL_ACTION";
    public static final String ADD_TIMER_ACTION = "TimerService.ADD_TIMER_ACTION";
    public static final String EDIT_TIMER_ACTION = "TimerService.EDIT_TIMER_ACTION";
    public static final String DELETE_TIMER_ACTION = "TimerService.DELETE_TIMER_ACTION";
    public static final String START_TIMER_ACTION = "TimerService.START_TIMER_ACTION";
    public static final String PAUSE_TIMER_ACTION = "TimerService.PAUSE_TIMER_ACTION";
    public static final String REQUEST_TIMER_COUNT_ACTION = "TimerService.REQUEST_TIMER_COUNT_ACTION";

    public static final String ON_UPDATE_ALL = "TimerService.ON_UPDATE_ALL";
    public static final String ON_UPDATE = "TimerService.ON_UPDATE";
    public static final String ON_TIMER_ADDED = "TimerService.ON_TIMER_ADDED";
    public static final String ON_TIMER_DELETED = "TimerService.ON_TIMER_DELETED";
    public static final String TIMER_COUNT = "TimerService.TIMER_COUNT";

    public static final String TIMER_INDEX_EXTRA = "TimerService.TIMER_INDEX_EXTRA";
    public static final String TIMER_DATA_EXTRA = "TimerService.TIMER_DATA_EXTRA";
    public static final String TIMER_COUNT_EXTRA = "TimerService.TIMER_COUNT_EXTRA";

    private final List<Timer> timers = new ArrayList<>();

    private Receiver receiver;

    private final HandlerThread backgroundThread = new HandlerThread("TimerService.backgroundThread");
    private Handler backgroundHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(REQUEST_UPDATE_ALL_ACTION);
        filter.addAction(ADD_TIMER_ACTION);
        filter.addAction(EDIT_TIMER_ACTION);
        filter.addAction(DELETE_TIMER_ACTION);
        filter.addAction(START_TIMER_ACTION);
        filter.addAction(PAUSE_TIMER_ACTION);
        filter.addAction(REQUEST_TIMER_COUNT_ACTION);
        registerReceiver(receiver, filter);

        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);

        backgroundHandler.getLooper().quit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void sendRequestTimerCountActionBroadcast(Context context) {
        Intent requestTimerCountAction = new Intent();
        requestTimerCountAction.setAction(TimerService.REQUEST_TIMER_COUNT_ACTION);
        context.sendBroadcast(requestTimerCountAction);
    }

    private class Receiver extends BroadcastReceiver {
        public Receiver() {
            sendTimerCountBroadcast();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case REQUEST_UPDATE_ALL_ACTION: {
                    Intent onUpdateAll = new Intent();
                    onUpdateAll.setAction(ON_UPDATE_ALL);
                    List<TimerData> timerDatas = new ArrayList<>();
                    for (Timer t : timers) {
                        timerDatas.add(t.getTimerData());
                    }
                    onUpdateAll.putExtra(TIMER_DATA_EXTRA, (Serializable) timerDatas);
                    sendBroadcast(onUpdateAll);

                    break;
                }
                case ADD_TIMER_ACTION: {
                    TimerData timerData = (TimerData) intent.getSerializableExtra(TIMER_DATA_EXTRA);
                    Timer timer = new Timer(timerData);
                    timer.addTimerListener(new Timer.TimerListener() {
                        @Override
                        public void onTick() {
                            TimerData onTickTimerData = timer.getTimerData();
                            sendOnUpdateBroadcast(timers.indexOf(timer), onTickTimerData);

                            Notifications.sendTimerNotification(context, getCorrectTimerNotification(context, onTickTimerData));
                        }

                        @Override
                        public void onFinish() {
                            int i = timers.indexOf(timer);
                            timers.remove(i);
                            sendOnTimerDeletedBroadcast(i);
                            sendTimerCountBroadcast();

                            Notifications.sendTimerFinishedNotification(context, timerData);
                        }
                    });
                    timers.add(timer);
                    Intent onAddTimer = new Intent();
                    onAddTimer.setAction(ON_TIMER_ADDED);
                    onAddTimer.putExtra(TIMER_DATA_EXTRA, timerData);
                    sendBroadcast(onAddTimer);
                    sendTimerCountBroadcast();

                    backgroundHandler.post(timer::start);

                    startForeground(Notifications.TIMER_NOTIFICATION_ID, getCorrectTimerNotification(context, timerData));

                    break;
                }
                case EDIT_TIMER_ACTION : {
                    int i = intent.getIntExtra(TIMER_INDEX_EXTRA, 0);
                    Timer timer = timers.get(i);
                    TimerData timerData = (TimerData) intent.getSerializableExtra(TIMER_DATA_EXTRA);
                    timer.getTimerData().setTimerName(timerData.getTimerName());
                    sendOnUpdateBroadcast(i, timer.getTimerData());

                    break;
                }
                case DELETE_TIMER_ACTION: {
                    int i = intent.getIntExtra(TIMER_INDEX_EXTRA, 0);
                    timers.get(i).pause();
                    timers.remove(i);
                    sendOnTimerDeletedBroadcast(i);
                    sendTimerCountBroadcast();

                    break;
                }
                case START_TIMER_ACTION: {
                    int i = intent.getIntExtra(TIMER_INDEX_EXTRA, 0);
                    timers.get(i).start();

                    break;
                }
                case PAUSE_TIMER_ACTION: {
                    int i = intent.getIntExtra(TIMER_INDEX_EXTRA, 0);
                    Timer timer = timers.get(i);
                    timer.pause();
                    sendOnUpdateBroadcast(i, timer.getTimerData());

                    break;
                }
                case REQUEST_TIMER_COUNT_ACTION: {
                    sendTimerCountBroadcast();

                    break;
                }
            }
        }

        private void sendOnUpdateBroadcast(int index, TimerData timerData) {
            Intent onUpdate = new Intent();
            onUpdate.setAction(ON_UPDATE);
            onUpdate.putExtra(TIMER_INDEX_EXTRA, index);
            onUpdate.putExtra(TIMER_DATA_EXTRA, timerData);
            sendBroadcast(onUpdate);
        }

        private void sendOnTimerDeletedBroadcast(int index) {
            Intent onTimerDeleted = new Intent();
            onTimerDeleted.setAction(ON_TIMER_DELETED);
            onTimerDeleted.putExtra(TIMER_INDEX_EXTRA, index);
            sendBroadcast(onTimerDeleted);

            if (timers.isEmpty()) {
                stopForeground(true);
            }
        }

        private void sendTimerCountBroadcast() {
            Intent timerCount = new Intent();
            timerCount.setAction(TIMER_COUNT);
            timerCount.putExtra(TIMER_COUNT_EXTRA, timers.size());
            sendBroadcast(timerCount);
        }

        private Notification getCorrectTimerNotification(Context context, TimerData timerData) {
            if (timers.size() > 1) {
                timerData = null;
            }
            return Notifications.getTimerNotification(context, timerData);
        }
    }
}
