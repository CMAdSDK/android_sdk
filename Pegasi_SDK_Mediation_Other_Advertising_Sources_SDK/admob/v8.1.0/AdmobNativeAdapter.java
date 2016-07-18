package com.cmcm.adsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.cmcm.adsdk.Const;
import com.cmcm.adsdk.base.CMBaseNativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.Map;

/**
 * Created by chenhao on 15/12/1.
 */
public class AdmobNativeAdapter extends NativeloaderAdapter {
    @Override
    public void loadNativeAd(@NonNull Context context,
                                @NonNull Map<String, Object> extras) {

        new AdmobNativeAd(context, extras).loadAd();
    }

    @Override
    public int getReportRes() {
        return Const.res.admob;
    }

    @Override
    public String getReportPkgName(String adTypeName) {
        return Const.pkgName.admob;
    }

    @Override
    public String getAdKeyType() {
        return Const.KEY_AB;
    }

    @Override
    public long getDefaultCacheTime() {
        return Const.cacheTime.admob;
    }

    // FIXME: admob 广告的click 监听
    private class AdmobNativeAd extends CMBaseNativeAd implements
            NativeAppInstallAd.OnAppInstallAdLoadedListener,
            NativeContentAd.OnContentAdLoadedListener{
        private NativeAd mNativeAd;
        private Map<String, Object> mExtras;
        private Context mContext;

        public AdmobNativeAd(@NonNull Context context,
                             @Nullable Map<String, Object> extras){
            this.mContext = context.getApplicationContext();
            this.mExtras = extras;
        }

        public void loadAd(){
            String mUnitId = (String)mExtras.get(KEY_PLACEMENT_ID);
            AdLoader adLoader = new AdLoader.Builder(mContext, mUnitId)
                    .forAppInstallAd(this)
                    .forContentAd(this)
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            notifyNativeAdFailed(String.valueOf(errorCode));
                        }

                        @Override
                        public void onAdOpened() {
                            notifyNativeAdClick(AdmobNativeAd.this);
                        }
                    }).withNativeAdOptions(new NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build())
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }

        @Override
        public String getAdTypeName() {
            return Const.KEY_AB;
        }

        @Override
        public boolean registerViewForInteraction(View view) {
            if (view instanceof NativeContentAdView && mNativeAd instanceof NativeContentAd) {
                ((NativeContentAdView) view).setNativeAd(mNativeAd);
            } else if (view instanceof NativeAppInstallAdView && mNativeAd instanceof NativeAppInstallAd) {
                ((NativeAppInstallAdView) view).setNativeAd(mNativeAd);
            }

            if (mImpressionListener != null) {
                mImpressionListener.onLoggingImpression();
            }
            return true;
        }

        @Override
        public void unregisterView() {
        }

        @Override
        public Object getAdObject() {
            return mNativeAd;
        }

        @Override
        public void handleClick() {
        }

        @Override
        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
            setUpData(nativeAppInstallAd);
            mNativeAd = nativeAppInstallAd;

            notifyNativeAdLoaded(AdmobNativeAd.this);
        }

        @Override
        public void onContentAdLoaded(NativeContentAd nativeContentAd) {
            setUpData(nativeContentAd);
            mNativeAd = nativeContentAd;

            notifyNativeAdLoaded(AdmobNativeAd.this);
        }

        private void setUpData(@NonNull NativeAd admobAd) {
            if (admobAd instanceof NativeContentAd){
                NativeContentAd ad = (NativeContentAd) admobAd;
                setTitle(ad.getHeadline().toString());
                setAdBody(ad.getBody().toString());
                if(ad.getImages() != null && ad.getImages().get(0) != null
                        && ad.getImages().get(0).getUri() != null){
                    setAdCoverImageUrl(ad.getImages().get(0).getUri().toString());
                }
                if(ad.getLogo() != null && ad.getLogo().getUri() != null) {
                    setAdIconUrl(ad.getLogo().getUri().toString());
                }
                setAdCallToAction(ad.getCallToAction().toString());
                setIsDownloadApp(false);
                setAdStarRate(0.0d);
            }else if(admobAd instanceof NativeAppInstallAd){
                NativeAppInstallAd ad = (NativeAppInstallAd) admobAd;
                setTitle(ad.getHeadline().toString());
                setAdBody(ad.getBody().toString());
                if(ad.getImages() != null && ad.getImages().get(0) != null
                        && ad.getImages().get(0).getUri() != null){
                    setAdCoverImageUrl(ad.getImages().get(0).getUri().toString());
                }
                if(ad.getIcon() != null && ad.getIcon().getUri() != null) {
                    setAdIconUrl(ad.getIcon().getUri().toString());
                }
                setAdCallToAction(ad.getCallToAction().toString());
                setIsDownloadApp(true);
                try {
                    //此方法内部可能抛出NullPointException
                    setAdStarRate(ad.getStarRating());
                }catch (Exception e){
                    setAdStarRate(0.0d);
                }
            }
        }

    }

}
