package com.shashank.motionlayoutdemo;

import android.net.Uri;

import com.google.android.exoplayer2.MediaItem;

import java.io.File;

public class Method {

    public static void load_Directory_Files(File directory) {
        File[] fileList = directory.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (File file : fileList) {
                if (Constant.allMediaList.size() < 10) {
                    if (file.isDirectory() && !file.isHidden()) {
                        load_Directory_Files(file);
                    } else {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".mp4")) {
                            Constant.allMediaList.add(file);
                            Constant.mediaItemList.add(MediaItem.fromUri(Uri.fromFile(file)));
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }
}