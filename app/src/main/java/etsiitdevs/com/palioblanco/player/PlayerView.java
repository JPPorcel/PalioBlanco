package etsiitdevs.com.palioblanco.player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import etsiitdevs.com.palioblanco.R;
import etsiitdevs.com.palioblanco.api.Marcha;
import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioListNullPointerException;

public class PlayerView extends LinearLayout implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private static final String TAG = PlayerViewMin.class.getSimpleName();


    private TextView song_name;
    private TextView song_autor;
    private ImageView btnPrev;
    private ImageView btnNext;
    private ImageView btnPlay;
    private AudioPlayer audioPlayer;
    private SeekBar seekbar;
    private TextView time_start;
    private TextView time_end;

    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        audioPlayer = AudioPlayer.getInstance(getContext());
        audioPlayer.registerServiceListener(playerServiceListener);
        inflate(getContext(), R.layout.player_view, this);

        this.btnNext = (ImageView) findViewById(R.id.next);
        this.btnPrev = (ImageView) findViewById(R.id.previous);
        this.btnPlay = (ImageView) findViewById(R.id.play);
        this.song_name = (TextView) findViewById(R.id.song_name);
        this.song_autor = (TextView) findViewById(R.id.song_autor);
        this.seekbar = (SeekBar) findViewById(R.id.seekbar);
        this.time_end = (TextView) findViewById(R.id.time_end);
        this.time_start = (TextView) findViewById(R.id.time_start);
        this.btnPlay.setTag(R.drawable.ic_play_black_48dp);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        setAvailable(false);

        if(audioPlayer.getState() == Status.PlayState.PLAY || audioPlayer.getState() == Status.PlayState.PAUSE)
        {
            Marcha marcha = audioPlayer.getCurrentMarcha();
            song_name.setText(marcha.nombre);
            song_autor.setText(marcha.autor);
            if(audioPlayer.getState() == Status.PlayState.PLAY) {
                btnPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                btnPlay.setTag(R.drawable.ic_pause_black_48dp);
            }

            long aux = marcha.duration;
            int minute = (int) (aux / 60);
            int second = (int) (aux % 60);

            String sDuration = // Minutes
                    (minute < 10 ? "0" + minute : minute + "")
                            + ":" +
                            // Seconds
                            (second < 10 ? "0" + second : second + "");

            time_end.setText(sDuration);
            seekbar.setMax((int) aux);
            setAvailable(true);
        }
    }

    private void setAvailable(boolean available)
    {
        if(!available)
        {
            btnNext.setClickable(false);
            btnNext.setColorFilter(Color.rgb(136, 136, 136));
            btnPrev.setClickable(false);
            btnPrev.setColorFilter(Color.rgb(136, 136, 136));
            btnPlay.setClickable(false);
            btnPlay.setColorFilter(Color.rgb(136, 136, 136));
            song_name.setText("No hay nada que reproducir");
            seekbar.setEnabled(false);
        }
        else
        {
            btnNext.setClickable(true);
            btnNext.setColorFilter(null);
            btnPrev.setClickable(true);
            btnPrev.setColorFilter(null);
            btnPlay.setClickable(true);
            btnPlay.setColorFilter(null);
            seekbar.setEnabled(true);
        }
    }

    PlayerServiceListener playerServiceListener = new PlayerServiceListener() {

        @Override
        public void onPreparedAudio(String audioName, int duration) {
            //dismissProgressBar();
            //resetPlayerInfo();

            long aux = duration / 1000;
            int minute = (int) (aux / 60);
            int second = (int) (aux % 60);

            final String sDuration = // Minutes
                    (minute < 10 ? "0" + minute : minute + "")
                            + ":" +
                            // Seconds
                            (second < 10 ? "0" + second : second + "");
            time_end.setText(sDuration);
            seekbar.setMax((int) aux);
        }

        @Override
        public void onCompletedAudio() {
            //resetPlayerInfo();

            try {
                audioPlayer.next();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPaused() {
            btnPlay.setImageResource(R.drawable.ic_play_black_48dp);
            btnPlay.setTag(R.drawable.ic_play_black_48dp);
        }

        @Override
        public void onContinueAudio() {
            //dismissProgressBar();
        }

        @Override
        public void onPlaying() {
            btnPlay.setImageResource(R.drawable.ic_pause_black_48dp);
            btnPlay.setTag(R.drawable.ic_pause_black_48dp);
        }

        @Override
        public void onTimeChanged(long currentPosition) {
            final long aux = currentPosition / 1000;
            int minutes = (int) (aux / 60);
            int seconds = (int) (aux % 60);
            final String sMinutes = minutes < 10 ? "0" + minutes : minutes + "";
            final String sSeconds = seconds < 10 ? "0" + seconds : seconds + "";
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int value = (int) aux;
                    if(updateSeekbar) seekbar.setProgress(value);
                    time_start.setText(sMinutes+":"+sSeconds);
                }
            });
        }

        @Override
        public void updateTitle(final Marcha marcha) {
//            final String mTitle = title;
            song_autor.setText(marcha.autor);
            song_name.setText(marcha.nombre);
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play) {
            if(audioPlayer.getState() == Status.PlayState.PLAY) audioPlayer.pause();
            else audioPlayer.play();
        }
        if (v.getId() == R.id.next)
            audioPlayer.next();

        if (v.getId() == R.id.previous) {
            audioPlayer.previous();
        }

        if(v.getId() == R.id.max_player){
            //getContext().startActivity(new Intent(getContext(), PlayerActivity.class));
        }
    }

    private boolean updateSeekbar = true;
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        updateSeekbar = false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateSeekbar = true;
        audioPlayer.seekTo(seekBar.getProgress()*1000);
    }
}
