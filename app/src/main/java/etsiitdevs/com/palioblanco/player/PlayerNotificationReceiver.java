package etsiitdevs.com.palioblanco.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import etsiitdevs.com.palioblanco.player.PlayerExceptions.AudioListNullPointerException;

public class PlayerNotificationReceiver extends BroadcastReceiver {
    public PlayerNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioPlayer audioPlayer = AudioPlayer.getInstance(context);
        String action = "";

        if (intent.hasExtra(NotificationPlayerService.ACTION)) {
            action = intent.getStringExtra(NotificationPlayerService.ACTION);
        }

        switch (action) {
            case NotificationPlayerService.PLAY:
                audioPlayer.play();
                audioPlayer.updateNotification();
                break;

            case NotificationPlayerService.PAUSE:
                audioPlayer.pause();
                audioPlayer.updateNotification();
                break;

            case NotificationPlayerService.NEXT:

                audioPlayer.next();
                break;

            case NotificationPlayerService.PREVIOUS:
                audioPlayer.previous();
                break;
        }
    }
}
