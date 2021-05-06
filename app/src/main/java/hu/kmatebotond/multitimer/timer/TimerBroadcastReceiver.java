package hu.kmatebotond.multitimer.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.io.IOException;

public class TimerBroadcastReceiver extends BroadcastReceiver {
    public static final String ALARM_ACTION = "alarm_action";
    public static final String FINISH_ACTION = "finish_action";

    private static final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        String action = intent.getAction();
        if (action.equals(ALARM_ACTION)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();

                vibrator.cancel();
            }

            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            try {
                mediaPlayer.setDataSource(context, alarmUri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();

            vibrator.vibrate(VibrationEffect.createWaveform(new long[] {0, 500, 500}, 0));
        } else if (action.equals(FINISH_ACTION)) {
            mediaPlayer.reset();

            vibrator.cancel();
        }
    }
}
