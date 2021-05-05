package hu.kmatebotond.multitimer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {
    private final Context context;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    private final List<Timer> timers = new ArrayList<>();

    public TimerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.timer, parent, false);

        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        Timer timer = timers.get(position);

        viewBinderHelper.bind(holder.timerSwipeRevealLayout, String.valueOf(System.identityHashCode(timer)));

        holder.deleteTimer.setOnClickListener(e -> {
            timer.stopAndRemove(this);
        });

        holder.timerName.setText(timer.getTimerName());
        holder.time.setText(Timer.convertToHoursMinutesSeconds(timer.getTotalSeconds()));
        holder.timeProgressBar.setMax(timer.getMaxSeconds());
        holder.timeProgressBar.setProgress(timer.getMaxSeconds() - timer.getTotalSeconds());

        holder.pauseTimer.setOnClickListener(e -> {
            if (timer.isRunning()) {
                timer.stop();
            } else {
                timer.start(context, this);
            }
        });

        if (timer.isNeverStarted()) {
            timer.start(context, this);
        }
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public List<Timer> getTimers() {
        return timers;
    }

    public class TimerViewHolder extends RecyclerView.ViewHolder {
        private final SwipeRevealLayout timerSwipeRevealLayout;
        private final ImageView deleteTimer;
        private final TextView timerName, time;
        private final ProgressBar timeProgressBar;
        private final ImageView pauseTimer;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);

            timerSwipeRevealLayout = itemView.findViewById(R.id.timerSwipeRevealLayout);
            deleteTimer = itemView.findViewById(R.id.deleteTimer);
            timerName = itemView.findViewById(R.id.timerName);
            time = itemView.findViewById(R.id.time);
            timeProgressBar = itemView.findViewById(R.id.timerProgressBar);
            timeProgressBar.setMin(0);
            pauseTimer = itemView.findViewById(R.id.pauseTimer);
        }
    }
}
