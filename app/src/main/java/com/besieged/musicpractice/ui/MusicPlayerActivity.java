package com.besieged.musicpractice.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.service.MusicService;
import com.besieged.musicpractice.utils.DisplayUtil;
import com.besieged.musicpractice.utils.FastBlurUtil;
import com.besieged.musicpractice.widget.DiscView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_COMPLETE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_DURATION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_PAUSE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_PLAY;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_CURRENT_POSITION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_DURATION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_IS_OVER;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class MusicPlayerActivity extends BaseActivity implements DiscView.IPlayInfo, View.OnClickListener {

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
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus,int index) {
        switch (musicChangedStatus) {
            case PLAY:{
                mCurrentMusicIndex = index;
                startUpdateSeekBarProgress();
                break;
            }
            case PAUSE:{
                pauseUI();
                break;
            }
            case NEXT:{
                resetUI();
                break;
            }
            case STOP:{
                stop();
                break;
            }
        }
    }

    private int playMode = 0;//默认是

    private DiscView.MusicStatus musicStatus = DiscView.MusicStatus.STOP;

    MusicReceiver musicReceiver = new MusicReceiver();
    private List<Song> mMusicDatas = new ArrayList<>();
    private int mCurrentMusicIndex = 0;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        musicPlayer = MusicPlayer.getInstance(MusicPlayerActivity.this);
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
        initBroadCastReceiver();
    }

    private void initMusicDatas() {
        mMusicDatas = (List<Song>) this.getIntent().getSerializableExtra("songList");
        mCurrentMusicIndex = this.getIntent().getIntExtra("position", 0);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST, (Serializable) mMusicDatas);
        intent.putExtra(PARAM_MUSIC_POSITION, mCurrentMusicIndex);
        startService(intent);
    }

    private void initBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STATUS_MUSIC_PLAY);
        filter.addAction(ACTION_STATUS_MUSIC_PAUSE);
        filter.addAction(ACTION_STATUS_MUSIC_COMPLETE);
        filter.addAction(ACTION_STATUS_MUSIC_DURATION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(musicReceiver, filter);
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

    @Override
    public void onClick(View v) {
        if (v == ivPlayOrPause) {
            mDisc.playOrPause();
            musicPlayer.playOrPause();
        } else if (v == ivNext) {
            int index = 0;
            if (playMode == 1){
                index = mCurrentMusicIndex;
            }else if (playMode == 2){
                Random random = new Random();
                index = random.nextInt(mMusicDatas.size());
            }else if (playMode == 0){
                if (mCurrentMusicIndex + 1 >= mMusicDatas.size()){
                    index = 0;
                }else {
                    index = mCurrentMusicIndex + 1;
                }
            }
            mDisc.next(index);
            if (playMode == 1){
                musicPlayer.play(index);
            }
        } else if (v == ivLast) {
            int index = 0;
            if (playMode == 1){
                index = mCurrentMusicIndex;
            }else if (playMode == 2){
                Random random = new Random();
                index = random.nextInt(mMusicDatas.size());
            }else if (playMode == 0){
                if (mCurrentMusicIndex - 1 < 0){
                    index = mMusicDatas.size()-1;
                }else {
                    index = mCurrentMusicIndex - 1;
                }
            }
            mDisc.last(index);
            if (playMode == 1){
                musicPlayer.play(index);
            }
        } else if (v == ivPlayMode) {
            changePlayMode();
        } else if (v == ivLike) {
            addMusicToLike();
        }
    }

    private void addMusicToLike() {
    }

    private void changePlayMode() {
//        Intent intent = new Intent(ACTION_OPT_MUSIC_MODE_UPDATE);
        int index = 0;
        switch (playMode) {
            case 0:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_shuffle);
                playMode = 2;
                index = playMode;
                break;
            case 1:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_loop);
                playMode = 0;
                index = playMode;
                break;
            case 2:
                ivPlayMode.setImageResource(R.drawable.ic_play_mode_single);
                playMode = 1;
                index = playMode;
                break;
        }
        musicPlayer.changeMode(index);
//        intent.putExtra(PARAM_MUSIC_MODE, index);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private void stopUpdateSeekBarProgree() {
        handler.removeMessages(MUSIC_MESSAGE);
    }

    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_STATUS_MUSIC_PLAY)) {
                ivPlayOrPause.setImageResource(R.drawable.ic_pause);
                int currentPosition = intent.getIntExtra(PARAM_MUSIC_CURRENT_POSITION, 0);
                seekBar.setProgress(currentPosition);
                if(!mDisc.isPlaying()){
                    mDisc.playOrPause();
                }

            } else if (action.equals(ACTION_STATUS_MUSIC_PAUSE)) {
                ivPlayOrPause.setImageResource(R.drawable.ic_play);
                if (mDisc.isPlaying()) {
                    mDisc.playOrPause();
                }
            } else if (action.equals(ACTION_STATUS_MUSIC_DURATION)) {
                int duration = intent.getIntExtra(PARAM_MUSIC_DURATION, 0);
                updateMusicDurationInfo(duration);
            } else if (action.equals(ACTION_STATUS_MUSIC_COMPLETE)) {
                boolean isOver = intent.getBooleanExtra(PARAM_MUSIC_IS_OVER, true);
                complete(isOver);
            }
        }
    }

    private void complete(boolean isOver) {
        if (isOver) {
            mDisc.stop();
            stop();
            musicPlayer.stop();
        } else {
            int index = 0;
            if (playMode == 1){
                index = mCurrentMusicIndex;
            }else if (playMode == 2){
                Random random = new Random();
                index = random.nextInt(mMusicDatas.size());
            }else if (playMode == 0){
                if (mCurrentMusicIndex + 1 >= mMusicDatas.size()){
                    index = 0;
                }else {
                    index = mCurrentMusicIndex + 1;
                }
            }
            mDisc.next(index);
            if (playMode == 1){
                musicPlayer.play(index);
            }
        }
    }

    private void updateMusicDurationInfo(int totalDuration) {
        seekBar.setProgress(0);
        seekBar.setMax(totalDuration);
        tvTotalTime.setText(duration2Time(totalDuration));
        tvCurrentTime.setText(duration2Time(0));
        startUpdateSeekBarProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(musicReceiver);
    }

    private void play() {
//        optMusic(ACTION_OPT_MUSIC_PLAY);
        musicPlayer.play();
        startUpdateSeekBarProgress();
    }
//    private void play(final int index) {
//        Intent i = new Intent(ACTION_OPT_MUSIC_PLAY);
//        i.putExtra(PARAM_MUSIC_INDEX,index);
//        LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);
//    }

    private void pauseUI() {
        stopUpdateSeekBarProgree();
    }

    private void stop() {
        stopUpdateSeekBarProgree();
        ivPlayOrPause.setImageResource(R.drawable.ic_play);
        tvCurrentTime.setText(duration2Time(0));
        tvTotalTime.setText(duration2Time(0));
        seekBar.setProgress(0);
    }

    private void resetUI() {
        stopUpdateSeekBarProgree();
        tvCurrentTime.setText(duration2Time(0));
        tvTotalTime.setText(duration2Time(0));
    }
}
