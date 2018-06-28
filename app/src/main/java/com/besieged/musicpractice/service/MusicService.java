package com.besieged.musicpractice.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.besieged.musicpractice.R;
import com.besieged.musicpractice.model.Song;
import com.besieged.musicpractice.player.IPlayer;
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.ui.MainActivity;
import com.besieged.musicpractice.ui.MusicPlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/22.
 */

public class MusicService extends Service implements IPlayer,IPlayer.Callback {


    MusicPlayer mPlayer;

    public static final String ACTION_PLAY_TOGGLE = "ACTION_PLAY_TOGGLE";
    public static final String ACTION_PLAY_LAST = "ACTION_PLAY_LAST";
    public static final String ACTION_PLAY_NEXT = "ACTION_PLAY_NEXT";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_PLAY_POSITION = "ACTION_PLAY_POSITION";

    private static final int NOTIFICATION_ID = 1;

    private RemoteViews mContentViewBig, mContentViewSmall;

    private int mCurrentMusicIndex = 0;
    private boolean mIsMusicPause = false;
    private List<Song> mMusicDatas = new ArrayList<>();

    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private final Binder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    /**
     * 广播action 数组，添加action直接在这里写
     */
    private String[] actionArrs = new String[]{
            ACTION_STOP_SERVICE,
            ACTION_PLAY_LAST,
            ACTION_PLAY_NEXT,
            ACTION_STOP_SERVICE
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MusicPlayer.getInstance();
        mPlayer.registerCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMusicDatas(intent);
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY_TOGGLE.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (ACTION_PLAY_NEXT.equals(action)) {
                playNext();
            } else if (ACTION_PLAY_LAST.equals(action)) {
                playLast();
            } else if (ACTION_STOP_SERVICE.equals(action)) {
                if (isPlaying()) {
                    pause();
                }
                stopForeground(true);
                unregisterCallback(this);
            }else if (ACTION_PLAY_POSITION.equals(action)){
                play(mMusicDatas,mCurrentMusicIndex);
            }
        }
        return START_STICKY;
    }

    private void initMusicDatas(Intent intent) {
        if (intent == null) return;
        List<Song> musicDatas = mPlayer.getSongList();
        int position = intent.getIntExtra(MusicPlayerActivity.PARAM_MUSIC_POSITION,0);

        mCurrentMusicIndex = position;
        mMusicDatas.addAll(musicDatas);
    }

    @Override
    public void setPlayList(List<Song> list) {
        mPlayer.setPlayList(list);
    }

    @Override
    public boolean play() {
        return mPlayer.play();
    }

    @Override
    public boolean play(List<Song> list) {
        return mPlayer.play(list);
    }

    @Override
    public boolean play(List<Song> list, int startIndex) {
        return mPlayer.play(list,startIndex);
    }

    @Override
    public boolean play(Song song) {
        return mPlayer.play(song);
    }

    @Override
    public boolean playLast() {
        return mPlayer.playLast();
    }

    @Override
    public boolean playNext() {
        return mPlayer.playNext();
    }

    @Override
    public boolean pause() {
        return mPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mPlayer.getProgress();
    }

    @Override
    public Song getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    @Override
    public int getmCurrentMusicIndex() {
        return mPlayer.getmCurrentMusicIndex();
    }

    @Override
    public boolean seekTo(int progress) {
        return mPlayer.seekTo(progress);
    }

    @Override
    public void setPlayMode(int playMode) {
        mPlayer.setPlayMode(playMode);
    }

    @Override
    public int getPlayMode() {
        return mPlayer.getPlayMode();
    }

    @Override
    public void registerCallback(Callback callback) {
        mPlayer.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mPlayer.unregisterCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        mPlayer.removeCallbacks();
    }

    @Override
    public void releasePlayer() {
        mPlayer.releasePlayer();
        super.onDestroy();
    }
    @Override
    public void onSwitchLast(@Nullable Song last) {
        showNotification();
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        showNotification();
    }

    @Override
    public void onComplete(@Nullable Song next) {
        showNotification();
    }

    @Override
    public void onSongChangeed(@Nullable Song next) {
        showNotification();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        showNotification();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification);
    }

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private RemoteViews getBigContentView() {
        if (mContentViewBig == null) {
            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
            setUpRemoteView(mContentViewBig);
        }
        updateRemoteViews(mContentViewBig);
        return mContentViewBig;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last);
        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next);

        remoteView.setOnClickPendingIntent(R.id.button_close, getPendingIntent(ACTION_STOP_SERVICE));
        remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_PLAY_LAST));
        remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_PLAY_NEXT));
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_TOGGLE));
    }

    private void updateRemoteViews(final RemoteViews remoteView) {
        Song currentSong = mPlayer.getPlayingSong();
        if (currentSong != null) {
            remoteView.setTextViewText(R.id.text_view_name, currentSong.getTitle());
            remoteView.setTextViewText(R.id.text_view_artist, currentSong.getArtist());
        }
        remoteView.setImageViewResource(R.id.image_view_play_toggle, isPlaying()
                ? R.drawable.ic_remote_view_pause : R.drawable.ic_remote_view_play);
        final byte[] bytes = currentSong.getImgBytes();
        if (bytes == null) {
            remoteView.setImageViewResource(R.id.image_view_album, R.mipmap.ic_launcher);
        } else {
            Bitmap b = null;
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
            bitmap.createScaledBitmap(bitmap,72,72,true);
            b = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            remoteView.setImageViewBitmap(R.id.image_view_album, b);
            bitmap = null;
        }
    }
    // PendingIntent

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
