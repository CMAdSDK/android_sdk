package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

// Marked by vincent_peng ++
//import static com.mopub.nativeads.NativeImageHelper.preCacheImages;
// --

public class CMAdCustomEventNative extends CustomEventNative {
    private static final String LOG_TAG = CMAdCustomEventNative.class.getSimpleName();
    private static final String POSID_ID_KEY = "posid";

    // Add by vincent_peng ++
    private static final String APP_ID_KEY = "appid";
    private static final String CHANNEL_ID_KEY = "channelid";
    // --
    @Override
    public void loadNativeAd(@NonNull Context activity,
                                @NonNull CustomEventNativeListener customEventNativeListener,
                                @NonNull Map<String, Object> localExtras,
                                @NonNull Map<String, String> serverExtras) {
        // Add by vincent_peng ++
        if(serverExtras != null) {
            checkCMInited(activity, serverExtras.get(APP_ID_KEY), serverExtras.get(CHANNEL_ID_KEY));
        }

//        checkCMInited(activity, "1094", "");
        // --

        final String posid;
        if (extrasAreValid(serverExtras)) {
            posid = serverExtras.get(POSID_ID_KEY);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        new CMStaticNativeAd(activity, posid, customEventNativeListener).loadAd();
    }


    private static boolean extrasAreValid(final Map<String, String> serverExtras) {
        if(serverExtras == null) {
            return false;
        }
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
            mContext = context.getApplicationContext();
            mCustomEventNativeListener = customEventNativeListener;
            mPosid = posid;
        }

        void loadAd(){
            CMCustomAdProvider.getInstance().loadNativeAd(mContext, mPosid, CMStaticNativeAd.this);
        }


        @Override
        public void prepare(final View view) {
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
            notifyAdImpressed();
        }

        public void setUpData(@NonNull INativeAd nativeAd) {
            mNativeAd = nativeAd;
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
            if (mainImageUrl != null) {
                imageUrls.add(getMainImageUrl());
            }
            final String iconUrl = getIconImageUrl();
            if (iconUrl != null) {
                imageUrls.add(getIconImageUrl());
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
            Log.d(LOG_TAG, "adLoaded " + nativeAd.getAdTitle());
            if (nativeAd != null) {
                setUpData(nativeAd);
            } else {
                mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
            }
        }

        @Override
        public void adFailedToLoad(int errorCode) {
            Log.w(LOG_TAG, "error: " + errorCode);
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
            notifyAdClicked();
        }
    }

    // add by vincent_peng. To init CM sdk ++
    static AtomicBoolean s_CMInited = new AtomicBoolean(false);
    static void checkCMInited(Context context, String appId, String channelId) {
        if(!s_CMInited.get()) {
            Log.d(LOG_TAG, "init CMSDK mid: " + appId + ", channel id: " + channelId);
            CMAdManager.applicationInit(context.getApplicationContext(), appId, channelId == null ? "" : channelId);
            CMAdManager.enableLog();
            s_CMInited.set(true);
        }
    }
    // --
}
