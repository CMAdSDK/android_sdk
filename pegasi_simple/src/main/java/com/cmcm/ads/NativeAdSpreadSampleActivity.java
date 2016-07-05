package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cmcm.ads.ui.OrionNativeAdview;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.adsdk.nativead.NativeAdManagerEx;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

/**
 * Created by $ liuluchao@cmcm.com on 2016/7/4.
 */
public class NativeAdSpreadSampleActivity  extends Activity implements View.OnClickListener {

    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;//View Container
    private String mAdPosid =  "1094101";
    private OrionNativeAdview mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nativeAdContainer = (FrameLayout) findViewById(R.id.big_ad_container);
        findViewById(R.id.btn_req).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        initNativeAd();
    }

    private void initNativeAd() {
        //setp1 : create nativeAdManager
        //The first parameter：Context
        //The second parameter: posid
        nativeAdManager = new NativeAdManagerEx(this, mAdPosid);

        //setp2 : set callback listener(INativeAdLoaderListener)
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                //ad load  success ,you can do other something here;


                Toast.makeText(NativeAdSpreadSampleActivity.this, "ad load  success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(NativeAdSpreadSampleActivity.this, "ad load  failed,error code is:"+i, Toast.LENGTH_LONG).show();
            }

            @Override
            public void adClicked(INativeAd iNativeAd) {
                Toast.makeText(NativeAdSpreadSampleActivity.this, "ad click", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_req:
                //step3 : start load nativeAd
                nativeAdManager.loadAd();
                break;
            case R.id.btn_show:
                showAd();
                break;
            default:
                break;
        }
    }

    /**
     * if load nativeAd success,you can get and show nativeAd;
     */
    private void showAd(){
        if(nativeAdManager != null){

            INativeAd ad = nativeAdManager.getAd();
            if (ad == null) {
                Toast.makeText(NativeAdSpreadSampleActivity.this,
                        "no native ad loaded!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mAdView != null) {
                // remove old ad view
                nativeAdContainer.removeView(mAdView);
            }
            //the mAdView is custom by publisher
            mAdView = OrionNativeAdview.createAdView(getApplicationContext(), ad);

            //add the mAdView into the layout of view container.(the container should be prepared by youself)
            nativeAdContainer.addView(mAdView);

        }
    }
}
