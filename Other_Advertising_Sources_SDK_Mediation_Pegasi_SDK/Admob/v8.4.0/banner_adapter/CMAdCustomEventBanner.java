package com.cmcm.customevent.banner;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.cmcm.adsdk.banner.CMAdView;
import com.cmcm.adsdk.banner.CMBannerAdListener;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CMAdCustomEventBanner implements CustomEventBanner,CMBannerAdListener {
    private CustomEventBannerListener mCustomEventBannerListener;
    private static final String KEY_POSID= "posid";
    private static final String KEY_SIZE ="size";
    private String mPosId;
    private String mSize;
    private Map<String,String> styleMap = new HashMap<String,String>();
    //s1  json 样式 {"posid":"1094108","size":"0"}
    @Override
    public void requestBannerAd(CustomEventBannerListener customEventBannerListener, Activity activity, String s, String s1, AdSize adSize, MediationAdRequest mediationAdRequest, Object o) {
        mCustomEventBannerListener = customEventBannerListener;
        Log.d("CMAdCustomEventBanner", "s1 is" + s1);
        if (TextUtils.isEmpty(s1)) {
            mCustomEventBannerListener.onFailedToReceiveAd();
            return;
        }
        parserData(s1);
        if (TextUtils.isEmpty(styleMap.get(KEY_POSID)) || TextUtils.isEmpty(styleMap.get(KEY_SIZE))) {
            mCustomEventBannerListener.onFailedToReceiveAd();
            return;
        }
        Log.d("CMAdCustomEventBanner","posid is"+ styleMap.get(KEY_POSID));
        Log.d("CMAdCustomEventBanner","size is"+ styleMap.get(KEY_SIZE));
        CMCustomAdProvider.getInstance().loadBannerAd(activity.getApplicationContext(), styleMap.get(KEY_POSID),styleMap.get(KEY_SIZE), this);
    }
    public void parserData(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            mPosId = (String) jsonObject.get(KEY_POSID);
            mSize = (String)jsonObject.get(KEY_SIZE);
            styleMap.put(KEY_POSID,mPosId);
            styleMap.put(KEY_SIZE,mSize);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void destroy() {
        if(!styleMap.isEmpty()){
            styleMap.clear();
        }
        CMCustomAdProvider.getInstance().destroy(styleMap.get(KEY_POSID));
    }

    @Override
    public void onAdLoaded(CMAdView cmAdView) {
        mCustomEventBannerListener.onReceivedAd(cmAdView);
    }

    @Override
    public void adFailedToLoad(CMAdView cmAdView, int i) {
        mCustomEventBannerListener.onFailedToReceiveAd();
    }

    @Override
    public void onAdClicked(CMAdView cmAdView) {
        mCustomEventBannerListener.onClick();
    }
}
