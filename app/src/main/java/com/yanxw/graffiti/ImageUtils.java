package com.yanxw.graffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * ImageUtils
 * Created by yanxinwei on 2017/3/3.
 */

public class ImageUtils {

    /**
     * 从path中获取图片信息
     * @param path
     * @return
     */
    public static Bitmap decodeBitmap(String path, float targetWidth, float targetHeight){
        BitmapFactory.Options op = new BitmapFactory.Options();
        //inJustDecodeBounds
        //If set to true, the decoder will return null (no bitmap), but the out…
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
        //获取比例大小
        int wRatio = (int) Math.ceil(op.outWidth / targetWidth);
        int hRatio = (int) Math.ceil(op.outHeight / targetHeight);
        //如果超出指定大小，则缩小相应的比例
        if(wRatio > 1 && hRatio > 1){
            if(wRatio > hRatio){
                op.inSampleSize = wRatio;
            }else{
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path, op);
        return bmp;
    }

    /**
     * 从path中获取图片信息
     * @param resId
     * @return
     */
    public static Bitmap decodeBitmap(Context context, int resId, float targetWidth, float targetHeight){
        BitmapFactory.Options op = new BitmapFactory.Options();
        //inJustDecodeBounds
        //If set to true, the decoder will return null (no bitmap), but the out…
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId, op); //获取尺寸信息
        //获取比例大小
        int wRatio = (int) Math.ceil(op.outWidth / targetWidth);
        int hRatio = (int) Math.ceil(op.outHeight / targetHeight);
        //如果超出指定大小，则缩小相应的比例
        if(wRatio > 1 && hRatio > 1){
            if(wRatio > hRatio){
                op.inSampleSize = wRatio;
            }else{
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeResource(context.getResources(), resId, op);
        return bmp;
    }

}
