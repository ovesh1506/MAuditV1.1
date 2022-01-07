package com.writercorporation.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hemina.shah on 4/22/2016.
 */
public class    BitmapLoader {
    public static int getScale(int originalWidth,int originalHeight,final int requiredWidth,final int requiredHeight){
        int scale=1;
        if((originalWidth>requiredWidth) || (originalHeight>requiredHeight)){
            if(originalWidth<originalHeight)
                scale=Math.round((float)originalWidth/requiredWidth);
            else
                scale=Math.round((float)originalHeight/requiredHeight);
        }
        return scale;
    }

    public static BitmapFactory.Options getOptions(String filePath,int requiredWidth,int requiredHeight){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(filePath,options);
        options.inSampleSize=getScale(options.outWidth,options.outHeight, requiredWidth, requiredHeight);
        options.inJustDecodeBounds=false;
        return options;
    }

    public static Bitmap loadBitmap(String filePath,int requiredWidth,int requiredHeight,String siteCode){

        BitmapFactory.Options options= getOptions(filePath,requiredWidth, requiredHeight);
        options.inMutable=true;
        Bitmap capturedImage = BitmapFactory.decodeFile(filePath,options);
        Canvas canvas = new Canvas(capturedImage);
        Paint paint = new Paint();
        paint.setTextSize(14);
        paint.setColor(Color.RED);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        canvas.drawText("DateTime: "+sdf.format(new Date()), 10, 20, paint);
        canvas.drawText("SiteCode: "+siteCode, 10, 40, paint);

        return capturedImage;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }
}
