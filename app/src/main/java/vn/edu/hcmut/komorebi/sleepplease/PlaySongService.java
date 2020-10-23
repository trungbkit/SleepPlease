package vn.edu.hcmut.komorebi.sleepplease;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlaySongService extends Service {
    private MediaPlayer mediaPlayer;
    public static int id_music;
    public PlaySongService() {
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mediaPlayer = id_music == 1 ? MediaPlayer.create(getApplicationContext(), R.raw.banana_minion) :
                MediaPlayer.create(getApplicationContext(), R.raw.kisstherain);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mediaPlayer.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
}
