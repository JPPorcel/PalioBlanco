package etsiitdevs.com.palioblanco.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import etsiitdevs.com.palioblanco.PlayerActivity;
import etsiitdevs.com.palioblanco.R;
import etsiitdevs.com.palioblanco.api.Marcha;


public class PlayerViewMin extends LinearLayout implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private static final String TAG = PlayerViewMin.class.getSimpleName();


    private TextView title_song;
    private ImageView btnPrev;
    private ImageView btnNext;
    private ImageView btnPlay;
    private ImageView maximize;
    private AudioPlayer audioPlayer;

    public PlayerViewMin(Context context) {
        super(context);
        init();
    }

    public PlayerViewMin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        audioPlayer = AudioPlayer.getInstance(getContext());
        audioPlayer.registerServiceListener(playerServiceListener);
        inflate(getContext(), R.layout.player_min, this);

        this.btnNext = (ImageView) findViewById(R.id.next);
        this.btnPrev = (ImageView) findViewById(R.id.previous);
        this.btnPlay = (ImageView) findViewById(R.id.play);
        this.maximize = (ImageView) findViewById(R.id.max_player);
        this.title_song = (TextView) findViewById(R.id.title_song);
        this.btnPlay.setTag(R.drawable.ic_play_black_48dp);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        maximize.setOnClickListener(this);
        if(AudioPlayer.getInstance(getContext()).getState() == Status.PlayState.STOP)
            setAvailable(false);
        else {
            setAvailable(true);
            if(AudioPlayer.getInstance(getContext()).getState() == Status.PlayState.PLAY) {
                btnPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                btnPlay.setTag(R.drawable.ic_pause_black_48dp);
            }
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
            title_song.setText("No hay nada que reproducir");
            title_song.setSelected(true);
        }
        else
        {
            btnNext.setClickable(true);
            btnNext.setColorFilter(null);
            btnPrev.setClickable(true);
            btnPrev.setColorFilter(null);
            btnPlay.setClickable(true);
            btnPlay.setColorFilter(null);
            Marcha marcha = audioPlayer.getCurrentMarcha();
            if(marcha != null) title_song.setText(marcha.title);
            title_song.setSelected(true);
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
            setAvailable(true);
            btnPlay.setImageResource(R.drawable.ic_pause_black_48dp);
            btnPlay.setTag(R.drawable.ic_pause_black_48dp);
        }

        @Override
        public void onTimeChanged(long currentPosition) {
            long aux = currentPosition / 1000;
            int minutes = (int) (aux / 60);
            int seconds = (int) (aux % 60);
            final String sMinutes = minutes < 10 ? "0" + minutes : minutes + "";
            final String sSeconds = seconds < 10 ? "0" + seconds : seconds + "";
        }

        @Override
        public void updateTitle(final Marcha marcha) {
//            final String mTitle = title;
            title_song.setText(marcha.title);
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
            getContext().startActivity(new Intent(getContext(), PlayerActivity.class));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
