package com.besieged.musicpractice.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.besieged.musicpractice.R;
import com.besieged.musicpractice.base.BaseActivity;
import com.besieged.musicpractice.model.Song;
import com.besieged.musicpractice.player.IPlayer;
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.service.MusicService;
import com.besieged.musicpractice.utils.DisplayUtil;
import com.besieged.musicpractice.utils.FastBlurUtil;
import com.besieged.musicpractice.widget.DiscView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.besieged.musicpractice.player.MusicPlayer.MUSIC_MODE_LIST_LOOP;
import static com.besieged.musicpractice.player.MusicPlayer.MUSIC_MODE_RANDOM_PLAY;
import static com.besieged.musicpractice.player.MusicPlayer.MUSIC_MODE_SINGLE_LOOP;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class MusicPlayerActivity extends BaseActivity implements IPlayer.Callback,DiscView.IPlayInfo, View.OnClickListener {

    @BindView(R.id.rootLay)
    RelativeLayout rootLay;
    @BindView(R.id.music_player_toolbar)
    Toolbar musicPlayerToolbar;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.rlMusicTime)
    RelativeLayout rlMusicTime;
    @BindView(R.id.ivPlayMode)
    ImageView ivPlayMode;
    @BindView(R.id.ivLast)
    ImageView ivLast;
    @BindView(R.id.ivPlayOrPause)
    ImageView ivPlayOrPause;
    @BindView(R.id.ivNext)
    ImageView ivNext;
    @BindView(R.id.ivLike)
    ImageView ivLike;
    @BindView(R.id.llPlayOption)
    LinearLayout llPlayOption;
    public static final int MUSIC_MESSAGE = 0;

    public static final String PARAM_MUSIC_LIST = "PARAM_MUSIC_LIST";
    public static final String PARAM_MUSIC_POSITION = "PARAM_MUSIC_POSITION";
    public static final int DURATION_NEEDLE_ANIAMTOR = 500;
