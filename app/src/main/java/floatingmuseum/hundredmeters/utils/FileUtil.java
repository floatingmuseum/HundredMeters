package floatingmuseum.hundredmeters.utils;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;

import floatingmuseum.hundredmeters.App;

/**
 * Created by Floatingmuseum on 2017/6/29.
 */

public class FileUtil {

    public static String getPathFromUri(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getPathFromUriAbove19(uri);
        } else {
            String fileName = "";
            if (uri.getScheme().compareTo("content") == 0) {
                Cursor cursor = App.context.getContentResolver().query(uri,
                        new String[]{MediaStore.Audio.Media.DATA}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(0);
                    }
                    cursor.close();
                }
            } else if (uri.getScheme().compareTo("file") == 0) {//file:///开头的uri
                fileName = uri.toString().replace("file://", "");
                //替换file://
                if (!fileName.startsWith("/mnt")) {
                    //加上"/mnt"头
                    fileName += "/mnt";
                }
            }
            return fileName;
        }
    }

    private static String getPathFromUriAbove19(Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = App.context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public static String getFileName(@NonNull String path) {
        int lastPoi = path.lastIndexOf('.');
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? path : path.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return path.substring(lastSep + 1);
        }
        return path.substring(lastSep + 1, lastPoi);
    }

    public static String getFileNameWithExtension(@NonNull String path) {
        int lastSep = path.lastIndexOf(File.separator);
        return lastSep == -1 ? path : path.substring(lastSep + 1);
    }
}
