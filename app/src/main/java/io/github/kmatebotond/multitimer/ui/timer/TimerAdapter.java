package io.github.kmatebotond.multitimer.ui.timer;

import android.content.Context;
import android.content.Intent;
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

import io.github.kmatebotond.multitimer.R;
import io.github.kmatebotond.multitimer.timer.Timer;
import io.github.kmatebotond.multitimer.timer.TimerData;
import io.github.kmatebotond.multitimer.timer.TimerService;
import io.github.kmatebotond.multitimer.ui.activities.MainActivity;

public class TimerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TIMER_VIEW_TYPE = 1;
    public static final int ADD_TIMER_VIEW_TYPE = 2;

    private List<TimerData> timerDatas = new ArrayList<>();

    private final Context context;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public TimerAdapter(Context context) {
        this.context = context;

        Intent requestUpdateAllAction = new Intent();
        requestUpdateAllAction.setAction(TimerService.REQUEST_UPDATE_ALL_ACTION);
        context.sendBroadcast(requestUpdateAllAction);
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

            viewBinderHelper.bind(timerViewHolder.swipeRevealLayout, timerData.getId() + "");

            timerViewHolder.deleteTimer.setOnClickListener(v -> {
                Intent deleteTimerAction = new Intent();
                deleteTimerAction.setAction(TimerService.DELETE_TIMER_ACTION);
                deleteTimerAction.putExtra(TimerService.TIMER_INDEX_EXTRA, timerDatas.indexOf(timerData));
                context.sendBroadcast(deleteTimerAction);
            });

            if (timerData.isRunning()) {
                timerViewHolder.constraintLayout.setAlpha(1);
            } else {
                timerViewHolder.constraintLayout.setAlpha(0.33f);
            }

            timerViewHolder.timerName.setText(timerData.getTimerName());
            timerViewHolder.time.setText(timerData.getFormattedTotalSeconds());
            timerViewHolder.progressBar.setMax(timerData.getMaxSeconds());
            timerViewHolder.progressBar.setProgress(timerData.getMaxSeconds() - timerData.getTotalSeconds());

            if (timerData.isRunning()) {
                timerViewHolder.startPauseTimer.setImageResource(R.drawable.baseline_pause_24);
            } else {
                timerViewHolder.startPauseTimer.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            timerViewHolder.startPauseTimer.setOnClickListener(v -> {
                Intent startPauseTimerAction = new Intent();
                if (timerData.isRunning()) {
                    startPauseTimerAction.setAction(TimerService.PAUSE_TIMER_ACTION);
                } else {
                    startPauseTimerAction.setAction(TimerService.START_TIMER_ACTION);
                }
                startPauseTimerAction.putExtra(TimerService.TIMER_INDEX_EXTRA, timerDatas.indexOf(timerData));
                context.sendBroadcast(startPauseTimerAction);
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

    public List<TimerData> getTimerDatas() {
        return timerDatas;
    }
    public void setTimerDatas(List<TimerData> timerDatas) {
        this.timerDatas = timerDatas;
    }

    public class TimerViewHolder extends RecyclerView.ViewHolder {
        private final SwipeRevealLayout swipeRevealLayout;
        private final ImageView deleteTimer;
        private final ConstraintLayout constraintLayout;
        private final TextView timerName;
        private final TextView time;
        private final ProgressBar progressBar;
        private final ImageView startPauseTimer;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.timer_swipeRevealLayout);
            deleteTimer = itemView.findViewById(R.id.timer_deleteTimer);
            constraintLayout = itemView.findViewById(R.id.timer_constraintLayout);
            timerName = itemView.findViewById(R.id.timer_timerName);
            time = itemView.findViewById(R.id.timer_time);
            progressBar = itemView.findViewById(R.id.timer_progressBar);
            progressBar.setMin(Timer.MIN);
            startPauseTimer = itemView.findViewById(R.id.timer_startPauseTimer);
        }
    }

    public class AddTimerViewHolder extends RecyclerView.ViewHolder {
        public AddTimerViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageView addTimer = itemView.findViewById(R.id.addTimer_addTimer);
            addTimer.setOnClickListener(v -> ((MainActivity) context).startSetTimerActivityForResult());
        }
    }
}
