package com.besieged.musicpractice.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/22.
 */

public class utils {

    public static final void saveLyc(String path,String text){
        try {
            File f = new File(path);
            if (!f.exists()){
                f.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(f);
            os.write(text.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
