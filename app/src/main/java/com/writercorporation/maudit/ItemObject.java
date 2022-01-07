package com.writercorporation.maudit;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by hemina.shah on 4/22/2016.
 */
public class ItemObject {
    private String name;
    private Bitmap bitmap;
    private boolean isSelectedItem=false;
    private Uri fileUri;
    private int size;
    public ItemObject(String name, Bitmap bitmap,boolean isSelectedItem) {
        this.name = name;
        this.bitmap = bitmap;
        this.isSelectedItem=isSelectedItem;
    }

    public int getSize()
    {
        return  size;
    }
    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSelectedItem() {
        return isSelectedItem;
    }

    public void setIsSelectedItem(boolean isSelected,int position) {
        this.isSelectedItem = isSelected;
        this.size=position;
    }
}
