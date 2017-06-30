package etsiitdevs.com.palioblanco.player;

import etsiitdevs.com.palioblanco.api.Marcha;

/**
 * Created by jpblo on 10/06/17.
 */

public interface PlayerServiceListener {
    void onPreparedAudio(String audioName, int duration);
    void onCompletedAudio();
    void onPaused();
    void onContinueAudio();
    void onPlaying();
    void onTimeChanged(long currentTime);
    void updateTitle(Marcha marchaa);
}
