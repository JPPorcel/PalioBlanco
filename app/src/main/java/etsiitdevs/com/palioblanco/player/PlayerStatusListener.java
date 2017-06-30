package etsiitdevs.com.palioblanco.player;

/**
 * Created by jpblo on 10/06/17.
 */

public interface PlayerStatusListener {
    void onPausedStatus(Status status);
    void onContinueAudioStatus(Status status);
    void onPlayingStatus(Status status);
    void onTimeChangedStatus(Status status);
    void onCompletedAudioStatus(Status status);
    void onPreparedAudioStatus(Status status);
}
