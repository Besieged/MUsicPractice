package com.besieged.musicpractice.model;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/7/2.
 */

public class Lrc {

    /**
     * sgc : false
     * sfy : false
     * qfy : false
     * lrc : {"version":5,"lyric":"[00:00.00] 作曲 : 暗杠\n[00:00.940] 作词 : 暗杠/古道背棺人\n[00:02.820]编曲：暗杠\n[00:04.890]器乐演奏：暗杠\n[00:06.920]和声编配：暗杠\n[00:09.110]混音后期：A.Q.Studio\n[00:16.090]囡囡呀不要调皮\n[00:18.790]坐下听听阿婆说\n[00:22.800]这个季节天气转凉地上雨水多\n[00:29.760]囡囡呀不要惊慌\n[00:32.850]过来听听阿婆说\n[00:36.710]睡个觉雷声过后就能看云朵\n[00:44.440]囡囡别怕\n[00:45.770]囡囡别哭\n[00:47.490]快快睡咯\n[00:50.710]你静静听首歌\n[00:57.960]蛐蛐轻些\n[00:59.630]静静安歇\n[01:01.450]月儿圆哟\n[01:04.720]你乖乖呀抱阿婆\n[01:11.740]风铃呀轻响鸟儿轻唱远处谁在和\n[01:18.460]亲了彩虹惊了云朵\n[01:21.400]我已成归客\n[01:25.630]囡囡呀你会长大会走很远会觉得累了\n[01:32.760]只要记得河婆话\u201c阿婆\u201d怎么说\n[02:07.420]囡囡呀你会困惑\n[02:10.160]慢些脚步别忘了\n[02:14.220]慢慢地 你会明白丢了的是什么\n[02:21.240]人生路本就是场获得与失的选择\n[02:28.120]迷路时想想当年阿婆怎么说\n[02:35.590]回头看看\n[02:37.150]雨水过后\n[02:38.960]云彩很多\n[02:42.090]来吧 阿婆帮你偷偷摘一朵\n[02:49.290]等你老了\n[02:50.990]阿婆走了\n[02:52.790]你要记得\n[02:56.380]把这乡音教会娃儿怎么说\n[03:03.310]把这乡音教给你的囡囡哟\n[03:38.300]回头看看\n[03:39.720]雨水过后\n[03:41.430]少了冷漠\n[03:44.640]来吧 阿婆等你还在那村落\n"}
     * klyric : {"version":0,"lyric":null}
     * tlyric : {"version":0,"lyric":null}
     * code : 200
     */

    private boolean sgc;
    private boolean sfy;
    private boolean qfy;
    private LrcBean lrc;
    private KlyricBean klyric;
    private TlyricBean tlyric;
    private int code;

    public boolean isSgc() {
        return sgc;
    }

    public void setSgc(boolean sgc) {
        this.sgc = sgc;
    }

    public boolean isSfy() {
        return sfy;
    }

    public void setSfy(boolean sfy) {
        this.sfy = sfy;
    }

    public boolean isQfy() {
        return qfy;
    }

    public void setQfy(boolean qfy) {
        this.qfy = qfy;
    }

    public LrcBean getLrc() {
        return lrc;
    }

    public void setLrc(LrcBean lrc) {
        this.lrc = lrc;
    }

    public KlyricBean getKlyric() {
        return klyric;
    }

    public void setKlyric(KlyricBean klyric) {
        this.klyric = klyric;
    }

    public TlyricBean getTlyric() {
        return tlyric;
    }

    public void setTlyric(TlyricBean tlyric) {
        this.tlyric = tlyric;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class LrcBean {
        /**
         * version : 5
         * lyric : [00:00.00] 作曲 : 暗杠
         [00:00.940] 作词 : 暗杠/古道背棺人
         [00:02.820]编曲：暗杠
         [00:04.890]器乐演奏：暗杠
         [00:06.920]和声编配：暗杠
         [00:09.110]混音后期：A.Q.Studio
         [00:16.090]囡囡呀不要调皮
         [00:18.790]坐下听听阿婆说
         [00:22.800]这个季节天气转凉地上雨水多
         [00:29.760]囡囡呀不要惊慌
         [00:32.850]过来听听阿婆说
         [00:36.710]睡个觉雷声过后就能看云朵
         [00:44.440]囡囡别怕
         [00:45.770]囡囡别哭
         [00:47.490]快快睡咯
         [00:50.710]你静静听首歌
         [00:57.960]蛐蛐轻些
         [00:59.630]静静安歇
         [01:01.450]月儿圆哟
         [01:04.720]你乖乖呀抱阿婆
         [01:11.740]风铃呀轻响鸟儿轻唱远处谁在和
         [01:18.460]亲了彩虹惊了云朵
         [01:21.400]我已成归客
         [01:25.630]囡囡呀你会长大会走很远会觉得累了
         [01:32.760]只要记得河婆话“阿婆”怎么说
         [02:07.420]囡囡呀你会困惑
         [02:10.160]慢些脚步别忘了
         [02:14.220]慢慢地 你会明白丢了的是什么
         [02:21.240]人生路本就是场获得与失的选择
         [02:28.120]迷路时想想当年阿婆怎么说
         [02:35.590]回头看看
         [02:37.150]雨水过后
         [02:38.960]云彩很多
         [02:42.090]来吧 阿婆帮你偷偷摘一朵
         [02:49.290]等你老了
         [02:50.990]阿婆走了
         [02:52.790]你要记得
         [02:56.380]把这乡音教会娃儿怎么说
         [03:03.310]把这乡音教给你的囡囡哟
         [03:38.300]回头看看
         [03:39.720]雨水过后
         [03:41.430]少了冷漠
         [03:44.640]来吧 阿婆等你还在那村落

         */

        private int version;
        private String lyric;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
    }

    public static class KlyricBean {
        /**
         * version : 0
         * lyric : null
         */

        private int version;
        private Object lyric;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Object getLyric() {
            return lyric;
        }

        public void setLyric(Object lyric) {
            this.lyric = lyric;
        }
    }

    public static class TlyricBean {
        /**
         * version : 0
         * lyric : null
         */

        private int version;
        private Object lyric;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Object getLyric() {
            return lyric;
        }

        public void setLyric(Object lyric) {
            this.lyric = lyric;
        }
    }
}
