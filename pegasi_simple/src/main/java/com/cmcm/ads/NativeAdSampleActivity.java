package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.ads.ui.OrionNativeAdview;
import com.cmcm.ads.utils.VolleyUtil;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.adsdk.nativead.NativeAdManagerEx;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;
import com.cmcm.picks.loader.Ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Native Ad (the Ad type include fb , cm , admob , yahoo , mopub )
 * request Native Ad steps : step1 , step2 , step3
 */
public class NativeAdSampleActivity extends Activity implements OnClickListener {

    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;//View Container
	private String mAdPosid =  "1094100";
    private View mAdView = null;
    private INativeAd mNativeAd;
    private  View  mNativeAdView;

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
        nativeAdManager = new NativeAdManager(this, mAdPosid);

        //setp2 : set callback listener(INativeAdLoaderListener)
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                //ad load  success ,you can do other something here;


                Toast.makeText(NativeAdSampleActivity.this, "ad load  success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(NativeAdSampleActivity.this, "ad load  failed,error code is:"+i, Toast.LENGTH_LONG).show();
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
            mAdView = createAdView(ad);
            //add the mAdView into the layout of view container.(the container should be prepared by youself)
            nativeAdContainer.addView(mAdView);
        }
    }


    private View createAdView(INativeAd ad){

       if( ((Ad)ad.getAdObject()).getAppShowType() !=Ad.SHOW_TYPE_NEWS_THREE_PIC){
           mNativeAdView = View.inflate(getApplicationContext(), R.layout.native_ad_layout,null);
           String mainImageUrl = ad.getAdCoverImageUrl();
           if (!TextUtils.isEmpty(mainImageUrl)) {
               ImageView imageViewMain = (ImageView) mNativeAdView
                       .findViewById(R.id.iv_main);
               imageViewMain.setVisibility(View.VISIBLE);
               VolleyUtil.loadImage(imageViewMain, mainImageUrl);
           }
       }else{
           //native three pics
           mNativeAdView = View.inflate(getApplicationContext(),R.layout.native_ad_three_pics,null);
           List<String> pics = ad.getExtPics();
           ImageView img1 = (ImageView)mNativeAdView.findViewById(R.id.iv_pic1);
           ImageView img2 =(ImageView)mNativeAdView.findViewById(R.id.iv_pic2);
           ImageView img3 = (ImageView)mNativeAdView.findViewById(R.id.iv_pic3);
           List<ImageView> imgList = new ArrayList<ImageView>();
           imgList.add(img1);
           imgList.add(img2);
           imgList.add(img3);
           if(!pics.isEmpty()) {
               for (int i = 0; i < imgList.size(); i++) {
                   VolleyUtil.loadImage(imgList.get(i), pics.get(i));
               }
           }
       }
        TextView titleTextView = (TextView) mNativeAdView.findViewById(R.id.big_main_title);
        TextView subtitleTextView = (TextView) mNativeAdView.findViewById(R.id.big_sub_title);
        Button bigButton = (Button) mNativeAdView.findViewById(R.id.big_btn_install);
        TextView bodyTextView = (TextView) mNativeAdView.findViewById(R.id.text_body);
        //fill ad data
        titleTextView.setText(ad.getAdTitle());
        subtitleTextView.setText(ad.getAdTypeName());
        bigButton.setText(ad.getAdCallToAction());
        bodyTextView.setText(ad.getAdBody());
        String iconUrl = ad.getAdIconUrl();
        ImageView iconImageView = (ImageView) mNativeAdView
                .findViewById(R.id.big_iv_icon);
        if (iconUrl != null) {
            VolleyUtil.loadImage(iconImageView, iconUrl);
        }
        if (mNativeAd != null) {
            mNativeAd.unregisterView();
        }
        mNativeAd = ad;
        // step2: register view for ad
        mNativeAd.registerViewForInteraction(mNativeAdView);

        return mNativeAdView;
    }

}
