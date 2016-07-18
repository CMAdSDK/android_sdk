package com.mopub.nativeads;


import android.content.Context;

import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class CMAdCustomEventInterstitial extends CustomEventInterstitial implements InterstitialAdCallBack {
    private static final String POSID_ID_KEY = "posid";
    private String mPosId;
    private CustomEventInterstitialListener mCustomEventInterstitialListener;
    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mCustomEventInterstitialListener = customEventInterstitialListener;
        if (extrasAreValid(serverExtras)) {
            mPosId = serverExtras.get(POSID_ID_KEY);
        } else {
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        CMCustomAdProvider.getInstance().loadInterstitialAd(context, mPosId, CMAdCustomEventInterstitial.this);
    }

    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        final String posid = serverExtras.get(POSID_ID_KEY);
        return (posid != null && posid.length() > 0);
    }

    @Override
    protected void showInterstitial() {
        CMCustomAdProvider.getInstance().showInterstitialAd(mPosId);
    }

    @Override
    protected void onInvalidate() {
        CMCustomAdProvider.getInstance().destroy(mPosId);
    }

    @Override
    public void onAdLoadFailed(int i) {
        if(i == 10001){
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }else if(i == 10002){
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }else if(i == 10004){
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_TIMEOUT);
        }else{
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
        }
    }

    @Override
    public void onAdLoaded() {
        mCustomEventInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onAdClicked() {
        mCustomEventInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onAdDisplayed() {
        mCustomEventInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onAdDismissed() {
        mCustomEventInterstitialListener.onInterstitialDismissed();
    }

}
