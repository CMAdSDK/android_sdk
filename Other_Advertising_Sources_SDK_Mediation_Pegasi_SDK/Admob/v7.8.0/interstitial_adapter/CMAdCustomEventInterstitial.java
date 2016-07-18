package com.cmcm.customevent.interstitial;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import java.util.logging.Logger;


public class CMAdCustomEventInterstitial implements CustomEventInterstitial,InterstitialAdCallBack {
    private static final String TAG ="CMAdCustomEventInterstitial";
    private String mPosId;
    private CustomEventInterstitialListener mCustomEventInterstitialListener;
    @Override
    public void requestInterstitialAd(Context context, CustomEventInterstitialListener customEventInterstitialListener, String s, MediationAdRequest mediationAdRequest, Bundle bundle) {
        mCustomEventInterstitialListener = customEventInterstitialListener;
        mPosId = s;
        if (TextUtils.isEmpty(mPosId)) {
            mCustomEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                return;
        }
        CMCustomAdProvider.getInstance().loadInterstitialAd(context, mPosId, this);
    }

    @Override
    public void showInterstitial() {
        CMCustomAdProvider.getInstance().showInterstitialAd(mPosId);
    }

    @Override
    public void onDestroy() {
        CMCustomAdProvider.getInstance().destroy(mPosId);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }
    // cmcm InterstitialAdCallBack
    @Override
    public void onAdLoadFailed(int i) {
        if(i == 10001){
            mCustomEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
        }else if(i == 10002){
            mCustomEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        }
    }

    @Override
    public void onAdLoaded() {
        mCustomEventInterstitialListener.onAdLoaded();
    }

    @Override
    public void onAdClicked() {
        mCustomEventInterstitialListener.onAdClicked();
    }
    @Override
    public void onAdDisplayed() {
        mCustomEventInterstitialListener.onAdLeftApplication();
    }

    @Override
    public void onAdDismissed() {
        mCustomEventInterstitialListener.onAdClosed();
    }

}
