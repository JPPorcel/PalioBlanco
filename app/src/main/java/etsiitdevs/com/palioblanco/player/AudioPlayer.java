package etsiitdevs.com.palioblanco.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import etsiitdevs.com.palioblanco.SessionManager;
import etsiitdevs.com.palioblanco.api.Api;
import etsiitdevs.com.palioblanco.api.Marcha;
import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioListNullPointerException;

/**
 * Created by jean on 12/07/16.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpblo on 16/06/17.
 */

public class AudioPlayer
{

    private List<Marcha> playlist;
    private int currentMarcha;
    private PlayerService playerService;
    private Status.PlayState state;
    private NotificationPlayerService notificationPlayer;
    private List<PlayerServiceListener> playerServiceListeners;
    private List<PlayerStatusListener> playerStatusListeners;
    private PlayerServiceListener notificationListener;
    private Context context;


    private static AudioPlayer instance = null;
    public static AudioPlayer getInstance(Context context)
    {
        if(instance == null)
            instance = new AudioPlayer(context);
        return instance;
    }

    private AudioPlayer(Context context)
    {
        playlist = new ArrayList<>();
        currentMarcha = -1;
        state = Status.PlayState.STOP;
        this.notificationPlayer = new NotificationPlayerService(context);
        playerServiceListeners = new ArrayList<>();
        playerStatusListeners = new ArrayList<>();
        this.context = context;

        // bind service
        Intent intent = new Intent(context.getApplicationContext(), PlayerService.class);
        intent.putExtra(NotificationPlayerService.PLAYLIST, (Serializable) playlist);
        intent.putExtra(NotificationPlayerService.CURRENT_AUDIO, currentMarcha);
        context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);


        registerNotificationListener(new NotificationPlayerService(context));
    }

    public void updateNotification() {
        notificationPlayer.updateTitle(getCurrentMarcha());
        notificationPlayer.updateNotification();
    }

    private boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayerService.PlayerServiceBinder binder = (PlayerService.PlayerServiceBinder) service;
            Log.e("GIDM", "bind servide");
            playerService = binder.getService();

            if (playerServiceListeners != null) {
                for (PlayerServiceListener playerServiceListener : playerServiceListeners)
                    playerService.registerServicePlayerListener(playerServiceListener);
            }
            if (playerStatusListeners != null) {
                for (PlayerStatusListener playerStatusListener : playerStatusListeners)
                    playerService.registerStatusListener(playerStatusListener);
            }
            if(notificationListener != null) {
                playerService.registerNotificationListener(notificationListener);
            }

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            state = Status.PlayState.PAUSE;
        }
    };


    public void registerNotificationListener(PlayerServiceListener notificationListener) {
        this.notificationListener = notificationListener;
        if (playerService != null) {
            playerService.registerNotificationListener(notificationListener);
        }
    }

    public void registerServiceListener(PlayerServiceListener playerServiceListener) {
        playerServiceListeners.add(playerServiceListener);
        if (playerService != null) {
            playerService.registerServicePlayerListener(playerServiceListener);
        }
    }

    public void registerStatusListener(PlayerStatusListener statusListener) {
        playerStatusListeners.add(statusListener);
        if (playerService != null) {
            playerService.registerStatusListener(statusListener);
        }
    }


    public void addMarcha(Marcha marcha)
    {
        playlist.add(marcha);
    }

    public List<Marcha> getPlaylist() {
        return playlist;
    }

    public void play()
    {
        if(state == Status.PlayState.PAUSE) {
            state = Status.PlayState.PLAY;
            playerService.play(playlist.get(currentMarcha));

        }
        else if(state == Status.PlayState.STOP || state == Status.PlayState.PLAY)
        {
            next();
        }
    }

    public void pause()
    {
        if (state == Status.PlayState.PLAY) {
            state = Status.PlayState.PAUSE;
            playerService.pause(playlist.get(currentMarcha));
        }
    }

    public void next()
    {
        if(currentMarcha+1 < playlist.size())
        {
            playerService.stop();
            currentMarcha = currentMarcha+1;
            playerService.play(playlist.get(currentMarcha));
            state = Status.PlayState.PLAY;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Api.nuevaEscucha(new SessionManager(context).getUserDetails().get(SessionManager.KEY_ID), playlist.get(currentMarcha).id);
                }
            }).start();
        }
        else
        {
            Toast.makeText(context, "No hay más marchas en la cola de reproducción", Toast.LENGTH_SHORT).show();
        }
    }


    public void stop()
    {
        playerService.stop();
    }

    public void previous()
    {
        if(currentMarcha-1 >= 0)
        {
            playerService.stop();
            currentMarcha = currentMarcha-1;
            playerService.play(playlist.get(currentMarcha));
            state = Status.PlayState.PLAY;
        }
        else
        {
            Toast.makeText(context, "No hay más marchas en la cola de reproducción", Toast.LENGTH_SHORT).show();
        }
    }

    public void seekTo(int time) {
        if (playerService != null) {
            playerService.seekTo(time);
        }
    }

    public Status.PlayState getState() {
        return state;
    }


    public Marcha getCurrentMarcha()
    {
        return playerService.getCurrentMarcha();
    }

    public int getProgress()
    {
        return playerService.getProgress();
    }
}
