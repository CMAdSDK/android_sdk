package com.cmcm.ads.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.ads.R;
import com.cmcm.ads.utils.VolleyUtil;
import com.cmcm.baseapi.ads.INativeAd;

public class OrionNativeAdview extends FrameLayout {

    final protected Context mContext;
    public INativeAd mNativeAd;
    protected View mNativeAdView;

    public static OrionNativeAdview createAdView(Context context, INativeAd ad) {
        OrionNativeAdview view = new OrionNativeAdview(context);
        view.initAdView(ad);

        return view;
    }

    public OrionNativeAdview(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public OrionNativeAdview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public OrionNativeAdview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    public void initAdView(INativeAd ad) {
            mNativeAdView = View.inflate(mContext, R.layout.native_ad_layout, this);
            String iconUrl = ad.getAdIconUrl();
            ImageView iconImageView = (ImageView) mNativeAdView
                    .findViewById(R.id.big_iv_icon);
            if (iconUrl != null) {
                VolleyUtil.loadImage(iconImageView, iconUrl);
            }
            //get image url
            String mainImageUrl = ad.getAdCoverImageUrl();
            if (!TextUtils.isEmpty(mainImageUrl)) {
                ImageView imageViewMain = (ImageView) mNativeAdView
                        .findViewById(R.id.iv_main);
                imageViewMain.setVisibility(View.VISIBLE);
                VolleyUtil.loadImage(imageViewMain, mainImageUrl);
            }
            TextView titleTextView = (TextView) mNativeAdView.findViewById(R.id.big_main_title);
            TextView subtitleTextView = (TextView) mNativeAdView.findViewById(R.id.big_sub_title);
            Button bigButton = (Button) mNativeAdView.findViewById(R.id.big_btn_install);
            TextView bodyTextView = (TextView) mNativeAdView.findViewById(R.id.text_body);
            titleTextView.setText(ad.getAdTitle());
            subtitleTextView.setText(ad.getAdSocialContext());
            bigButton.setText(ad.getAdCallToAction());
            bodyTextView.setText(ad.getAdBody());

        if (mNativeAd != null) {
            mNativeAd.unregisterView();
        }
        mNativeAd = ad;
        //register view for ad
        mNativeAd.registerViewForInteraction(mNativeAdView);
    }
}
