package com.cmcm.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cmcm.picks.vastvideo.VastAd;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity{
    Map<Integer, String> mActionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mActionMap.put(R.id.btn_native_ex, "NativeAdSampleActivity");
        mActionMap.put(R.id.btn_banner, "BannerSampleActivity");
        mActionMap.put(R.id.btn_interstitial, "InterstitalAdSampleActivity");
    }

    public void click(View v){
        if (mActionMap.containsKey(v.getId())) {
            String cls = mActionMap.get(v.getId());
            try {
                Class activityClass = Class.forName("com.cmcm.ads." + cls);
                startActivity(new Intent(this, activityClass));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
