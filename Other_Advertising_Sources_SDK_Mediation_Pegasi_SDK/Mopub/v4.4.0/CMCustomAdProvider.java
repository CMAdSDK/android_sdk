package com.mopub.nativeads;

import android.content.Context;

import com.cmcm.adsdk.banner.CMBannerAdListener;
import com.cmcm.adsdk.banner.CMBannerAdSize;
import com.cmcm.adsdk.banner.CMNativeBannerView;
import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.adsdk.interstitial.InterstitialAdManager;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

import java.util.HashMap;
import java.util.Map;

public class CMCustomAdProvider {
    private static CMCustomAdProvider mInstance;
    private Map<String, NativeAdManager> mNativeLoaderMap = new HashMap<String, NativeAdManager>();
    private Map<String, InterstitialAdManager> mInterstitialLoaderMap = new HashMap<String, InterstitialAdManager>();
    private Map<String, CMNativeBannerView> mBannerLoaderMap = new HashMap<String, CMNativeBannerView>();
    // banner size type
    //CMBannerAdSize BANNER_320_50  == 0;
    public static final  String  BANNER_320_50 = "0";
    // BANNER_300_250 ==1
    public static final String BANNER_300_250 = "1";

    private CMCustomAdProvider(){

    }

    public static synchronized CMCustomAdProvider getInstance(){
        if (mInstance == null) {
            mInstance = new CMCustomAdProvider();
        }
        return mInstance;
    }


    public void loadNativeAd(Context context, String posid, INativeAdLoaderListener loaderListener){
        NativeAdManager nativeAdManager = mNativeLoaderMap.get(posid);
        if(nativeAdManager == null){
            nativeAdManager = new NativeAdManager(context, posid);
            mNativeLoaderMap.put(posid, nativeAdManager);
        }
        nativeAdManager.setNativeAdListener(loaderListener);
        nativeAdManager.loadAd();
    }

    public void loadInterstitialAd(Context context, String posid, InterstitialAdCallBack interstitialAdCallBack){
        InterstitialAdManager interstitialAdManager = mInterstitialLoaderMap.get(posid);
        if(interstitialAdManager == null){
            interstitialAdManager = new InterstitialAdManager(context, posid);
            mInterstitialLoaderMap.put(posid, interstitialAdManager);
        }
        interstitialAdManager.setInterstitialCallBack(interstitialAdCallBack);
        interstitialAdManager.loadAd();
    }

    public void loadBannerAd(Context context, String posid, String size,CMBannerAdListener cmBannerAdListener){
        try {
            CMNativeBannerView cmNativeBannerView = mBannerLoaderMap.get(posid);

            if (cmNativeBannerView == null) {
                cmNativeBannerView = new CMNativeBannerView(context);
                mBannerLoaderMap.put(posid, cmNativeBannerView);
            }
            cmNativeBannerView.setAdListener(cmBannerAdListener);
            cmNativeBannerView.setAdSize(getSize(Integer.parseInt(size)));
            cmNativeBannerView.setPosid(posid);
            cmNativeBannerView.loadAd();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private CMBannerAdSize getSize(int size){
        CMBannerAdSize cmBannerAdSize = CMBannerAdSize.BANNER_300_250;
        if(size ==0){
            cmBannerAdSize = CMBannerAdSize.BANNER_320_50;
        }
        if(size == 1){
            cmBannerAdSize = CMBannerAdSize.BANNER_300_250;
        }
        return cmBannerAdSize;
    }

    public INativeAd getNativeAd(String posid){
        NativeAdManager nativeAdManager = mNativeLoaderMap.get(posid);
        if(nativeAdManager != null) {
            return nativeAdManager.getAd();
        }
        return null;
    }

    public void showInterstitialAd(String posid){
        InterstitialAdManager interstitialAdManager = mInterstitialLoaderMap.get(posid);
        if(interstitialAdManager != null) {
            interstitialAdManager.showAd();
        }
    }

    //当不再需要时可以调用destroy
    public void destroy(String posId){
        NativeAdManager nativeAdManager = mNativeLoaderMap.get(posId);
        if(nativeAdManager != null){
            mNativeLoaderMap.clear();
        }
        InterstitialAdManager interstitialAdManager = mInterstitialLoaderMap.get(posId);
        if(interstitialAdManager != null){
            mInterstitialLoaderMap.clear();
        }
        CMNativeBannerView cmNativeBannerView = mBannerLoaderMap.get(posId);
        if(cmNativeBannerView != null) {
            mBannerLoaderMap.clear();
            cmNativeBannerView.onDestroy();
        }

    }

}
