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
        nativeAdManager = new NativeAdManager(this, mAdPosid);
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
                nativeAdManager.loadAd();
                break;
            case R.id.btn_show:
                showAd();
                break;
            default:
                break;
        }
    }

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
            mAdView = OrionNativeAdview.createAdView(getApplicationContext(), ad);
            nativeAdContainer.addView(mAdView);

        }
    }
}
