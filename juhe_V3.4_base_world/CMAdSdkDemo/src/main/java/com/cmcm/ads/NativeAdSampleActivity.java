package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cmcm.ads.ui.OrionNativeAdview;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;

/**
 * Native Ad (the Ad resource only from picks(cm) )
 * request Native Ad steps : step1 , step2 , step3
 */
public class NativeAdSampleActivity extends Activity implements OnClickListener {

    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;
    private Button loadAdButton;
	private String mAdPosid =  "1094101";
    private OrionNativeAdview mAdView = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nativeAdContainer = (FrameLayout) findViewById(R.id.big_ad_container);
		loadAdButton = (Button) findViewById(R.id.btn_req);
		loadAdButton.setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        initNativeAd();
    }

    private void initNativeAd() {
        //setp1 : create nativeAdManager
        //The first parameter：Context
        //The second parameter: posid
        nativeAdManager = new NativeAdManager(this, mAdPosid);

        //setp2 : set callback listener(INativeAdLoaderListener)
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                Toast.makeText(NativeAdSampleActivity.this, "ad load  success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(NativeAdSampleActivity.this, "ad load  failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void adClicked(INativeAd iNativeAd) {
                Toast.makeText(NativeAdSampleActivity.this, "ad click", Toast.LENGTH_LONG).show();
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
                Toast.makeText(NativeAdSampleActivity.this,
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
