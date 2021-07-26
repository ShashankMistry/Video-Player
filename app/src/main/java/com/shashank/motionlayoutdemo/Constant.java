package com.shashank.motionlayoutdemo;

import com.google.android.exoplayer2.MediaItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static String[] videoExtensions = {".mp4"};

    //all loaded files will be here
    public static ArrayList<File> allMediaList = new ArrayList<>();

    public static List<MediaItem> mediaItemList = new ArrayList<>();

}