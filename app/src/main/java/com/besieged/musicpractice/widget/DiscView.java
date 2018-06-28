package com.besieged.musicpractice.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.besieged.musicpractice.R;
import com.besieged.musicpractice.model.Song;
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.utils.DisplayUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AchillesL on 2016/11/15.
 */

public class DiscView extends RelativeLayout {

    private ImageView mIvNeedle;
    private ViewPager mVpContain;
    private NewViewPagerAdapter mViewPagerAdapter;
    private ObjectAnimator mNeedleAnimator;

    MusicPlayer musicPlayer;
    private Context mContext;

    private List<Song> mMusicDatas = new ArrayList<>();
    private List<ObjectAnimator> mDiscAnimators = new ArrayList<>();
    private ObjectAnimator currentAnimator = new ObjectAnimator();
    /*标记ViewPager是否处于偏移的状态*/
    private boolean mViewPagerIsOffset = false;

    /*标记唱针复位后，是否需要重新偏移到唱片处*/
    private boolean mIsNeed2StartPlayAnimator = false;
    private MusicStatus musicStatus = MusicStatus.STOP;

    public static final int DURATION_NEEDLE_ANIAMTOR = 300;
    private NeedleAnimatorStatus needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;

    public static final int INDEX_NULL = -1;

    private IPlayInfo mIPlayInfo;

    private boolean isUserSlide = true;

    private int mScreenWidth, mScreenHeight;

    /*唱针当前所处的状态*/
    private enum NeedleAnimatorStatus {
        /*移动时：从唱盘往远处移动*/
        TO_FAR_END,
        /*移动时：从远处往唱盘移动*/
        TO_NEAR_END,
        /*静止时：离开唱盘*/
        IN_FAR_END,
        /*静止时：贴近唱盘*/
        IN_NEAR_END
    }

    /*音乐当前的状态：只有播放、暂停、停止三种*/
    public enum MusicStatus {
        PLAY, PAUSE, STOP
    }

    /*DiscView需要触发的音乐切换状态：播放、暂停、上/下一首、停止*/
    public enum MusicChangedStatus {
        PLAY, PAUSE, NEXT, LAST, STOP
    }

    public interface IPlayInfo {
        /*用于更新标题栏变化*/
        void onMusicInfoChanged(String musicName, String musicAuthor);
        /*用于更新背景图片*/
        void onMusicPicChanged(String musicPicRes);
        /*用于更新背景图片*/
        void onMusicPicChanged(byte[] bytes);
        /*用于更新音乐播放状态 index-更新当前播放的音乐的index*/
        void onMusicChanged(MusicChangedStatus status,int index);
    }

    private static final String TAG = "discView";
    public DiscView(Context context) {
        this(context, null);
    }

    public DiscView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScreenWidth = DisplayUtil.getScreenWidth(context);
        mScreenHeight = DisplayUtil.getScreenHeight(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        musicPlayer = MusicPlayer.getInstance();

        initDiscBlackground();
        initViewPager();
        initNeedle();
        initObjectAnimator();
    }

    private void initDiscBlackground() {
        ImageView mDiscBlackground = (ImageView) findViewById(R.id.ivDiscBlackgound);
        mDiscBlackground.setImageDrawable(getDiscBlackgroundDrawable());

        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        LayoutParams layoutParams = (LayoutParams) mDiscBlackground
                .getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0);

        mDiscBlackground.setLayoutParams(layoutParams);
    }

    private void initViewPager() {
        mViewPagerAdapter = new NewViewPagerAdapter();
        mVpContain = (ViewPager) findViewById(R.id.vpDiscContain);
        mVpContain.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mVpContain.setOffscreenPageLimit(2);
        mVpContain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentItem = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"onPageSelected-position="+position);
                resetOtherDiscAnimation(position);
                notifyMusicPicChanged(position);
                notifyMusicInfoChanged(position);
                //更新playing页面 ui
