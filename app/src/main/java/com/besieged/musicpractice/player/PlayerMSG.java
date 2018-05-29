package com.besieged.musicpractice.player;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/29.
 */

public class PlayerMSG {
    public class ACTION_MSG{
        //操作指令
        public static final String ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY";
        public static final String ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE";
        public static final String ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT";
        public static final String ACTION_OPT_MUSIC_LAST = "ACTION_OPT_MUSIC_LAST";
        public static final String ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO";
        public static final String ACTION_OPT_MUSIC_MODE_UPDATE = "ACTION_OPT_MUSIC_MODE_UPDATE";

        //状态指令
        public static final String ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY";
        public static final String ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE";
        public static final String ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE";
        public static final String ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION";

        public static final String PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION";
        public static final String PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO";
        public static final String PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION";
        public static final String PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER";
        public static final String PARAM_MUSIC_POSITION = "PARAM_MUSIC_POSITION";
        public static final String PARAM_MUSIC_MODE = "PARAM_MUSIC_MODE";
        public static final String PARAM_MUSIC_INDEX = "PARAM_MUSIC_INDEX";

    }
}
