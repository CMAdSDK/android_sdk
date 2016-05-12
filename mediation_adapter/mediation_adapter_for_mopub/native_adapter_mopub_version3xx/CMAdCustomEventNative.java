package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.cmcm.adsdk.Const;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;
import com.liehu.nativeads.loaders.mopub.MopubNativeAdManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CMAdCustomEventNative extends CustomEventNative {
    private static final String POSID_ID_KEY = "placement_id";
    @Override
    protected void loadNativeAd(@NonNull Context context,
                                @NonNull CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {

        final String posid;
        if (extrasAreValid(serverExtras)) {
            posid = serverExtras.get(POSID_ID_KEY);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        new CMStaticNativeAd(context, posid, customEventNativeListener).loadAd();
    }


    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        final String posid = serverExtras.get(POSID_ID_KEY);
        return (posid != null && posid.length() > 0);
    }


    static class CMStaticNativeAd extends BaseForwardingNativeAd implements INativeAd.ImpressionListener, INativeAdLoaderListener {
        private static final String SOCIAL_CONTEXT_FOR_AD = "socialContextForAd";
        private final Context mContext;
        private final CustomEventNativeListener mCustomEventNativeListener;
        private INativeAd mNativeAd;
        private String mPosid;
        CMStaticNativeAd(final Context context, String posid,
                               final CustomEventNativeListener customEventNativeListener) {
            mContext = context;
            mCustomEventNativeListener = customEventNativeListener;
            mPosid = posid;
        }

        void loadAd(){
            CMCustomAdProvider.getInstance().loadNativeAd(mContext, mPosid, CMStaticNativeAd.this);
        }


        @Override
        public void prepare(final View view) {
            if(mNativeAd.getAdOnClickListener() == null){
                mNativeAd.setAdOnClickListener(new INativeAd.IAdOnClickListener() {
                    @Override
                    public void onAdClick(INativeAd iNativeAd) {
                        notifyAdClicked();
                    }
                });
            }
            mNativeAd.setImpressionListener(new INativeAd.ImpressionListener() {
                @Override
                public void onLoggingImpression() {
                    notifyAdImpressed();
                }
            });
            mNativeAd.registerViewForInteraction(view);
        }

        @Override
        public void clear(final View view) {
            mNativeAd.unregisterView();
        }

        @Override
        public void destroy() {
        }


        @Override
        public void onLoggingImpression() {
        }

        public void setUpData(@NonNull INativeAd nativeAd) {
            mNativeAd = nativeAd;
            addExtra(MopubNativeAdManager.KEY_AD_TYPE, Const.KEY_CM);
            addExtra(MopubNativeAdManager.KEY_AD_OBJECT, mNativeAd.getAdObject());
            //fixme:pkg name and res
            addExtra(MopubNativeAdManager.KEY_AD_PKG, "com.cmcm.native");
            addExtra(MopubNativeAdManager.KEY_AD_RES, 3009);
            mNativeAd.setImpressionListener(this);
            setTitle(nativeAd.getAdTitle());
            setText(nativeAd.getAdBody());
            setMainImageUrl(nativeAd.getAdCoverImageUrl());
            setIconImageUrl(nativeAd.getAdIconUrl());
            setCallToAction(nativeAd.getAdCallToAction());
            setStarRating(nativeAd.getAdStarRating());
            addExtra(SOCIAL_CONTEXT_FOR_AD, nativeAd.getAdSocialContext());
            final List<String> imageUrls = new ArrayList<String>();
            final String mainImageUrl = getMainImageUrl();
            if (!TextUtils.isEmpty(mainImageUrl)) {
                imageUrls.add(mainImageUrl);
            }
            final String iconUrl = getIconImageUrl();
            if (!TextUtils.isEmpty(iconUrl)) {
                imageUrls.add(iconUrl);
            }
            preCacheImages(mContext, imageUrls, new ImageListener() {
                @Override
                public void onImagesCached() {
                    mCustomEventNativeListener.onNativeAdLoaded(CMStaticNativeAd.this);
                }

                @Override
                public void onImagesFailedToCache(NativeErrorCode errorCode) {
                    mCustomEventNativeListener.onNativeAdFailed(errorCode);
                }
            });
        }

        @Override
        public void adLoaded() {
            INativeAd nativeAd = CMCustomAdProvider.getInstance().getNativeAd(mPosid);
            if (nativeAd != null) {
                setUpData(nativeAd);
            } else {
                mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
            }
        }

        @Override
        public void adFailedToLoad(int errorCode) {
            if (errorCode == 10001) {
                mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            } else if (errorCode == 10002) {
                mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
            } else {
                mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void adClicked(INativeAd iNativeAd) {
        }
    }
}
