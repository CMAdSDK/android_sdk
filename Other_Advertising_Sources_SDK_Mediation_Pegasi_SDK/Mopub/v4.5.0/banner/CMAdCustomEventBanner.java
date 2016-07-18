package com.mopub.nativeads;

import android.content.Context;

import com.cmcm.adsdk.banner.CMAdView;
import com.cmcm.adsdk.banner.CMBannerAdListener;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class CMAdCustomEventBanner extends CustomEventBanner implements CMBannerAdListener {
    private static final String POSID_ID_KEY = "posid";
    private static final String BANNER_SIZE = "size";
    private CustomEventBannerListener mCustomEventBannerListener;
    private String mPosId;
    @Override
    public void loadBanner(Context context, CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mCustomEventBannerListener = customEventBannerListener;
        if (extrasAreValid(serverExtras)) {
            mPosId = serverExtras.get(POSID_ID_KEY);
        } else {
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        CMCustomAdProvider.getInstance().loadBannerAd(context, mPosId,serverExtras.get(BANNER_SIZE), this);
    }
    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        final String posid = serverExtras.get(POSID_ID_KEY);
        return (posid != null && posid.length() > 0);
    }
    @Override
    protected void onInvalidate() {
        CMCustomAdProvider.getInstance().destroy(mPosId);
    }
    @Override
    public void onAdLoaded(CMAdView cmAdView) {
        mCustomEventBannerListener.onBannerLoaded(cmAdView);
    }

    @Override
    public void adFailedToLoad(CMAdView cmAdView, int i) {
        if(i == 10001){
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }else if(i == 10002){
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NO_FILL);
        }else if (i == 10004){
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_TIMEOUT);
        }else{
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
        }
    }

    @Override
    public void onAdClicked(CMAdView cmAdView) {
        mCustomEventBannerListener.onBannerClicked();
    }

}
