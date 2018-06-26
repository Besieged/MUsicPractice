package com.besieged.musicpractice.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.besieged.musicpractice.R;
import com.besieged.musicpractice.base.BaseFragment;
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.utils.AlbumUtils;
import com.bumptech.glide.Glide;

public class QuickControlsFragment extends BaseFragment {


    private ProgressBar mProgress;
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = musicPlayer.getmCurrentMusicIndex();
            long duration = musicPlayer.getPlayingSong().getDuration();
            if (duration > 0 && duration < 627080716){
                mProgress.setProgress((int) (1000 * position / duration));
            }

            if (musicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            }else {
                mProgress.removeCallbacks(mUpdateProgress);
            }

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



//
//                if (MusicPlayer.getQueueSize() == 0) {
//                    Toast.makeText(MainApplication.context, getResources().getString(R.string.queue_is_empty),
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    HandlerUtil.getInstance(MainApplication.context).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            MusicPlayer.playOrPause();
//                        }
//                    }, 60);
//                }

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
//                Intent intent = new Intent(MyApplication.getContext(), MusicPlayerActivity.class);
//                intent.putExtra("songList", (Serializable) songList);
//                intent.putExtra("position",position);
//                MyApplication.getContext().startActivity(intent);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgress.setMax(1000);
        mProgress.removeCallbacks(mUpdateProgress);
        mProgress.postDelayed(mUpdateProgress,0);
        updateNowplayingCard();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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

}