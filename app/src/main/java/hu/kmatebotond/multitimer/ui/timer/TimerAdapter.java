package hu.kmatebotond.multitimer.ui.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.TimerData;
import hu.kmatebotond.multitimer.timer.TimerService;
import hu.kmatebotond.multitimer.ui.activities.MainActivity;
import hu.kmatebotond.multitimer.timer.Timer;
import hu.kmatebotond.multitimer.ui.activities.SetTimerActivity;

public class TimerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TIMER_VIEW_TYPE = 1;
    public static final int ADD_TIMER_VIEW_TYPE = 2;

    private final Context context;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    private List<TimerData> timerDatas = new ArrayList<>();

    public TimerAdapter(Context context) {
        this.context = context;

        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.UPDATE_ACTION);
        context.registerReceiver(receiver, filter);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TIMER_VIEW_TYPE) {
            View view = inflater.inflate(R.layout.timer, parent, false);

            return new TimerViewHolder(view);
        } else if (viewType == ADD_TIMER_VIEW_TYPE) {
            View view = inflater.inflate(R.layout.add_timer, parent, false);

            return new AddTimerViewHolder(view);
        }

        throw new AssertionError();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TimerViewHolder) {
            TimerViewHolder timerViewHolder = (TimerViewHolder) holder;

            TimerData timerData = timerDatas.get(position);

            viewBinderHelper.bind(timerViewHolder.timerSwipeRevealLayout, timerData.getId() + "");

            timerViewHolder.deleteTimer.setOnClickListener(e -> {
                Intent intent = new Intent();
                intent.setAction(TimerService.Receiver.DELETE_TIMER_ACTION);
                intent.putExtra(TimerService.Receiver.DELETE_TIMER_ACTION_INDEX, position);

                context.sendBroadcast(intent);
            });

            if (timerData.isRunning()) {
                timerViewHolder.timerConstraintLayout.setAlpha(1);
            } else {
                timerViewHolder.timerConstraintLayout.setAlpha(0.33f);
            }

            timerViewHolder.timerName.setText(timerData.getTimerName());
            timerViewHolder.time.setText(Timer.convertToHoursMinutesSeconds(timerData.getTotalSeconds()));
            timerViewHolder.timeProgressBar.setMax(timerData.getMaxSeconds());
            timerViewHolder.timeProgressBar.setProgress(timerData.getMaxSeconds() - timerData.getTotalSeconds());

            timerViewHolder.pauseTimer.setOnClickListener(e -> {
                Intent intent = new Intent();

                if (timerData.isRunning()) {
                    intent.setAction(TimerService.Receiver.PAUSE_TIMER_ACTION);
                    intent.putExtra(TimerService.Receiver.PAUSE_TIMER_ACTION_INDEX, position);
                } else {
                    intent.setAction(TimerService.Receiver.START_TIMER_ACTION);
                    intent.putExtra(TimerService.Receiver.START_TIMER_ACTION_INDEX, position);
                }

                context.sendBroadcast(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return timerDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == timerDatas.size() ? ADD_TIMER_VIEW_TYPE : TIMER_VIEW_TYPE);
    }

    public class TimerViewHolder extends RecyclerView.ViewHolder {
        private final SwipeRevealLayout timerSwipeRevealLayout;
        private final ImageView deleteTimer;
        private final ConstraintLayout timerConstraintLayout;
        private final TextView timerName, time;
        private final ProgressBar timeProgressBar;
        private final ImageView pauseTimer;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);

            timerSwipeRevealLayout = itemView.findViewById(R.id.timerSwipeRevealLayout);
            deleteTimer = itemView.findViewById(R.id.deleteTimer);
            timerConstraintLayout = itemView.findViewById(R.id.timerConstraintLayout);
            timerName = itemView.findViewById(R.id.timerName);
            time = itemView.findViewById(R.id.time);
            timeProgressBar = itemView.findViewById(R.id.timerProgressBar);
            timeProgressBar.setMin(Timer.MIN);
            pauseTimer = itemView.findViewById(R.id.pauseTimer);
        }
    }

    public class AddTimerViewHolder extends RecyclerView.ViewHolder {
        public AddTimerViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageView addTimer = itemView.findViewById(R.id.addTimer);
            addTimer.setOnClickListener(e -> {
                Intent intent = new Intent(context, SetTimerActivity.class);
                ((MainActivity) context).startActivityForResult(intent, MainActivity.SET_TIMER_REQUEST_CODE);
            });
        }
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TimerService.UPDATE_ACTION)) {
                timerDatas = (List<TimerData>) intent.getSerializableExtra(TimerService.TIMER_DATAS);

                notifyDataSetChanged();
            }
        }
    }
}
