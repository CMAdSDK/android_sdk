package com.cmcm.ads;

import android.app.Application;
import com.cmcm.adsdk.CMAdManager;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
        CMAdManager.applicationInit(this, "1094", "");
        CMAdManager.enableLog();
    }

}
