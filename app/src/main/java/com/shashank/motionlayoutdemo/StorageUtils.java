package com.shashank.motionlayoutdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for getting all storages directories in an Android device
 * <a href="https://stackoverflow.com/a/40582634/3940133">Solution of this problem</a>
 * Consider to use
 * <a href="https://developer.android.com/guide/topics/providers/document-provider">StorageAccessFramework(SAF)</>
 * if your min SDK version is 19 and your requirement is just for browse and open documents, images, and other files
 *
 * @author Dmitriy Lozenko, HendraWD
 */
public class StorageUtils {

    // Primary physical SD-CARD (not emulated)
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");

    // All Secondary SD-CARDs (all exclude primary) separated by File.pathSeparator, i.e: ":", ";"
    private static final String SECONDARY_STORAGES = System.getenv("SECONDARY_STORAGE");

    // Primary emulated SD-CARD
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");

    // PhysicalPaths based on phone model
    @SuppressLint("SdCardPath")
    @SuppressWarnings("SpellCheckingInspection")
    private static final String[] KNOWN_PHYSICAL_PATHS = new String[]{
            "/storage/sdcard0",
            "/storage/sdcard1",                 //Motorola Xoom
            "/storage/extsdcard",               //Samsung SGS3
            "/storage/sdcard0/external_sdcard", //User request
            "/mnt/extsdcard",
            "/mnt/sdcard/external_sd",          //Samsung galaxy family
            "/mnt/sdcard/ext_sd",
            "/mnt/external_sd",
            "/mnt/media_rw/sdcard1",            //4.4.2 on CyanogenMod S3
            "/removable/microsd",               //Asus transformer prime
            "/mnt/emmc",
            "/storage/external_SD",             //LG
            "/storage/ext_sd",                  //HTC One Max
            "/storage/removable/sdcard1",       //Sony Xperia Z1
            "/data/sdext",
            "/data/sdext2",
            "/data/sdext3",
            "/data/sdext4",
            "/sdcard1",                         //Sony Xperia Z
            "/sdcard2",                         //HTC One M8s
            "/storage/microsd"                  //ASUS ZenFone 2
    };

    /**
     * Returns all available storages in the system (include emulated)
     * <p/>
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standard way to get it.
     *
     * @return paths to all available storages in the system (include emulated)
     */
    public static String[] getStorageDirectories(Context context) {
        // Final set of paths
        final Set<String> availableDirectoriesSet = new HashSet<>();

        if (!TextUtils.isEmpty(EMULATED_STORAGE_TARGET)) {
            // Device has an emulated storage
            availableDirectoriesSet.add(getEmulatedStorageTarget());
        } else {
            // Device doesn't have an emulated storage
            availableDirectoriesSet.addAll(getExternalStorage(context));
        }

        // Add all secondary storages
        Collections.addAll(availableDirectoriesSet, getAllSecondaryStorages());

        String[] storagesArray = new String[availableDirectoriesSet.size()];
        return availableDirectoriesSet.toArray(storagesArray);
    }

    private static Set<String> getExternalStorage(Context context) {
        final Set<String> availableDirectoriesSet = new HashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Solution of empty raw emulated storage for android version >= marshmallow
            // because the EXTERNAL_STORAGE become something like: "/Storage/A5F9-15F4",
            // so we can't access it directly
            File[] files = getExternalFilesDirs(context);
            for (File file : files) {
                if (file != null) {
                    String applicationSpecificAbsolutePath = file.getAbsolutePath();
                    String rootPath = applicationSpecificAbsolutePath.substring(
                            0,
                            applicationSpecificAbsolutePath.indexOf("Android/data")
                    );
                    availableDirectoriesSet.add(rootPath);
                }
            }
        } else {
            if (TextUtils.isEmpty(EXTERNAL_STORAGE)) {
                availableDirectoriesSet.addAll(getAvailablePhysicalPaths());
            } else {
                // Device has physical external storage; use plain paths.
                availableDirectoriesSet.add(EXTERNAL_STORAGE);
            }
        }
        return availableDirectoriesSet;
    }

    private static String getEmulatedStorageTarget() {
        String rawStorageId = "";
        // External storage paths should have storageId in the last segment
        // i.e: "/storage/emulated/storageId" where storageId is 0, 1, 2, ...
        final String path = System.getenv("EXTERNAL_STORAGE");
        assert path != null;
        final String[] folders = path.split(File.separator);
        final String lastSegment = folders[folders.length - 1];
        if (!TextUtils.isEmpty(lastSegment) && TextUtils.isDigitsOnly(lastSegment)) {
            rawStorageId = lastSegment;
        }

        if (TextUtils.isEmpty(rawStorageId)) {
            return EMULATED_STORAGE_TARGET;
        } else {
            return EMULATED_STORAGE_TARGET + File.separator + rawStorageId;
        }
    }

    private static String[] getAllSecondaryStorages() {
        if (!TextUtils.isEmpty(SECONDARY_STORAGES)) {
            // All Secondary SD-CARDs split into array
            return SECONDARY_STORAGES.split(File.pathSeparator);
        }
        return new String[0];
    }


    private static List<String> getAvailablePhysicalPaths() {
        List<String> availablePhysicalPaths = new ArrayList<>();
        for (String physicalPath : KNOWN_PHYSICAL_PATHS) {
            File file = new File(physicalPath);
            if (file.exists()) {
                availablePhysicalPaths.add(physicalPath);
            }
        }
        return availablePhysicalPaths;
    }


    private static File[] getExternalFilesDirs(Context context) {
        return context.getExternalFilesDirs(null);
    }
}