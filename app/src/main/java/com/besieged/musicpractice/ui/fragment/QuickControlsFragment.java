package com.besieged.musicpractice.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.besieged.musicpractice.MyApplication;
import com.besieged.musicpractice.R;
import com.besieged.musicpractice.base.BaseFragment;
import com.besieged.musicpractice.model.Song;
import com.besieged.musicpractice.player.IPlayer;
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.service.MusicService;
import com.besieged.musicpractice.ui.MusicPlayerActivity;
import com.besieged.musicpractice.utils.AlbumUtils;
import com.bumptech.glide.Glide;

public class QuickControlsFragment extends BaseFragment implements IPlayer.Callback{

    private ProgressBar mProgress;
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = mPlayer.getProgress();
            long duration = mPlayer.getPlayingSong().getDuration();
            if (duration > 0 && duration < 627080716){
                mProgress.setProgress((int) (1000 * position / duration));
            }

            if (mPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            }else {
                mProgress.removeCallbacks(mUpdateProgress);
            }

        }
    };
    //是否初始化页面UI
    boolean isInitUI = true;
    private IPlayer mPlayer;
    private MusicService mMusicService;
    boolean mIsServiceBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mMusicService = ((MusicService.LocalBinder) service).getService();
            mPlayer = mMusicService;
            mPlayer.registerCallback(QuickControlsFragment.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mMusicService = null;
            mPlayer.unregisterCallback(QuickControlsFragment.this);
            mPlayer = null;
        }
    };
    private ImageView mPlayPause;
    private TextView mTitle;
    private TextView mArtist;
    private ImageView mAlbumArt;
    private View rootView;
    MusicPlayer musicPlayer;
    private ImageView playQueue, next;
    private String TAG = "QuickControlsFragment";
    private static QuickControlsFragment fragment;

    public static QuickControlsFragment newInstance() {
        return new QuickControlsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.float_play_bar, container, false);
        this.rootView = rootView;
        musicPlayer = MusicPlayer.getInstance();
        mPlayPause = (ImageView) rootView.findViewById(R.id.control);
        mProgress = (ProgressBar) rootView.findViewById(R.id.song_progress_normal);
        mTitle = (TextView) rootView.findViewById(R.id.playbar_info);
        mArtist = (TextView) rootView.findViewById(R.id.playbar_singer);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.playbar_img);
        next = (ImageView) rootView.findViewById(R.id.play_next);
        playQueue = (ImageView) rootView.findViewById(R.id.play_list);

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayPause.setImageResource(musicPlayer.isPlaying() ? R.drawable.playbar_btn_pause
                        : R.drawable.playbar_btn_play);

                if (musicPlayer.getSongList().size() == 0) {
                    Toast.makeText(MyApplication.getContext(), getResources().getString(R.string.queue_is_empty),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            musicPlayer.playOrPause();
                        }
                    },60);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        musicPlayer.playNext();
                    }
                }, 60);
            }
        });

        playQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        PlayQueueFragment playQueueFragment = new PlayQueueFragment();
//                        playQueueFragment.show(getFragmentManager(), "playqueueframent");
                    }
                }, 60);
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), MusicPlayerActivity.class);
                intent.putExtra("position",musicPlayer.getmCurrentMusicIndex());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    public void updateNowplayingCard() {
        mTitle.setText(musicPlayer.getPlayingSong().getTitle());
        mArtist.setText(musicPlayer.getPlayingSong().getArtist());

        byte[] album = AlbumUtils.parseAlbumByte(musicPlayer.getPlayingSong());

        Glide.with(getActivity()).load(album).asBitmap().into(mAlbumArt);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        mProgress.removeCallbacks(mUpdateProgress);
        if (mIsServiceBound){
            mContext.unbindService(mConnection);
            mIsServiceBound = false;
            if (mPlayer!=null){
                mPlayer.unregisterCallback(QuickControlsFragment.this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mContext.bindService(new Intent(mContext, MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;

        mProgress.setMax(1000);
        if (mPlayer!=null && mPlayer.isPlaying()){
            mProgress.removeCallbacks(mUpdateProgress);
            mProgress.postDelayed(mUpdateProgress,0);
            updateNowplayingCard();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsServiceBound){
            mContext.unbindService(mConnection);
            mIsServiceBound = false;
        }
    }

    public void updateState() {
        if (musicPlayer.isPlaying()) {
            mPlayPause.setImageResource(R.drawable.playbar_btn_pause);
            mProgress.removeCallbacks(mUpdateProgress);
            mProgress.postDelayed(mUpdateProgress,50);
        } else {
            mPlayPause.setImageResource(R.drawable.playbar_btn_play);
            mProgress.removeCallbacks(mUpdateProgress);
        }
    }


    public void updateTrackInfo() {
        updateNowplayingCard();
        updateState();
    }
    @Override
    public void onSwitchLast(@Nullable Song last) {
        updateTrackInfo();
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        updateTrackInfo();
    }

    @Override
    public void onComplete(@Nullable Song next) {
        updateTrackInfo();
    }

    @Override
    public void onSongChangeed(@Nullable Song next) {
        updateNowplayingCard();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        updateState();
    }
}