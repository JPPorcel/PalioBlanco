package etsiitdevs.com.palioblanco.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import etsiitdevs.com.palioblanco.R;
import etsiitdevs.com.palioblanco.api.Marcha;

class NotificationPlayerService implements PlayerServiceListener
{
    static final String NEXT = "NEXT";
    static final String PREVIOUS = "PREVIOUS";
    static final String PAUSE = "PAUSE";
    static final String PLAY = "PLAY";
    static final String ACTION = "ACTION";
    static final String PLAYLIST = "PLAYLIST";
    static final String CURRENT_AUDIO = "CURRENT_AUDIO";

    private static final int NOTIFICATION_ID = 100;
    private static final int NEXT_ID = 0;
    private static final int PREVIOUS_ID = 1;
    private static final int PLAY_ID = 2;
    private static final int PAUSE_ID = 3;

    private NotificationManager notificationManager;
    private Context context;
    private String title;
    private String time  = "00:00";
    private int iconResource;
    private Notification notification;

    public NotificationPlayerService(Context context){
        this.context = context;
        iconResource = R.drawable.icon;
    }

    public void createNotificationPlayer(final int iconResourceResource)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                iconResource = iconResourceResource;
                Intent openUi = new Intent(context, context.getClass());
                openUi.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                if (notificationManager == null) {
                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }

                notification = new Notification.Builder(context)
                        .setSmallIcon(iconResourceResource)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconResourceResource))
                        .setContent(createNotificationPlayerView())
                        .setContentIntent(PendingIntent.getActivity(context, NOTIFICATION_ID, openUi, PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        }).start();


    }

    public void updateNotification() {
        createNotificationPlayer(iconResource);
    }

    private RemoteViews createNotificationPlayerView() {
        RemoteViews remoteView;

        if (AudioPlayer.getInstance(context).getState() == Status.PlayState.PLAY) {
            remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_pause);
            remoteView.setOnClickPendingIntent(R.id.btn_pause_notification, buildPendingIntent(PAUSE, PAUSE_ID));
        } else {
            remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_play);
            remoteView.setOnClickPendingIntent(R.id.btn_play_notification, buildPendingIntent(PLAY, PLAY_ID));
        }

        remoteView.setTextViewText(R.id.txt_current_music_notification, title);
        remoteView.setImageViewResource(R.id.icon_player, iconResource);

        return remoteView;
    }

    private PendingIntent buildPendingIntent(String action, int id) {
        Intent playIntent = new Intent(context.getApplicationContext(), PlayerNotificationReceiver.class);
        playIntent.putExtra(ACTION, action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), id, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onPreparedAudio(String audioName, int duration) {
        this.title = audioName;
        createNotificationPlayer(iconResource);
    }

    @Override
    public void onCompletedAudio() {
        createNotificationPlayer(iconResource);
    }

    @Override
    public void onPaused() {
        createNotificationPlayer(iconResource);
    }

    @Override
    public void onContinueAudio() {
        createNotificationPlayer(iconResource);
    }

    @Override
    public void onPlaying() {
        createNotificationPlayer(iconResource);
    }

    @Override
    public void onTimeChanged(long currentTime) {

    }

    @Override
    public void updateTitle(Marcha marcha) {
        title = marcha.title;
    }

    public void destroyNotificationIfExists() {
        if (notificationManager != null) {
            try {
                notificationManager.cancel(NOTIFICATION_ID);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}