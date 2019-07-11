package com.yanxw.graffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class FileUtils {

    private static final String DIR_IMAGE = "local_image";
    private static final String DIR_RECORD = "local_record";
    private static final String FRESCO_CACHE = "fresco_img";
    private static final String DIR_DATA_ROOT = "/data/data/";
    private static final String DIR_INTERNAL_CACHE = "internal";
    private static final String DIR_FILES = "files";

    public static String getDCIMPath() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getInternalCache(Context context) {
        String path = DIR_DATA_ROOT + context.getPackageName();
        if (!TextUtils.isEmpty(path)) {
            String dir = path + File.separator + DIR_INTERNAL_CACHE;
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getAbsolutePath();
        }
        return "";
    }

    public static String getFrescoCache(Context context) {
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)) {
            String dir = path + File.separator + FRESCO_CACHE;
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getAbsolutePath();
        }
        return "";
    }

    public static String getImageCachePath(Context context) {
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)) {
            String root = path + File.separator + DIR_IMAGE;
            File file = new File(root);
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getAbsolutePath();
        }
        return "";
    }

    public static String getFileName(Uri sourceUri) {
        String url = sourceUri.toString();
        String token = sourceUri.getQueryParameter("token");
        String ts = sourceUri.getQueryParameter("ts");
        String timeStamp = sourceUri.getQueryParameter("timeStamp");
        if (!TextUtils.isEmpty(token)) {
            url = url.replace(token, "");
        }
        if (!TextUtils.isEmpty(ts)) {
            url = url.replace(ts, "");
        }
        if (!TextUtils.isEmpty(timeStamp)) {
            url = url.replace(timeStamp, "");
        }
        return url;
    }

    public static File newImageCacheFile(Context context) {
        String root = getImageCachePath(context);
        if (!TextUtils.isEmpty(root)) {
            return new File(root, UUID.randomUUID().toString() + ".jpg");
        }
        return null;
    }

    public static String getCachePath(Context context) {
        String state = Environment.getExternalStorageState();
        String path = "";
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {

            if (Build.VERSION.SDK_INT >= 8) {
                File file = context.getExternalCacheDir();
                if (file != null) {
                    path = file.getAbsolutePath();
                }
                if (TextUtils.isEmpty(path)) {
                    path = Environment.getExternalStorageDirectory()
                            .getAbsolutePath();
                }
            } else {
                path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
            }
        } else if (context.getCacheDir() != null) {
            path = context.getCacheDir().getAbsolutePath();
        }
        return path;
    }

    public static File getRecordPath(Context context) {
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)) {
            String root = new File(path).getParentFile() + File.separator + DIR_RECORD;
            File file = new File(root);
            if (!file.exists()) {
                file.mkdir();
            }
            return file;
        }
        return null;
    }

    public static File getFilesPath(Context context) {
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)) {
            String root = new File(path).getParentFile() + File.separator + DIR_FILES;
            File file = new File(root);
            if (!file.exists()) {
                file.mkdir();
            }
            return file;
        }
        return null;
    }

    public static void deleteFile(String path) {
        try {
            File f = new File(path);
            if (f.isDirectory()) {
                File[] file = f.listFiles();
                if (file != null) {
                    for (File file2 : file) {
                        deleteFile(file2.toString());
                        file2.delete();
                    }
                }
            } else {
                f.delete();
            }
        } catch (Exception e) {

        }
    }

    public static byte[] getFileBytes(String pathName) {
        File file = new File(pathName);
        FileInputStream fis = null;
        byte[] retBytes = null;
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                int len = fis.available();
                retBytes = new byte[len];
                fis.read(retBytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return retBytes;
    }

    public static byte[] getBitmapBytes(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return baos.toByteArray();
    }

    private static String getSuffix(String path) {
        File file = null;
        try {
            file = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getMimeType(String path) {
        String suffix = getSuffix(path);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null && !TextUtils.isEmpty(type)) {
            return type;
        }
        return "file/*";
    }

    public static String getMimeTypeByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            String ext = getExtensionName(name);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (type != null && !TextUtils.isEmpty(type)) {
                return type;
            }
            return "file/*";
        }
        return "file/*";
    }

    public static String getExtensionName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        int dotPosition = fileName.lastIndexOf('.');
        if (dotPosition == -1)
            return "";

        String ext = fileName.substring(dotPosition + 1, fileName.length()).toLowerCase();
        return ext;
    }

    /**
     * 检测内存卡是否可用
     */
    public static boolean isSDcardUsable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取内存卡路径
     */
    public static File getSDcardDir() {
        if (isSDcardUsable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return Environment.getDataDirectory();
        }
    }

    public static String getEntryPath(Context context, String dirName) {
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)) {
            File file = new File(new File(path).getParentFile(), dirName);
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getAbsolutePath();
        }
        return null;
    }


    public static String getOldEntryPath(Context context, String dirName) {
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)) {
            String root = path + File.separator + dirName;
            File file = new File(root);
            if (file.exists()) return file.getAbsolutePath();
        }
        return "";
    }

    public static void writeFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void writeFile(byte[] bfile, File file) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) {
        long size = 0;
        if (f == null) return size;
        File flist[] = f.listFiles();
        if (flist != null && flist.length > 0) {
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSizes(flist[i]);
                } else {
                    size = size + getFileSize(flist[i]);
                }
            }
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        DecimalFormat df = new DecimalFormat("#.00");
        double formatSize = 0d;
        if (size >= gb) {
            formatSize = Double.valueOf(df.format(size * 1.0 / gb));
            return formatSize + "GB";
        } else if (size >= mb) {
            formatSize = Double.valueOf(df.format(size * 1.0 / mb));
            return formatSize + "MB";
        } else {
            formatSize = Double.valueOf(df.format(size * 1.0 / kb));
            if (size == 0)
                return "0KB";
            return formatSize + "KB";
        }
    }

    /**
     * 获取本地路径的文件名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        String result = "";
        if (!TextUtils.isEmpty(filePath)) {
            String[] strs = filePath.split("/");
            if (strs.length > 0) {
                return strs[strs.length - 1];
            }
        }
        return result;
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
            }
        }
    }

    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public static boolean moveDirectory(String srcDirName, String destDirName) {

        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        if (sourceFiles != null && sourceFiles.length > 0) {
            for (File sourceFile : sourceFiles) {
                if (sourceFile.isFile()) {
                    moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
                } else if (sourceFile.isDirectory()) {
                    moveDirectory(sourceFile.getAbsolutePath(),
                            destDir.getAbsolutePath() + File.separator + sourceFile.getName());
                }
            }
        }
        deleteFile(srcDirName);
        return true;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable throwable) {
            }
        }
    }

    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable == null) continue;
            try {
                closeable.close();
            } catch (Throwable throwable) {
            }
        }
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }


    }
}
