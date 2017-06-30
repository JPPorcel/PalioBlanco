package etsiitdevs.com.palioblanco.player;


import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import etsiitdevs.com.palioblanco.api.Marcha;
import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioAssetsInvalidException;
import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioFilePathInvalidException;
import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioRawInvalidException;
import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioUrlInvalidException;

public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener{

    private static final String TAG = PlayerService.class.getSimpleName();

    private MediaPlayer mediaPlayer;
    private List<PlayerServiceListener> playerServiceListeners;
    private List<PlayerStatusListener> playerStatusListeners;
    private PlayerServiceListener notificationListener;
    private Status status = new Status();


    public void registerNotificationListener(PlayerServiceListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public void registerServicePlayerListener(PlayerServiceListener playerServiceListener) {
        if (playerServiceListeners == null) {
            playerServiceListeners = new ArrayList<>();
        }

        if (!playerServiceListeners.contains(playerServiceListener)) {
            playerServiceListeners.add(playerServiceListener);
        }
    }

    public void registerStatusListener(PlayerStatusListener statusListener) {
        if (playerStatusListeners == null) {
            playerStatusListeners = new ArrayList<>();
        }

        if (!playerStatusListeners.contains(statusListener)) {
            playerStatusListeners.add(statusListener);
        }
    }


    private Marcha currentMarcha;
    private boolean isPlaying;
    private long duration;
    private long currentTime;
    public void play(Marcha marcha)
    {
        String id = "";
        if(currentMarcha != null) id = currentMarcha.id;
        currentMarcha = marcha;
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(marcha.path);

                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
            } else {
                if (isPlaying) {
                    stop();
                    play(marcha);
                } else {
                    if(id != currentMarcha.id) {
                        stop();
                        play(marcha);
                    } else {
                        mediaPlayer.start();
                        isPlaying = true;

                        if (playerServiceListeners != null) {
                            for (PlayerServiceListener playerServiceListener : playerServiceListeners) {
                                playerServiceListener.onContinueAudio();
                            }
                        }

                        if (playerStatusListeners != null) {
                            for (PlayerStatusListener playerViewStatusListener : playerStatusListeners) {
                                status.setMarcha(marcha);
                                status.setPlayState(Status.PlayState.PLAY);
                                status.setDuration(mediaPlayer.getDuration());
                                status.setCurrentPosition(mediaPlayer.getCurrentPosition());
                                playerViewStatusListener.onContinueAudioStatus(status);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateTimeAudio();

        if(playerServiceListeners != null) {
            for (PlayerServiceListener playerServiceListener : playerServiceListeners) {
                playerServiceListener.onPlaying();
            }
        }

        if (playerStatusListeners != null) {
            for (PlayerStatusListener playerViewStatusListener : playerStatusListeners) {
                status.setMarcha(marcha);
                status.setPlayState(Status.PlayState.PLAY);
                status.setDuration(0);
                status.setCurrentPosition(0);
                playerViewStatusListener.onPlayingStatus(status);
            }
        }

        if (notificationListener != null)
        {
            notificationListener.updateTitle(currentMarcha);
            notificationListener.onPlaying();
        }
    }

    private void updateTimeAudio() {
        new Thread() {
            public void run() {
                while (isPlaying) {
                    try {

                        if (playerServiceListeners != null) {
                            for (PlayerServiceListener playerServiceListener : playerServiceListeners) {
                                playerServiceListener.onTimeChanged(mediaPlayer.getCurrentPosition());
                            }
                        }
                        if (notificationListener != null) {
                            notificationListener.onTimeChanged(mediaPlayer.getCurrentPosition());
                        }

                        if (playerStatusListeners != null) {
                            for (PlayerStatusListener playerViewStatusListener : playerStatusListeners) {
                                status.setPlayState(Status.PlayState.PLAY);
                                status.setDuration(mediaPlayer.getDuration());
                                status.setCurrentPosition(mediaPlayer.getCurrentPosition());
                                playerViewStatusListener.onTimeChangedStatus(status);
                            }
                        }
                        Thread.sleep(200);
                    } catch (IllegalStateException | InterruptedException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void pause(Marcha marcha) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            duration = mediaPlayer.getDuration();
            currentTime = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }

        if(playerServiceListeners != null) {
            for (PlayerServiceListener playerServiceListener : playerServiceListeners) {
                playerServiceListener.onPaused();
            }
        }

        if (notificationListener != null) {
            notificationListener.updateTitle(currentMarcha);
            notificationListener.onPaused();
        }

        if(playerStatusListeners != null) {
            for (PlayerStatusListener playerStatusListener :playerStatusListeners) {
                status.setMarcha(marcha);
                status.setDuration(duration);
                status.setCurrentPosition(currentTime);
                status.setPlayState(Status.PlayState.PAUSE);
                playerStatusListener.onPausedStatus(status);
            }
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        ((NotificationPlayerService)notificationListener).destroyNotificationIfExists();
        isPlaying = false;
    }

    public void destroy() {
        stop();
        stopSelf();
    }

    public void seekTo(int time){
        Log.d("time = ", Integer.toString(time));
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playerServiceListeners != null) {
            for (PlayerServiceListener playerServiceListener : playerServiceListeners) {
                playerServiceListener.onCompletedAudio();
            }
        }
        if (notificationListener != null) {
            notificationListener.onCompletedAudio();
        }

        if (playerStatusListeners != null) {
            for (PlayerStatusListener playerViewStatusListener : playerStatusListeners) {
                playerViewStatusListener.onCompletedAudioStatus(status);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        isPlaying = true;
        this.duration = mediaPlayer.getDuration();
        this.currentTime = mediaPlayer.getCurrentPosition();
        updateTimeAudio();

        if (playerServiceListeners != null) {
            for (PlayerServiceListener playerServiceListener : playerServiceListeners) {
                playerServiceListener.updateTitle(currentMarcha);
                playerServiceListener.onPreparedAudio(currentMarcha.nombre + " - " + currentMarcha.autor, mediaPlayer.getDuration());
            }
        }

        if (notificationListener != null) {
            notificationListener.updateTitle(currentMarcha);
            notificationListener.onPreparedAudio(currentMarcha.nombre + " - " + currentMarcha.autor, mediaPlayer.getDuration());
        }

        if (playerStatusListeners != null) {
            for (PlayerStatusListener playerViewStatusListener : playerStatusListeners) {
                status.setMarcha(currentMarcha);
                status.setPlayState(Status.PlayState.PLAY);
                status.setDuration(duration);
                status.setCurrentPosition(currentTime);
                playerViewStatusListener.onPreparedAudioStatus(status);
            }
        }
    }

    public Marcha getCurrentMarcha() {
        return currentMarcha;
    }

    public int getProgress()
    {
        return mediaPlayer.getCurrentPosition();
    }

    private final IBinder mBinder = new PlayerServiceBinder();
    public class PlayerServiceBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
