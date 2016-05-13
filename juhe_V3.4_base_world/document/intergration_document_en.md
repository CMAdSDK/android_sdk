#Integration Description Document

##1、Introduction
Oversea Version 3.4 includes Native, Native Banner and Native Interstitial Ads formats.
##2、Prepare work
###2.1、Add aar
before add aar file must find the .aar file in libs folder
![image load failed ](http://i.imgur.com/A3d8tnv.png)

Copy aar file to libs of project，add compile(name: 'cmadsdk_world_release_V3.4', ext: 'aar') in dependent tag of Gradle script.

    dependencies {
		compile(name: 'cmadsdk_world_release_V3.4', ext: 'aar')
		
	}

![image load failed](http://i.imgur.com/va9cHVe.png)
		
###2.2、Modify AndroidManifest.xml

Add permission：

	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

![image load failed](http://i.imgur.com/1DS3cRI.png)
	
###2.3、Init SDK
Suggesting in OnCreate() method of Application initialize sdk，for example：   
	
	@Override
	public void onCreate() {
    	super.onCreate();   
    	//The first parameter：Context
    	//The second parameter: Mid(the identifier of the app，the first four numbers of posid)       		
        //The third parameter：product channel id（can be null）
    	CMAdManager.applicationInit(this, "Your AppId", "You channel ID");
	}		

![image load failed](http://i.imgur.com/apDtCYf.png)
##3. Code Integration
###3.1、 Native	
####3.1.1、loadAd and showAd
	 // The first parameter：Context
	 // The second parameter：Posid
 	if(nativeAdManager == null) {
            nativeAdManager = new NativeAdManager(this, "your posId");
        }
     nativeAdManager.setNativeAdListener(new INativeAdLoader.INativeAdLoaderListener() {
        	@Override
         	public void adLoaded() {
				INativeAd ad = nativeAdManager.getAd();
                View mAdView = View.inflate(MainActivity.this,"Your Ad layout", null);
                //get ad data by InatveAd object and rander ad view
                //register view
                mNativeAd.registerViewForInteraction(mAdView);
				//note：requested event
				//unregisterView  please call this method when ad display is not needed, quitting the interface view, etc.
				//ad display View
				 nativeAdContainer.addView(mAdView);
            }
            @Override
            public void adFailedToLoad(int errorCode) {
				//ad request failure callback            }
            @Override
            public void adClicked(INativeAd ad) {
				//ad click callback
            }
        });     
		nativeAdManager.loadAd();
####3.1.2、Native API
- com.cmcm.adsdk.nativead.NativeAdManager
<table>
<tbody>
<tr><td>Methodology</td><td>method introduction</td></tr>
<tr><td>NativeAdManager(Context context, String posid)</td><td>NativaAdManager construct function </td></tr>
<tr><td>setNativeAdListener(INativeAdLoaderListener listener)</td><td>set callback interface</td></tr>
<tr><td>loadAd( )</td><td>load ads </td></tr>
<tr><td>getAd( )</td><td>get ads</td></tr>
</tbody>
</table>

- com.cmcm.baseapi.ads.INativeAd
<table>
<tbody>
<tr><td>Methodology</td><td>method introduction</td></tr>
<tr><td>getAdtitle()</td><td>ad title</td></tr>
<tr><td>getAdBody()</td><td>ad description</td></tr>
<tr><td>getAdIconUrl()</td><td>ad icon url</td></tr>
<tr><td>getAdCoverImageUrl()</td><td>ad background picture Url</td></tr>
<tr><td>getAdCallToAction()</td><td>ad button text</td></tr>
<tr><td>getAdStarRating()</td><td>ad star score（can be null）</td></tr>
<tr><td>getAdSocialContext()</td><td>ad download amount or website（can be null）</td></tr>
<tr><td>hasExpired()</td><td> check ad expired（true mean expired）</td></tr>
<tr><td>isDownLoadApp()</td><td>check download type app (true: yes false: no null: no information</td></tr>
<tr><td>setImpressionListener(ImpressionListener listener)</td><td>set callback after SDK ad impression report </td></tr>
<tr><td>registerViewForInteraction(View view)</td><td>bundle ad view with ad itself(requested) </td></tr>
<tr><td>unregisterView()</td><td>unbundling ad view with ad itself(requested)</td></tr>
</tbody>
</table>




###3.2、Banner Ad integration

	//Parameter：Context
	CMNativeBannerView bannerView = new CMNativeBannerView(this);
	//Set banner Size（requested）
    bannerView.setAdSize(CMBannerAdSize);
	//Set posid（requested）
	bannerView.setPosid(mAdPosid);
	bannerView.setAdListener(new CMBannerAdListener() {
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
    bannerView.loadAd();
	
####3.2.1、The Main APIs for Native Banner

- com.cmcm.adsdk.banner.CMNativeBannerView
<table>
<tbody>
<tr><td>Methodology</td><td>method description</td></tr>
<tr><td>setPosid(String posid)</td><td>Set ad position id</td></tr>
<tr><td>setAdSize(CMBannerSize size)</td><td>Set banner size</td></tr>
<tr><td>setAdListener(CMBannerAdListener listener)</td><td>Set banner callback interface </td></tr>
<tr><td>loadAd()</td><td>downloading ad</td></tr>
</tbody>
</table>

- com.cmcm.adsdk.banner.CMBannerAdListener
<table>
<tbody>
<tr><td>Methodology</td><td>method description</td></tr>
<tr><td>onAdLoaded(CMAdView view)</td><td>ad request implement</td></tr>
<tr><td>adFailedToLoad(CMAdView view, int error)</td><td>ad request failure</td></tr>
<tr><td>onAdClicked(CMAdView view)</td><td>ad click</td></tr>
</tbody>
</table>

###3.2、interstitial Ad integration

	if(interstitialAdManager == null) {
			interstitialAdManager = new InterstitialAdManager(this, posid);
		}
		interstitialAdManager
				.setInterstitialCallBack(new InterstitialAdCallBack() {
					@Override
					public void onAdLoadFailed(int errorCode) {
						
					}

					@Override
					public void onAdLoaded() {
						interstitialAdManager.showAd();
						
					}

					@Override
					public void onAdClicked() {

					}

					@Override
					public void onAdDisplayed() {

					}

					@Override
					public void onAdDismissed() {

					}
				});
		interstitialAdManager.loadAd();
	


###3.3.1、The Main APIs for Native Interstitial

- com.cmcm.adsdk.interstitial.InterstitialAdManager
<table>
<tbody>
<tr><td>Methodology</td><td>method description</td></tr>
<tr><td>loadAd()</td><td>ad downloading</td></tr>
<tr><td>showAd()</td><td>ad display</td></tr>
<tr><td>setInterstitialCallBack(InterstitialAdCallBack listener)</td><td>set callback interface </td></tr>
<tr><td>setInterstialOverClickEnable(boolean enable)</td><td>set whether the shaded are response or not when cliked (no response in default)</td></tr>
</tbody>
</table>
- com.cmcm.adsdk.interstitial.InterstitialAdCallBack
<table>
<tbody>
<tr><td>Methodology</td><td>method description</td></tr>
<tr><td>onAdLoaded( )</td><td>ad request implement</td></tr>
<tr><td>onAdLoadFailed( )</td><td>ad request failure</td></tr>
<tr><td>onAdClicked( )</td><td>ad click</td></tr>
<tr><td>onAdDisplayed( )</td><td>ad display</td></tr>
<tr><td>onAdDismissed(int errorcode)</td><td>click ‘close’ button to close ad unit</td></tr>
</tbody>
</table>
Note: There is no picture loading function of the SDK, need to add the picture loading function from outside. Please set the load function before sending request of interstitial Ads. Please add the following code at the init:
 
 `CMAdManagerFactory.setImageDownloaderDelegate(new MyImageLoadListener()); `

 see example at Demo MyAppLication.

![image load failed](http://i.imgur.com/qDxyypA.png)

##4、Proguard script

Add the following script in your proguard script:
	
	-keep class com.cmcm.adsdk.** { *;}

  ![image load failed](http://i.imgur.com/825Fo9v.png)

##5、Error Code

<table>
<tbody>
<tr><td>ErrorCode</td><td>instruction</td></tr>
<tr><td>10001</td><td>lack the settings of the ad position，(perhaps lacks sdk initialization，or the cloud end closes the ad position）</td></tr>
<tr><td>10002</td><td>lacks ad data</td></tr>
<tr><td>10003</td><td>internal error</td></tr>
<tr><td>10004</td><td>request timeout</td></tr>
</tbody>
</table>

	
