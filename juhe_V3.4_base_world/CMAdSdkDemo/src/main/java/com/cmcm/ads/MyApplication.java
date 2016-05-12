package com.cmcm.ads;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cmcm.ads.utils.VolleyUtil;
import com.cmcm.adsdk.BitmapListener;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.CMAdManagerFactory;
import com.cmcm.adsdk.ImageDownloadListener;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
        //init sdk
        CMAdManager.applicationInit(this, "1094", "");
        //for interstitial
        CMAdManagerFactory.setImageDownloadListener(new MyImageLoadListener());
        //for debug
//        CMAdManager.enableLog();
    }
    class MyImageLoadListener implements ImageDownloadListener {

        @Override
        public void getBitmap(String url, final BitmapListener imageListener) {
            if(TextUtils.isEmpty(url)){
                if(imageListener != null) {
                    imageListener.onFailed("url is null");
                }
                return;
            }
            VolleyUtil.loadImage(url, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (imageListener != null) {
                        imageListener.onFailed(volleyError.getMessage());
                    }
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer != null && imageContainer.getBitmap() != null) {
                        if (imageListener != null) {
                            imageListener.onSuccessed(imageContainer.getBitmap());
                        }
                    }
                }
            });
        }
    }

}
