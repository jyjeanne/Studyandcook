package fr.wayofcode.studyandcook.utils;

import android.os.Environment;

/**
 * Created by John on 18/01/2017.
 */

public class StorageHelper {

  private static boolean externalStorageReadable, externalStorageWritable;

  public static boolean isExternalStorageReadable() {
    checkStorage();
    return externalStorageReadable;
  }

  public static boolean isExternalStorageWritable() {
    checkStorage();
    return externalStorageWritable;
  }

  public static boolean isExternalStorageReadableAndWritable() {
    checkStorage();
    return externalStorageReadable && externalStorageWritable;
  }

  private static void checkStorage() {
    String state = Environment.getExternalStorageState();
    if (state.equals(Environment.MEDIA_MOUNTED)) {
      externalStorageReadable = externalStorageWritable = true;
    } else if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
      externalStorageReadable = true;
      externalStorageWritable = false;
    } else {
      externalStorageReadable = externalStorageWritable = false;
    }
  }

}
