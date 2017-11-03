package com.schaffer.base.common.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by AndroidSchaffer on 2017/11/3.
 */

public class BannerGlideLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).thumbnail(0.5f).into(imageView);
    }
}
