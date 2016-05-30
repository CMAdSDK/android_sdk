package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.cmcm.adsdk.config.RequestUFS;
import com.cmcm.adsdk.utils.Assure;
import com.cmcm.picks.gaid.AdvertisingIdHelper;
import com.mopub.common.ClientMetadata;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeResponse;
import com.mopub.nativeads.RequestParameters;

import java.util.Map;

/**
 * Created by chenhao on 15/12/1.
 */
public class MopubNativeAdapter extends NativeloaderAdapter {
    private final String PREFS_NAME = "cmcmadsdk_config";
    private static SharedPreferences sSharedPreferences;
    @Override
    public void loadNativeAd(@NonNull Context context,
                             @NonNull Map<String, Object> extras) {

        sSharedPreferences = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        new MopubNativeAd(context, extras).loadAd();
    }

    @Override
    public int getReportRes() {
        return Const.res.mopub;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return String.format("%s.%s", Const.pkgName.mopub, adTypeName);
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_MP;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.mopub;
    }

    private class MopubNativeAd extends CMBaseNativeAd implements MoPubNative.MoPubNativeListener {
        private NativeResponse mNativeResponse;
        private Map<String, Object> mExtras;
        private Context mContext;
        private View mAdView;
        public MopubNativeAd(@NonNull Context context,
                             @Nullable Map<String, Object> extras){
            this.mContext = context;
            this.mExtras = extras;
        }

        public void loadAd(){
            String mUnitId = (String)mExtras.get(KEY_PLACEMENT_ID);
            Assure.checkNotNull(mUnitId);
            MoPubNative moPubNative = new MoPubNative(mContext, mUnitId,  MopubNativeAd.this);
            String gaId = AdvertisingIdHelper.getInstance().getGAId();
            gaId.trim();
            boolean dnt = AdvertisingIdHelper.getInstance().getTrackFlag();
            ClientMetadata clientMetadata = ClientMetadata.getInstance(mContext);
            if (!TextUtils.isEmpty(gaId)) {
                clientMetadata.setAdvertisingInfo(gaId, dnt);
            }

            RequestParameters.Builder builder = new RequestParameters.Builder();
            String keyWords = RequestUFS.getInstance(mContext).getUFSInfo();
            if(!TextUtils.isEmpty(keyWords)) {
                builder.keywords(keyWords);
                moPubNative.makeRequest(builder.build());
            }else {
                moPubNative.makeRequest();
            }
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_MP;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            mAdView = view;
            mNativeResponse.recordImpression(view);
            if(null != mImpressionListener){
                mImpressionListener.onLoggingImpression();
            }
            return false;
        }

        @Override
        public void unregisterView() {
            if (null != mAdView) {
                mNativeResponse.clear(mAdView);
            }
        }

        @Override
        public Object getAdObject() {
            return mNativeResponse;
        }

        @Override
        public void handleClick() {
            mNativeResponse.handleClick(mAdView);
        }

        @Override
        public void onNativeLoad(NativeResponse nativeResponse) {
            mNativeResponse = nativeResponse;
            setTitle(nativeResponse.getTitle());
            setAdBody(nativeResponse.getText());
            setAdCallToAction(nativeResponse.getCallToAction());
            setAdCoverImageUrl(nativeResponse.getMainImageUrl());
            setAdIconUrl(nativeResponse.getIconImageUrl());
            String starRating = nativeResponse.getStarRating() + "";
            if (starRating.equals("null")) {
                starRating = "0";
            }
            setAdStarRate(Double.parseDouble(starRating));
            notifyNativeAdLoaded(this);
        }

        @Override
        public void onNativeFail(NativeErrorCode nativeErrorCode) {
            notifyNativeAdFailed(nativeErrorCode.toString());
        }

        @Override
        public void onNativeImpression(View view) {
            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
        }

        @Override
        public void onNativeClick(View view) {

        }
    }

}
