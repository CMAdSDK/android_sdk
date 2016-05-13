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
/**
 * it is strongly recommended that custom Application
 * and initialized SDK in application#onCreate()
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
        //init sdk
        //The first parameter：Context
        //The second parameter: Mid(the identifier of the app，the first four numbers of posid)
        //The third parameter：product channel id（can be null）
        CMAdManager.applicationInit(this, "1094", "");

        //it is strongly recommended that custom imageload for interstitial
        CMAdManagerFactory.setImageDownloadListener(new MyImageLoadListener());

        //if you want to print debug log ,set it
//      CMAdManager.enableLog();
    }


    /**
     * it is strongly recommended that custom imageLoad to handle image
     */
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
