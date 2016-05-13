package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cmcm.adsdk.interstitial.InterstitialAdCallBack;
import com.cmcm.adsdk.interstitial.InterstitialAdManager;
/**
 * InterstitalAd
 *
 * request interstitalAd steps(see demo):
 * step1 , step2 , step3 , step4
 *
 */
public class InterstitalAdSampleActivity extends Activity {
	private InterstitialAdManager interstitialAdManager;
	private Button showBtn;
	private Button loadBtn;
	private String posid = "1094104";
	private boolean isAdReady = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interstitial);
		showBtn = (Button) findViewById(R.id.btn_show);
		loadBtn = (Button)findViewById(R.id.btn_load);
		loadBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestInstitialAd();
			}
		});

		showBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//step4 : if load interstitialAd success ,
				//        you can show Ad by InterstitialAdManager#showAd()
				if(interstitialAdManager != null && isAdReady) {
					interstitialAdManager.showAd();
				}
			}
		});
	}


	private void requestInstitialAd() {
		if(interstitialAdManager == null) {
			//step1 : create interstitialAdManager
			//The first parameter：Context
			//The second parameter: posid
			interstitialAdManager = new InterstitialAdManager(this, posid);
		}
		//step2 : set callBack listener if you need .
		interstitialAdManager
				.setInterstitialCallBack(new InterstitialAdCallBack() {
					@Override
					public void onAdLoadFailed(int errorCode) {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd load Failed errorcode:"+errorCode,Toast.LENGTH_LONG).show();
					}

					@Override
					public void onAdLoaded() {
						Toast.makeText(InterstitalAdSampleActivity.this, "interstitialAd load success",Toast.LENGTH_LONG).show();
						isAdReady = true;
					}

					@Override
					public void onAdClicked() {

					}

					@Override
					public void onAdDisplayed() {
						//ad display
					}

					@Override
					public void onAdDismissed() {
						//click close button that ad destory
					}
				});
		isAdReady = false;
		//step3 : start load interstitialAd by InterstitialAdManager#loadAd()
		interstitialAdManager.loadAd();
	}
}
