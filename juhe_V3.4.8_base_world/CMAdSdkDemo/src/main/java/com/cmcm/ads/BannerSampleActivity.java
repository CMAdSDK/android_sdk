package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cmcm.adsdk.banner.CMAdView;
import com.cmcm.adsdk.banner.CMBannerAdListener;
import com.cmcm.adsdk.banner.CMBannerAdSize;
import com.cmcm.adsdk.banner.CMNativeBannerView;
/**
 * Banner Ad
 * The current SDK version only support 320*50 or 300 * 250
 * request banner ad steps(see demo):
 * step1 ,step2 ,step3 ,step4 ,step5 ,step6
 *
 *  notice: if the activity or fragment destory ,strongly suggest destory bannerView
 */
public class BannerSampleActivity extends Activity{
    private FrameLayout mLinearLayout;
    private Button mBtn_load;
    private String mAdPosid = "1094101";
    private CMNativeBannerView cmNativeBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_origin);
        initUI();
    }
    private void initUI(){
        mLinearLayout = (FrameLayout)findViewById(R.id.origin_picks);
        mBtn_load = (Button)findViewById(R.id.btn_load);
        //only support 320*50 or 300 * 250
        mBtn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBanner(CMBannerAdSize.BANNER_320_50);
            }
        });
        Button btn2 = (Button)findViewById(R.id.btn_load2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBanner(CMBannerAdSize.BANNER_300_250);
            }
        });
    }

    private void showBanner(CMBannerAdSize cmBannerAdSize){
        if (cmNativeBannerView == null) {
            //step1 : create CMNativeBannerView ,
            // the parameter : Context
            cmNativeBannerView = new CMNativeBannerView(this);
        }
        //step2 : set size of bannerAd
        //the parameter : bannerSize,only support CMBannerAdSize.BANNER_300_250 or CMBannerAdSize.BANNER_320_50
        cmNativeBannerView.setAdSize(cmBannerAdSize);
        //step3 : set Posid
        cmNativeBannerView.setPosid(mAdPosid);
        //step4 : set callBack listener
        cmNativeBannerView.setAdListener(new CMBannerAdListener() {
            @Override
            public void onAdLoaded(CMAdView ad) {

                mLinearLayout.removeAllViews();
                // step6: load success ,get bannerView and add to your View container.
                mLinearLayout.addView(ad);
            }

            @Override
            public void adFailedToLoad(CMAdView ad, int errorCode) {
                Toast.makeText(BannerSampleActivity.this, "interstitialAd load Failed,error code is"+errorCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClicked(CMAdView ad) {

            }
        });
        // step5: start load bannerAd
        cmNativeBannerView.loadAd();
    }

    /**
     * if the activity or fragment destory ,strongly suggest destory bannerView
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cmNativeBannerView != null){
            cmNativeBannerView.onDestroy();
        }
    }
}
