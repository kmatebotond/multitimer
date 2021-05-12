package hu.kmatebotond.multitimer.timer;

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

import hu.kmatebotond.multitimer.utils.notifications.Notifications;

public class TimerService extends Service {
    public static final String REQUEST_UPDATE_ALL_ACTION = "TimerService.REQUEST_UPDATE_ALL_ACTION";
    public static final String ADD_TIMER_ACTION = "TimerService.ADD_TIMER_ACTION";
    public static final String EDIT_TIMER_ACTION = "TimerService.EDIT_TIMER_ACTION";
    public static final String DELETE_TIMER_ACTION = "TimerService.DELETE_TIMER_ACTION";
    public static final String START_TIMER_ACTION = "TimerService.START_TIMER_ACTION";
    public static final String PAUSE_TIMER_ACTION = "TimerService.PAUSE_TIMER_ACTION";

    public static final String ON_UPDATE_ALL = "TimerService.ON_UPDATE_ALL";
    public static final String ON_UPDATE = "TimerService.ON_UPDATE";
    public static final String ON_TIMER_ADDED = "TimerService.ON_TIMER_ADDED";
    public static final String ON_TIMER_DELETED = "TimerService.ON_TIMER_DELETED";

    public static final String TIMER_INDEX_EXTRA = "TimerService.TIMER_INDEX_EXTRA";
    public static final String TIMER_DATA_EXTRA = "TimerService.TIMER_DATA_EXTRA";

    private final List<Timer> timers = new ArrayList<>();

    private final Receiver receiver = new Receiver();

    private final HandlerThread backgroundThread = new HandlerThread("TimerService.backgroundThread");
    private Handler backgroundHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(REQUEST_UPDATE_ALL_ACTION);
        filter.addAction(ADD_TIMER_ACTION);
        filter.addAction(EDIT_TIMER_ACTION);
        filter.addAction(DELETE_TIMER_ACTION);
        filter.addAction(START_TIMER_ACTION);
        filter.addAction(PAUSE_TIMER_ACTION);
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

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case REQUEST_UPDATE_ALL_ACTION: {
                    sendOnUpdateAllBroadcast();

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

                            stopForegroundIfTimersIsEmpty();
                            Notifications.sendTimerFinishedNotification(context, timerData);
                        }
                    });
                    timers.add(timer);
                    sendOnTimerAddedBroadcast(timerData);

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

                    stopForegroundIfTimersIsEmpty();

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
            }
        }

        private void sendOnUpdateAllBroadcast() {
            Intent onUpdateAll = new Intent();
            onUpdateAll.setAction(ON_UPDATE_ALL);
            List<TimerData> timerDatas = new ArrayList<>();
            for (Timer t : timers) {
                timerDatas.add(t.getTimerData());
            }
            onUpdateAll.putExtra(TIMER_DATA_EXTRA, (Serializable) timerDatas);
            sendBroadcast(onUpdateAll);
        }

        private void sendOnUpdateBroadcast(int index, TimerData timerData) {
            Intent onUpdateIntent = new Intent();
            onUpdateIntent.setAction(ON_UPDATE);
            onUpdateIntent.putExtra(TIMER_INDEX_EXTRA, index);
            onUpdateIntent.putExtra(TIMER_DATA_EXTRA, timerData);
            sendBroadcast(onUpdateIntent);
        }

        private void sendOnTimerAddedBroadcast(TimerData timerData) {
            Intent onAddTimer = new Intent();
            onAddTimer.setAction(ON_TIMER_ADDED);
            onAddTimer.putExtra(TIMER_DATA_EXTRA, timerData);
            sendBroadcast(onAddTimer);
        }

        private void sendOnTimerDeletedBroadcast(int index) {
            Intent onTimerDeleted = new Intent();
            onTimerDeleted.setAction(ON_TIMER_DELETED);
            onTimerDeleted.putExtra(TIMER_INDEX_EXTRA, index);
            sendBroadcast(onTimerDeleted);
        }

        private Notification getCorrectTimerNotification(Context context, TimerData timerData) {
            if (timers.size() > 1) {
                timerData = null;
            }

            return Notifications.getTimerNotification(context, timerData);
        }

        private void stopForegroundIfTimersIsEmpty() {
            if (timers.isEmpty()) {
                stopForeground(true);
            }
        }
    }
}
