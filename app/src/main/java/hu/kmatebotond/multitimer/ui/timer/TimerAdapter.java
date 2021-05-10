package hu.kmatebotond.multitimer.ui.timer;

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

import hu.kmatebotond.multitimer.R;
import hu.kmatebotond.multitimer.timer.Timer;
import hu.kmatebotond.multitimer.timer.TimerData;
import hu.kmatebotond.multitimer.timer.TimerService;
import hu.kmatebotond.multitimer.ui.activities.MainActivity;
import hu.kmatebotond.multitimer.ui.activities.SetTimerActivity;

public class TimerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TIMER_VIEW_TYPE = 1;
    public static final int ADD_TIMER_VIEW_TYPE = 2;

    private List<TimerData> timerDatas = new ArrayList<>();

    private final Context context;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public TimerAdapter(Context context) {
        this.context = context;

        Intent requestUpdateAllActionIntent = new Intent();
        requestUpdateAllActionIntent.setAction(TimerService.REQUEST_UPDATE_ALL_ACTION);
        context.sendBroadcast(requestUpdateAllActionIntent);
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
                Intent deleteTimerActionIntent = new Intent();
                deleteTimerActionIntent.setAction(TimerService.DELETE_TIMER_ACTION);
                deleteTimerActionIntent.putExtra(TimerService.TIMER_INDEX_EXTRA, timerDatas.indexOf(timerData));
                context.sendBroadcast(deleteTimerActionIntent);
            });

            if (timerData.isRunning()) {
                timerViewHolder.timerConstraintLayout.setAlpha(1);
            } else {
                timerViewHolder.timerConstraintLayout.setAlpha(0.33f);
            }

            timerViewHolder.timerName.setText(timerData.getTimerName());
            timerViewHolder.time.setText(timerData.getFormattedTotalSeconds());
            timerViewHolder.timeProgressBar.setMax(timerData.getMaxSeconds() - 1);
            timerViewHolder.timeProgressBar.setProgress(timerData.getMaxSeconds() - timerData.getTotalSeconds());

            timerViewHolder.pauseTimer.setOnClickListener(e -> {
                Intent startPauseTimerActionIntent = new Intent();

                if (timerData.isRunning()) {
                    startPauseTimerActionIntent.setAction(TimerService.PAUSE_TIMER_ACTION);
                } else {
                    startPauseTimerActionIntent.setAction(TimerService.START_TIMER_ACTION);
                }

                startPauseTimerActionIntent.putExtra(TimerService.TIMER_INDEX_EXTRA, timerDatas.indexOf(timerData));
                context.sendBroadcast(startPauseTimerActionIntent);
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
        private final SwipeRevealLayout timerSwipeRevealLayout;
        private final ImageView deleteTimer;
        private final ConstraintLayout timerConstraintLayout;
        private final TextView timerName;
        private final TextView time;
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
                Intent request = new Intent(context, SetTimerActivity.class);
                ((MainActivity) context).startActivityForResult(request, MainActivity.SET_TIMER_REQUEST_CODE);
            });
        }
    }
}
