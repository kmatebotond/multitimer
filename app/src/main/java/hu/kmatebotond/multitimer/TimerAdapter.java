package hu.kmatebotond.multitimer;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {
    private final Context context;
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

        holder.getTimerName().setText(timer.getTimerName());
        holder.getTimerName().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                timer.setTimerName(s.toString());
            }
        });

        holder.getHours().setMinValue(Timer.MIN);
        holder.getHours().setMaxValue(Timer.HOURS_MAX);
        holder.getHours().setValue(timer.getHours());
        holder.getHours().setOnValueChangedListener((picker, oldVal, newVal) -> {
            timer.setHours(newVal);
        });

        holder.getMinutes().setMinValue(Timer.MIN);
        holder.getMinutes().setMaxValue(Timer.MINUTES_MAX);
        holder.getMinutes().setValue(timer.getMinutes());
        holder.getMinutes().setOnValueChangedListener((picker, oldVal, newVal) -> {
            timer.setMinutes(newVal);
        });

        holder.getSeconds().setMinValue(Timer.MIN);
        holder.getSeconds().setMaxValue(Timer.SECONDS_MAX);
        holder.getSeconds().setValue(timer.getSeconds());
        holder.getSeconds().setOnValueChangedListener((picker, oldVal, newVal) -> {
            timer.setSeconds(newVal);
        });

        if (timer.isRunning()) {
            holder.getStartStop().setText(R.string.stop);
        } else {
            holder.getStartStop().setText(R.string.start);
        }
        holder.getStartStop().setOnClickListener(e -> {
            if (timer.isRunning()) {
                timer.stop(this);
            } else {
                timer.start(this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public List<Timer> getTimers() {
        return timers;
    }

    public class TimerViewHolder extends RecyclerView.ViewHolder {
        private final EditText timerName;
        private final NumberPicker hours, minutes, seconds;
        private final Button startStop;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);

            timerName = itemView.findViewById(R.id.timerName);
            hours = itemView.findViewById(R.id.hours);
            minutes = itemView.findViewById(R.id.minutes);
            seconds = itemView.findViewById(R.id.seconds);
            startStop = itemView.findViewById(R.id.startStop);
        }

        public EditText getTimerName() {
            return timerName;
        }
        public NumberPicker getHours() {
            return hours;
        }
        public NumberPicker getMinutes() {
            return minutes;
        }
        public NumberPicker getSeconds() {
            return seconds;
        }
        public Button getStartStop() {
            return startStop;
        }
    }
}
