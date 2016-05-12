package com.cmcm.ads.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cmcm.adsdk.CMAdManager;

import java.io.File;

public class VolleyUtil {

    public static ImageLoader sImageLoader = new ImageLoader(Volley.newRequestQueue(
            CMAdManager.getContext()), new BitmapLruCache());

    public static void loadImage(final ImageView view, String url) {
        /**
         +         * patch: http://trace-abord.cm.ijinshan.com/index/dump?version=4250026&date=20150628&thever=21&dumpkey=2745934861&field=%E6%97%A0&field_content=
         +         * 由于之前版本出现多进程访问volley，导致部分文件被写坏而发生崩溃，现在先清理一次缓存
         +         *
         * */
        doCleanAllCache();

        sImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if(null != response.getBitmap()){
                    view.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    public static void loadImage(String url, ImageLoader.ImageListener listener) {
        doCleanAllCache();

        sImageLoader.get(url, listener);
    }

    private static ImageLoader.ImageListener preloadImageListener ;

    private static void doCleanAllCache() {
        /** Default on-disk cache directory. */
        final String DEFAULT_CACHE_DIR = "volley";
        File cacheDir = new File(CMAdManager.getContext().getCacheDir(), DEFAULT_CACHE_DIR);

        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }


    public static void preloadImage(final String imageUrl){
        if(TextUtils.isEmpty(imageUrl)){
            return;
        }

        /**
         +         * patch: http://trace-abord.cm.ijinshan.com/index/dump?version=4250026&date=20150628&thever=21&dumpkey=2745934861&field=%E6%97%A0&field_content=
         +         * 由于之前版本出现多进程访问volley，导致部分文件被写坏而发生崩溃，现在先清理一次缓存
         +         *
         * */
        doCleanAllCache();

        //预加载的复用同一个ImageListener
        if(null == preloadImageListener){
            preloadImageListener = new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            };
        }
        sImageLoader.get(imageUrl, preloadImageListener);
    }

    public static class BitmapLruCache extends android.support.v4.util.LruCache<String, Bitmap> implements ImageLoader.ImageCache {
        public static int getDefaultLruCacheSize() {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            return cacheSize;
        }

        public BitmapLruCache() {
            this(getDefaultLruCacheSize());
        }

        public BitmapLruCache(int sizeInKiloBytes) {
            super(sizeInKiloBytes);
        }

        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            if (bitmap == null) {
                return 0;
            }
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }

        @Override
        public Bitmap getBitmap(String url) {

            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);

        }
    }


}
