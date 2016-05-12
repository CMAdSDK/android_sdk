package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.cmcm.adsdk.banner.CMAdView;
import com.cmcm.adsdk.banner.CMBannerAdListener;
import com.cmcm.adsdk.banner.CMBannerAdSize;
import com.cmcm.adsdk.banner.CMNativeBannerView;

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
            cmNativeBannerView = new CMNativeBannerView(this);
        }
        cmNativeBannerView.setAdSize(cmBannerAdSize);
        cmNativeBannerView.setPosid(mAdPosid);
        cmNativeBannerView.setAdListener(new CMBannerAdListener() {
            @Override
            public void onAdLoaded(CMAdView ad) {
                mLinearLayout.removeAllViews();
                mLinearLayout.addView(ad);
            }

            @Override
            public void adFailedToLoad(CMAdView ad, int errorCode) {

            }

            @Override
            public void onAdClicked(CMAdView ad) {

            }
        });
        cmNativeBannerView.loadAd();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cmNativeBannerView != null){
            cmNativeBannerView.onDestroy();
        }
    }
}
