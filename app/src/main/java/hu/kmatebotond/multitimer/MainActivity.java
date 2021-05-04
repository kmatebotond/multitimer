package hu.kmatebotond.multitimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private RecyclerView timers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTimer = findViewById(R.id.addTimer);
        addTimer.setOnClickListener(e -> {
            TimerAdapter adapter = (TimerAdapter) timers.getAdapter();
            if (adapter != null) {
                adapter.getTimers().add(new Timer());
                adapter.notifyDataSetChanged();
            }
        });

        timers = findViewById(R.id.timers);
        timers.setAdapter(new TimerAdapter(this));
        timers.setLayoutManager(new LinearLayoutManager(this));
    }
}