//    @BindView(R.id.discview)
    DiscView mDisc;

    MusicPlayer musicPlayer;
    private MusicService mMusicService;
    boolean mIsServiceBound = false;
    //是否初始化页面UI
    boolean isInitUI = true;

    private IPlayer mPlayer;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mMusicService = ((MusicService.LocalBinder) service).getService();
            mPlayer = mMusicService;
            mPlayer.registerCallback(MusicPlayerActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mMusicService = null;
            mPlayer.unregisterCallback(MusicPlayerActivity.this);
            mPlayer = null;
        }
    };

    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        getSupportActionBar().setTitle(musicName);
        getSupportActionBar().setSubtitle(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(String musicPicRes) {
        try2UpdateMusicPicBackground(musicPicRes);
    }

    @Override
    public void onMusicPicChanged(byte[] bytes) {
        try2UpdateMusicPicBackground(bytes);
    }

    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus,int index) {
        switch (musicChangedStatus) {
            case PLAY:{
                mCurrentMusicIndex = index;
                musicPlayer.play(musicPlayer.getSongList(),index);
                break;
            }
//            case PAUSE:{
//                pauseUI();
//                break;
//            }
//            case NEXT:{
//                resetUI();
//                break;
//            }
//            case STOP:{
//                stop();
//                break;
//            }
        }
    }

    private int playMode = 0;//默认是

    private DiscView.MusicStatus musicStatus = DiscView.MusicStatus.STOP;

    public List<Song> mMusicDatas = new ArrayList<>();
    private int mCurrentMusicIndex = 0;

    private Handler mHandler = new Handler();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    seekBar.setProgress(seekBar.getProgress() + 1000);
                    tvCurrentTime.setText(duration2Time(seekBar.getProgress()));
                    startUpdateSeekBarProgress();
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {

            if (mPlayer.isPlaying()) {
                int progress = mPlayer.getProgress();
                tvCurrentTime.setText(duration2Time(progress));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    seekBar.setProgress(progress, true);
                } else {
                    seekBar.setProgress(progress);
                }
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void showQuickControl(boolean show) {
//        super.showQuickControl(show);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        musicPlayer = MusicPlayer.getInstance();
        mDisc = (DiscView) findViewById(R.id.discview);
        setSupportActionBar(musicPlayerToolbar);
        musicPlayerToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        musicPlayerToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initMusicDatas();
        initView();
//        initBroadCastReceiver();
        mContext.bindService(new Intent(mContext, MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;
    }

    private void initMusicDatas() {
        mCurrentMusicIndex = this.getIntent().getIntExtra("position", 0);
        mMusicDatas = musicPlayer.getSongList();

//        Intent intent = new Intent(this, MusicService.class);
//        intent.putExtra(PARAM_MUSIC_POSITION, mCurrentMusicIndex);
//        startService(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {
        ivLast.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        ivPlayMode.setOnClickListener(this);
        ivPlayOrPause.setOnClickListener(this);
        mDisc.setPlayInfoListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvCurrentTime.setText(duration2Time(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdateSeekBarProgree();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayer.seekTo(seekBar.getProgress());
                startUpdateSeekBarProgress();
            }
        });
        //延迟200毫秒开始填充数据，播放音乐
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDisc.setMusicDataList(mMusicDatas,mCurrentMusicIndex);
                onSongUpdated(mMusicDatas.get(mCurrentMusicIndex));
                onPlayStatusChanged(true);
//                play();
            }
        },200);
    }

    private void try2UpdateMusicPicBackground(final String musicPicRes) {

        /*得到屏幕的宽高比，以便按比例切割图片一部分*/
        final float widthHeightSize = (float) (DisplayUtil.getScreenWidth(MusicPlayerActivity.this)
                * 1.0 / DisplayUtil.getScreenHeight(this) * 1.0);

        if (musicPicRes==null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Drawable foregroundDrawable = getForegroundDrawable(R.drawable.ic_blackground);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rootLay.setBackground(foregroundDrawable);
                        }
                    });
                }
            }).start();

        }else{
            Glide.with(mContext)
                    .load(musicPicRes)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                            int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
                            int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);

                            /*切割部分图片*/
                            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
                                    bitmap.getHeight());
                            /*缩小图片*/
                            Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap
                                    .getHeight() / 50, false);
                            /*模糊化*/
                            Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

                            Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
                            /*加入灰色遮罩层，避免图片过亮影响其他控件*/
                            foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            rootLay.setBackground(foregroundDrawable);
                        }
                    });
        }
    }
    private void try2UpdateMusicPicBackground(byte[] bytes) {

        /*得到屏幕的宽高比，以便按比例切割图片一部分*/
        final float widthHeightSize = (float) (DisplayUtil.getScreenWidth(MusicPlayerActivity.this)
                * 1.0 / DisplayUtil.getScreenHeight(this) * 1.0);

        if (bytes==null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Drawable foregroundDrawable = getForegroundDrawable(R.drawable.ic_blackground);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rootLay.setBackground(foregroundDrawable);
                        }
                    });
                }
            }).start();

        }else{
            Glide.with(mContext)
                    .load(bytes)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                            int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
                            int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);

                            /*切割部分图片*/
                            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
                                    bitmap.getHeight());
                            /*缩小图片*/
                            Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap
                                    .getHeight() / 50, false);
                            /*模糊化*/
                            Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

                            Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
                            /*加入灰色遮罩层，避免图片过亮影响其他控件*/
                            foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            rootLay.setBackground(foregroundDrawable);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivPlayOrPause) {
            playOrPause();
        } else if (v == ivNext) {
            next();
        } else if (v == ivLast) {
            last();
        } else if (v == ivPlayMode) {
            changePlayMode();
        } else if (v == ivLike) {
            addMusicToLike();
        }
    }
    private void play(){
        musicPlayer.play(musicPlayer.getSongList(),mCurrentMusicIndex);
    }
    //下一首
    private void next(){
        musicPlayer.playNext();
    }
    //上一首
    private void last(){
        musicPlayer.playLast();
    }
    //暂停或者开始播放
    private void playOrPause(){
        musicPlayer.playOrPause();
    }

    private void addMusicToLike() {
    }

    private void changePlayMode() {
        int index = 0;
        switch (playMode) {
            case MUSIC_MODE_LIST_LOOP:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_shuffle);
                playMode = MUSIC_MODE_RANDOM_PLAY;
                index = playMode;
                break;
            case MUSIC_MODE_SINGLE_LOOP:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_loop);
                playMode = MUSIC_MODE_LIST_LOOP;
                index = playMode;
                break;
            case MUSIC_MODE_RANDOM_PLAY:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_single);
                playMode = MUSIC_MODE_SINGLE_LOOP;
                index = playMode;
                break;
        }
        musicPlayer.setPlayMode(index);
    }
    private void setPlayModeView(int mode) {
        playMode = mode;
        switch (mode) {
            case MUSIC_MODE_LIST_LOOP:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_loop);
                break;
            case MUSIC_MODE_SINGLE_LOOP:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_single);
                break;
            case MUSIC_MODE_RANDOM_PLAY:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_shuffle);
                break;
        }
    }

    private Drawable getForegroundDrawable(int musicPicRes) {
        /*得到屏幕的宽高比，以便按比例切割图片一部分*/
        final float widthHeightSize = (float) (DisplayUtil.getScreenWidth(MusicPlayerActivity.this)
                * 1.0 / DisplayUtil.getScreenHeight(this) * 1.0);

        Bitmap bitmap = getForegroundBitmap(musicPicRes);

        int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);

        /*切割部分图片*/
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
                bitmap.getHeight());
        /*缩小图片*/
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap
                .getHeight() / 50, false);
        /*模糊化*/
        Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

        Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        /*加入灰色遮罩层，避免图片过亮影响其他控件*/
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }
    private Bitmap getForegroundBitmap(int musicPicRes) {
        int screenWidth = DisplayUtil.getScreenWidth(this);
        int screenHeight = DisplayUtil.getScreenHeight(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageWidth < screenWidth && imageHeight < screenHeight) {
            return BitmapFactory.decodeResource(getResources(), musicPicRes);
        }

        int sample = 2;
        int sampleX = imageWidth / DisplayUtil.getScreenWidth(this);
        int sampleY = imageHeight / DisplayUtil.getScreenHeight(this);

        if (sampleX > sampleY && sampleY > 1) {
            sample = sampleX;
        } else if (sampleY > sampleX && sampleX > 1) {
            sample = sampleY;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeResource(getResources(), musicPicRes, options);
    }

    /*根据时长格式化称时间文本*/
    private String duration2Time(int duration) {
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        return (min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }

    private void startUpdateSeekBarProgress() {
        /*避免重复发送Message*/
        stopUpdateSeekBarProgree();
        mHandler.post(mProgressCallback);
    }

    private void stopUpdateSeekBarProgree() {
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {
        onSongUpdated(last);
        changeDisc(last);
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        onSongUpdated(next);
        changeDisc(next);
    }

    @Override
    public void onComplete(@Nullable Song next) {
        onSongUpdated(next);
        changeDisc(next);
    }

    @Override
    public void onSongChangeed(@Nullable Song next) {

    }

    private void changeDisc(Song song){
        mDisc.switchSong(song);
    }
    /**
     * 音乐播放状态改变监听
     * @param isPlaying
     */
    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        ivPlayOrPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        if (isInitUI){//初始化更新UI
            isInitUI = false;
            updateMusicInfo();
        }
        if (!isPlaying){
            stopUpdateSeekBarProgree();
            mDisc.pause();
        }else {
            startUpdateSeekBarProgress();
            mDisc.play();
        }
    }
    //更新UI信息，包括seekbar进度，事件等。
    private void updateMusicInfo(){
        if (musicPlayer.isPlaying()){
            int progress = musicPlayer.getProgress();
            int totalDuration = (int) musicPlayer.getPlayingSong().getDuration();
            seekBar.setProgress(progress);
            seekBar.setMax(totalDuration);
            tvTotalTime.setText(duration2Time(totalDuration));
            tvCurrentTime.setText(duration2Time(progress));
        }else {
            int progress = 0;
            int totalDuration = (int) musicPlayer.getPlayingSong().getDuration();
            seekBar.setProgress(progress);
            seekBar.setMax(totalDuration);
            tvTotalTime.setText(duration2Time(totalDuration));
            tvCurrentTime.setText(duration2Time(progress));
        }
        setPlayModeView(musicPlayer.getPlayMode());
    }

    /**
     * 更新UI信息
     * @param song
     */
    private void onSongUpdated(Song song){
        if (song == null) {
            resetUI();
            return;
        }
        //更新seekbar，已播放时间
        updateMusicDurationInfo((int)song.getDuration());

        //更新tiitle
        getSupportActionBar().setTitle(song.getTitle());
        getSupportActionBar().setSubtitle(song.getArtist());

        //更新背景图
        try2UpdateMusicPicBackground(song.getImgBytes());
    }

    private void updateMusicDurationInfo(int totalDuration) {
        seekBar.setProgress(0);
        seekBar.setMax(totalDuration);
        tvTotalTime.setText(duration2Time(totalDuration));
        tvCurrentTime.setText(duration2Time(0));
        startUpdateSeekBarProgress();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsServiceBound){
            mContext.unbindService(mConnection);
            if (mPlayer!=null){
                mPlayer.unregisterCallback(MusicPlayerActivity.this);
            }
            mIsServiceBound = false;
        }
        //LocalBroadcastManager.getInstance(mContext).unregisterReceiver(musicReceiver);
    }

    private void resetUI() {
        stopUpdateSeekBarProgree();
        seekBar.setProgress(0);
        tvCurrentTime.setText(duration2Time(0));
        tvTotalTime.setText(duration2Time(0));
    }
}
