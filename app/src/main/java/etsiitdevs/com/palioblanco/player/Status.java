package etsiitdevs.com.palioblanco.player;


import etsiitdevs.com.palioblanco.api.Marcha;

public class Status {
  enum PlayState {
    PLAY, PAUSE, STOP, UNINTIALIZED
  }

  private Marcha marcha;
  private long duration;
  private PlayState playState;
  private long currentPosition;

  public Status() {
    this(null, 0, PlayState.UNINTIALIZED);
  }

  public Status(Marcha marcha, long currentPosition, PlayState playState) {
    this.marcha = marcha;
    if(marcha != null) this.duration = marcha.duration;
    else this.duration = 0;
    this.playState = playState;
    this.currentPosition = currentPosition;
  }

  public Marcha getMarcha() {
    return marcha;
  }

  public void setMarcha(Marcha audio) {
    this.marcha = audio;
  }

  public long getCurrentPosition() {
    return currentPosition;
  }

  public void setCurrentPosition(long currentPosition) {
    this.currentPosition = currentPosition;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public PlayState getPlayState() {
    return playState;
  }

  public void setPlayState(PlayState playState) {
    this.playState = playState;
  }
}
