package hu.kmatebotond.multitimer.timer;

import android.content.Context;
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
import hu.kmatebotond.multitimer.activities.MainActivity;
import hu.kmatebotond.multitimer.timer.data.Timer;

public class TimerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TIMER_VIEW_TYPE = 1;
    public static final int ADD_TIMER_VIEW_TYPE = 2;

    private final Context context;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private final List<Timer> timers = new ArrayList<>();

    public TimerAdapter(Context context) {
        this.context = context;
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

            Timer timer = timers.get(position);

            viewBinderHelper.bind(timerViewHolder.timerSwipeRevealLayout, String.valueOf(System.identityHashCode(timer)));

            timerViewHolder.deleteTimer.setOnClickListener(e -> timer.stopAndRemove());

            if (timer.isRunning()) {
                timerViewHolder.timerConstraintLayout.setAlpha(1);
            } else {
                timerViewHolder.timerConstraintLayout.setAlpha(.33f);
            }

            timerViewHolder.timerName.setText(timer.getTimerName());
            timerViewHolder.time.setText(Timer.convertToHoursMinutesSeconds(timer.getTotalSeconds()));
            timerViewHolder.timeProgressBar.setMax(timer.getMaxSeconds());
            timerViewHolder.timeProgressBar.setProgress(timer.getMaxSeconds() - timer.getTotalSeconds());

            timerViewHolder.pauseTimer.setOnClickListener(e -> {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return timers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == timers.size() ? ADD_TIMER_VIEW_TYPE : TIMER_VIEW_TYPE);
    }

    public List<Timer> getTimers() {
        return timers;
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
            timeProgressBar.setMin(0);
            pauseTimer = itemView.findViewById(R.id.pauseTimer);
        }
    }

    public class AddTimerViewHolder extends RecyclerView.ViewHolder {
        public AddTimerViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageView addTimer = itemView.findViewById(R.id.addTimer);
            addTimer.setOnClickListener(e -> ((MainActivity) context).startSetTimerActivityForResult());
        }
    }
}