//                notifyMusicStatusChanged(MusicChangedStatus.PLAY,position);

                if (isUserSlide){
                    notifyMusicStatusChanged(MusicChangedStatus.PLAY,position);
                }

                isUserSlide = true;

                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                doWithAnimatorOnPageScroll(state);
            }
        });
        mVpContain.setAdapter(mViewPagerAdapter);

        LayoutParams layoutParams = (LayoutParams) mVpContain.getLayoutParams();
        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        layoutParams.setMargins(0, marginTop, 0, 0);
        mVpContain.setLayoutParams(layoutParams);
    }

    /**
     * 取消其他页面上的动画，并将图片旋转角度复原
     */
    private void resetOtherDiscAnimation(int position) {
        for (int i = 0; i < mDiscAnimators.size(); i++) {
            if (position == i) continue;
            mDiscAnimators.get(position).cancel();
        }
    }

    private void doWithAnimatorOnPageScroll(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
            case ViewPager.SCROLL_STATE_SETTLING: {
                mViewPagerIsOffset = false;
                if (musicStatus == MusicStatus.PLAY) {
                    playAnimator();
                }
                break;
            }
            case ViewPager.SCROLL_STATE_DRAGGING: {
                mViewPagerIsOffset = true;
                pauseAnimator();
                break;
            }
        }
    }

    private void initNeedle() {
        mIvNeedle = (ImageView) findViewById(R.id.ivNeedle);

        int needleWidth = (int) (DisplayUtil.SCALE_NEEDLE_WIDTH * mScreenWidth);
        int needleHeight = (int) (DisplayUtil.SCALE_NEEDLE_HEIGHT * mScreenHeight);

        /*设置手柄的外边距为负数，让其隐藏一部分*/
        int marginTop = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_TOP * mScreenHeight) * -1;
        int marginLeft = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_LEFT * mScreenWidth);

        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
                .ic_needle);
        Bitmap bitmap = Bitmap.createScaledBitmap(originBitmap, needleWidth, needleHeight, false);

        LayoutParams layoutParams = (LayoutParams) mIvNeedle.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, 0, 0);

        int pivotX = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_X * mScreenWidth);
        int pivotY = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_Y * mScreenWidth);

        mIvNeedle.setPivotX(pivotX);
        mIvNeedle.setPivotY(pivotY);
        mIvNeedle.setRotation(DisplayUtil.ROTATION_INIT_NEEDLE);
        mIvNeedle.setImageBitmap(bitmap);
        mIvNeedle.setLayoutParams(layoutParams);
    }

    private void initObjectAnimator() {
        mNeedleAnimator = ObjectAnimator.ofFloat(mIvNeedle, View.ROTATION, DisplayUtil
                .ROTATION_INIT_NEEDLE, 0);
        mNeedleAnimator.setDuration(DURATION_NEEDLE_ANIAMTOR);
        mNeedleAnimator.setInterpolator(new AccelerateInterpolator());
        mNeedleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.d(TAG,"onAnimationStart-needleAnimatorStatus="+needleAnimatorStatus);
                /**
                 * 根据动画开始前NeedleAnimatorStatus的状态，
                 * 即可得出动画进行时NeedleAnimatorStatus的状态
                 * */
                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_NEAR_END;
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.d(TAG,"onAnimationEnd");
                if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_NEAR_END;
                    int index = mVpContain.getCurrentItem();
                    playDiscAnimator(index);
                    musicStatus = MusicStatus.PLAY;
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
                    if (musicStatus == MusicStatus.STOP) {
                        mIsNeed2StartPlayAnimator = true;
                    }
                }

                Log.d(TAG,"onAnimationEnd-mIsNeed2StartPlayAnimator="+mIsNeed2StartPlayAnimator);
                Log.d(TAG,"onAnimationEnd-needleAnimatorStatus="+needleAnimatorStatus);
                if (mIsNeed2StartPlayAnimator) {
                    mIsNeed2StartPlayAnimator = false;
                    /**
                     * 只有在ViewPager不处于偏移状态时，才开始唱盘旋转动画
                     * */
                    Log.i(TAG,"onAnimationEnd-mViewPagerIsOffset="+mViewPagerIsOffset);
                    if (!mViewPagerIsOffset) {
//                        playAnimator();
                        /*延时500ms*/
                        DiscView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        }, 50);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setPlayInfoListener(IPlayInfo listener) {
        this.mIPlayInfo = listener;
    }

    /*得到唱盘背后半透明的圆形背景*/
    private Drawable getDiscBlackgroundDrawable() {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc_blackground), discSize, discSize, false);
        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapDisc);
        return roundDiscDrawable;
    }

    /**
     * 设置唱盘的图片
     * 唱盘图片由空心圆盘及音乐专辑图片“合成”得到
     * @param disc
     * @param musicPicRes
     */
    private void setDiscDrawable(final ImageView disc, final String musicPicRes, final Bitmap bitmapDisc){
        final int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);
        Glide.with(mContext)
                .load(musicPicRes==null?R.drawable.abt:musicPicRes)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(musicPicSize,musicPicSize) {
                    @Override
                    public void onResourceReady(Bitmap resourse, GlideAnimation<? super Bitmap> glideAnimation) {

                        Bitmap bitmapMusicPic = Bitmap.createScaledBitmap(resourse, musicPicSize, musicPicSize, true);

                        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
                        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                                (getResources(), bitmapMusicPic);

                        //抗锯齿
                        discDrawable.setAntiAlias(true);
                        roundMusicDrawable.setAntiAlias(true);

                        Drawable[] drawables = new Drawable[2];
                        drawables[0] = roundMusicDrawable;
                        drawables[1] = discDrawable;

                        LayerDrawable layerDrawable = new LayerDrawable(drawables);
                        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil
                                .SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);
                        //调整专辑图片的四周边距，让其显示在正中
                        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
                                musicPicMargin);
                        disc.setImageDrawable(layerDrawable);
                        bitmapMusicPic.recycle();
                    }
                });
    }
    private void setDiscDrawable(final ImageView disc, byte[] bytes, final Bitmap bitmapDisc){
        final int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);

        Glide.with(mContext)
                .load(bytes)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(musicPicSize,musicPicSize) {
                    @Override
                    public void onResourceReady(Bitmap resourse, GlideAnimation<? super Bitmap> glideAnimation) {

                        Bitmap bitmapMusicPic = Bitmap.createScaledBitmap(resourse, musicPicSize, musicPicSize, true);

                        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
                        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                                (getResources(), bitmapMusicPic);

                        //抗锯齿
                        discDrawable.setAntiAlias(true);
                        roundMusicDrawable.setAntiAlias(true);

                        Drawable[] drawables = new Drawable[2];
                        drawables[0] = roundMusicDrawable;
                        drawables[1] = discDrawable;

                        LayerDrawable layerDrawable = new LayerDrawable(drawables);
                        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil
                                .SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);
                        //调整专辑图片的四周边距，让其显示在正中
                        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
                                musicPicMargin);
                        disc.setImageDrawable(layerDrawable);
                        bitmapMusicPic.recycle();
                    }
                });
    }
    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(),musicPicRes,options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //设置图片采样率
        options.inSampleSize = dstSample;
        //设置图片解码格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                musicPicRes, options), musicPicSize, musicPicSize, true);
    }

    private ObjectAnimator getDiscObjectAnimator(ImageView disc) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(disc, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setInterpolator(new LinearInterpolator());

        return objectAnimator;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /*播放动画*/
    private void playAnimator() {
        Log.i(TAG,"playAnimator-needleAnimatorStatus="+needleAnimatorStatus);
        /*唱针处于远端时，直接播放动画*/
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
            mNeedleAnimator.start();
        }
        /*唱针处于往远端移动时，设置标记，等动画结束后再播放动画*/
        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
            mIsNeed2StartPlayAnimator = true;
        }
        Log.i(TAG,"playAnimator-mIsNeed2StartPlayAnimator="+mIsNeed2StartPlayAnimator);
    }

    /*暂停动画*/
    private void pauseAnimator() {
        Log.i(TAG,"pauseAnimator-needleAnimatorStatus="+needleAnimatorStatus);
        /*播放时暂停动画*/
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
            int index = mVpContain.getCurrentItem();
            pauseDiscAnimatior(index);
        }
        /*唱针往唱盘移动时暂停动画*/
        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
            mNeedleAnimator.reverse();
            /**
             * 若动画在没结束时执行reverse方法，则不会执行监听器的onStart方法，此时需要手动设置
             * */
            needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
        }
        Log.i(TAG,"pauseAnimator-musicStatus="+musicStatus);
        /**
         * 动画可能执行多次，只有音乐处于停止 / 暂停状态时，才执行暂停命令
         * */
//        if (musicStatus == MusicStatus.STOP) {
//            notifyMusicStatusChanged(MusicChangedStatus.STOP,INDEX_NULL);
//        }else if (musicStatus == MusicStatus.PAUSE) {
//            notifyMusicStatusChanged(MusicChangedStatus.PAUSE,INDEX_NULL);
//        }
    }

    /*播放唱盘动画*/
    private void playDiscAnimator(int index) {
        Log.e(TAG,"playDiscAnimator-index="+index);
        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
        if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        } else {
            objectAnimator.start();
        }
//        if (musicStatus != MusicStatus.PLAY) {
//            notifyMusicStatusChanged(MusicChangedStatus.PLAY,mVpContain.getCurrentItem());
//        }
    }

    /*暂停唱盘动画*/
    private void pauseDiscAnimatior(int index) {
        Log.i(TAG,"pauseDiscAnimatior-index="+index);
        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
        objectAnimator.pause();
        mNeedleAnimator.reverse();
    }

    public void notifyMusicInfoChanged(int position) {
        if (mIPlayInfo != null) {
            Song musicData = mMusicDatas.get(position);
            mIPlayInfo.onMusicInfoChanged(musicData.getTitle(), musicData.getArtist());
        }
    }

    public void notifyMusicPicChanged(int position) {
        if (mIPlayInfo != null) {
            Song musicData = mMusicDatas.get(position);
            mIPlayInfo.onMusicPicChanged(musicData.getImgBytes());
        }
    }

    public void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus,int index) {
        if (mIPlayInfo != null) {
            mIPlayInfo.onMusicChanged(musicChangedStatus,index);
        }
    }

    public void play() {
        Log.i(TAG,"play");
        playAnimator();
    }

    public void pause() {
        Log.i(TAG,"pause");
        musicStatus = MusicStatus.PAUSE;
        pauseAnimator();
    }

    public void stop() {
        musicStatus = MusicStatus.STOP;
        pauseAnimator();
    }

    public void playOrPause() {
        Log.i(TAG,"playOrPause");
        if (musicPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public void next(int postion) {
        Log.e(TAG,"next-postion="+postion);
//        int currentItem = mVpContain.getCurrentItem();
//        if (currentItem == postion) {
//            resetOtherDiscAnimation(postion);
//            notifyMusicPicChanged(postion);
//            notifyMusicInfoChanged(postion);
//        } else {
        selectMusicWithButton();
        isUserSlide = false;
        mVpContain.setCurrentItem(postion, true);
//        }
    }

    public void last(int postion) {
//        int currentItem = mVpContain.getCurrentItem();
//        if (currentItem == postion) {//当传入的postion=currentItem时，重新刷新数据
//            resetOtherDiscAnimation(postion);
//            notifyMusicPicChanged(postion);
//            notifyMusicInfoChanged(postion);
//        } else {
        selectMusicWithButton();
        isUserSlide = false;
        mVpContain.setCurrentItem(postion, true);
//        }
    }

    public boolean isPlaying() {
        return musicPlayer.isPlaying();
    }

    private void selectMusicWithButton() {
        Log.i(TAG,"selectMusicWithButton-musicStatus="+musicStatus);
        if (musicStatus == MusicStatus.PLAY) {
            mIsNeed2StartPlayAnimator = true;
            pauseAnimator();
        } else if (musicStatus == MusicStatus.PAUSE) {
            play();
        }
    }

    /**
     * 设置数据
     * @param musicDataList
     * @param position
     */
    public void setMusicDataList(List<Song> musicDataList,int position) {
        if (musicDataList.isEmpty()) return;

        mMusicDatas.clear();
        mDiscAnimators.clear();
        mMusicDatas.addAll(musicDataList);

        //初始化向mDiscAnimators中放入mMusicDatas.size()个ObjectAnimator
        for (int i=0;i<mMusicDatas.size();i++){
            mDiscAnimators.add(new ObjectAnimator());
        }

        mViewPagerAdapter.notifyDataSetChanged();
        isUserSlide = false;
        mVpContain.setCurrentItem(position,false);

        Song musicData = mMusicDatas.get(position);
        if (mIPlayInfo != null) {
            mIPlayInfo.onMusicInfoChanged(musicData.getTitle(), musicData.getArtist());
            mIPlayInfo.onMusicPicChanged(musicData.getImgBytes());
        }
    }

    public void switchSong(Song song){
        int index = 0;
        for (Song song1 :mMusicDatas){
            if (song1.equals(song)){
                break;
            }
            index ++ ;
        }
        selectMusicWithButton();
        isUserSlide = false;
        mVpContain.setCurrentItem(index,true);
    }

    class NewViewPagerAdapter extends PagerAdapter{

        private int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        Bitmap bitmapDisc;
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if (mMusicDatas != null && position < mMusicDatas.size()) {
                View discLayout =  LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,
                        mVpContain, false);
//                String resId = mMusicDatas.get(position).getImage();

                ImageView disc = (ImageView) discLayout.findViewById(R.id.ivDisc);

                currentAnimator = getDiscObjectAnimator(disc);
                mDiscAnimators.set(position,currentAnimator);//初始化viewpager页面时 替换掉相应位置的ObjectAnimator

                if (bitmapDisc == null || bitmapDisc.isRecycled()){
                    bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                            .drawable.ic_disc), discSize, discSize, false);
                }

//                if (resId ==null || "".equals(resId)){

                    byte[] bytes = mMusicDatas.get(position).getImgBytes();

                    setDiscDrawable(disc,bytes,bitmapDisc);
                    discLayout.setTag(R.id.tag_resbitmap,bytes);
//                }else{
//                    setDiscDrawable(disc,resId,bitmapDisc);
//                    discLayout.setTag(R.id.tag_resid,resId);
//                }


                disc.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNeedleAnimator.reverse();
                    }
                });

                container.addView(discLayout);
                return discLayout;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object != null) {
                ViewGroup viewPager = ((ViewGroup) container);
                int count = viewPager.getChildCount();
                for (int i = 0; i < count; i++) {
                    View childView = viewPager.getChildAt(i);
                    if (childView == object) {
                        ImageView imageView = (ImageView) childView.findViewById(R.id.ivDisc);
                        releaseImageViewResouce(imageView);
                        viewPager.removeView(childView);
                        break;
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return mMusicDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 释放图片资源的方法
         * @param imageView
         */
        public void releaseImageViewResouce(ImageView imageView) {
            if (imageView == null) return;
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap=null;
                }
            }
            System.gc();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object != null && mMusicDatas != null) {

                byte[] bytes = (byte[]) ((ImageView)object).getTag(R.id.tag_resbitmap);
                if (bytes != null) {
                    for (int i = 0; i < mMusicDatas.size(); i++) {
                        if (Arrays.equals(bytes,mMusicDatas.get(i).getImgBytes())) {
                            return i;
                        }
                    }
                }

//                Object o = getTag(R.id.tag_resid);
//
//                if (null == o){
//
//                }else {
//                    String resId = (String)((ImageView)object).getTag(R.id.tag_resid);
//                    if (resId != null) {
//                        for (int i = 0; i < mMusicDatas.size(); i++) {
//                            if (resId.equals(mMusicDatas.get(i).getImage())) {
//                                return i;
//                            }
//                        }
//                    }
//                }
            }
            return POSITION_NONE;
        }
    }
}
