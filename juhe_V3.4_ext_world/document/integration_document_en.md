#PEGASI SDK Integration Description Document（world_ext version）
##1、Introduction
Overseas version 3.4, include Native、Banner、Interstitial... Ad style，also include Facebook、Mopub、Yahoo、Admob third-party Native Ad style SDK integration。
##2、Prepare work
###2.1、Add aar

before add aar file must find the .aar file in libs folder
![image load failed](http://i.imgur.com/zdihsa7.png)

Copy aar file to libs of project，add compile(name: ' cmadsdk_world_release_V3.4', ext: 'aar') in dependent tag of Gradle script.

	dependencies {
		compile(name: 'cmadsdk_ext_world_V3.4.0', ext: 'aar')
	}
	
![image load failed](http://i.imgur.com/Qdy0uQh.png)
	
notify:if the app need admob ad you must add:

		compile(name: 'play-services-ads-7.8.0', ext: 'aar')
	    compile(name: 'play-services-analytics-7.8.0', ext: 'aar')
	    compile(name: 'play-services-base-7.8.0', ext: 'aar')
![image load failed](http://i.imgur.com/YQsu7D3.png)

###2.2、Modify AndroidManifest.xml
Add permission：

	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

![image load failed](http://i.imgur.com/cFsbomZ.png)
	
other configuration（mopub and admob）：
	
if app need mopub ad must add this:

	<!--for Mopub -->
	 <activity android:name="com.mopub.mobileads.MoPubActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
     <activity android:name="com.mopub.mobileads.MraidActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
     <activity android:name="com.mopub.common.MoPubBrowser"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
     <activity android:name="com.mopub.mobileads.MraidVideoPlayerActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"/>
	<!--for Mopub-->
![image load failed](http://i.imgur.com/9idSGG2.png)
	
if app need mopub ad must add this:

	<!--for admob-->
    <meta-data android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />	
![image load failed](http://i.imgur.com/IdTAiOz.png)
###2.3、Init SDK(strongly recommended that initialization in the onCreate method of Application)
	Suggesting in OnCreate() method of Application initialize sdk，for example：   
	@Override
	public void onCreate() {
    	super.onCreate();   
   		 //The first parameter：Context
    	//The second parameter: Mid(the identifier of the app，the first four numbers of posid)   
        //The third parameter：product channel id（can be null）
    	CMAdManager.applicationInit(this, "Your AppId", "You channel ID");
	}		
![image load failed](http://i.imgur.com/DHARzA9.png)
##3. Code Integration
###3.1、 Native	
####3.1.1、loadAd and showAd
	 // The first parameter：Context
	 // The second parameter：Posid
	 if(nativeAdManagerEx == null){
 	    nativeAdManagerEx = new NativeAdManagerEx(this, "Your posid");
     }
 	 nativeAdManagerEx.setNativeAdListener(new INativeAdLoaderListener() {
		       @Override
		       public void adLoaded() {
				     //get ad
		       		 INativeAd ad = nativeAdManagerEx.getAd();
		       		 if (ad == null) {
                   			 return;
                      }
				    
				     //Initialize the ad view,and use the elemnets of the 
				     //INativeAd object to fill the mAdView					//the layout should be prepared by yourself
		           	 View mAdView = View.inflate(MainActivity.this,"Your Ad layout", null);
						
		           
		           
		            //Bind the ad with the mAdView
		            //notice: this step is necessary，if don't ,the event like 
		            //click of the ad will not effective.		            //unregisterView should be used when the ad no need to show.
		             ad.registerViewForInteraction(mAdView);
				
		             if (mAdView != null) {
	                    // remove old mAdView
	                    nativeAdContainer.removeView(mAdView);
                	  }
		            	//add the mAdView into the layout of view container.
		            	//(the container should be prepared by youself)
	            	 nativeAdContainer.addView(mAdView); 
       		  }

            @Override
            public void adFailedToLoad(int i) {
              // load failed
            }


            @Override
            public void adClicked(INativeAd ad) {
            	//ad clicked
            }
        });
     
  		nativeAdManagerEx.loadAd();
  	
  	

####3.1.2、Native API
- com.cmcm.adsdk.nativead.NativeAdManagerEx
<table>
<tbody>
<tr><td>Methodology</td><td>method introduction</td></tr>
<tr><td>NativeAdManagerEx(Context context, String posid)</td><td>NativaAdManagerExconstruct function</td></tr>
<tr><td>setNativeAdListener(INativeAdLoaderListener listener)</td><td>set callback interface</td></tr>
<tr><td>loadAd( )</td><td>concurrent load ads</td></tr>
<tr><td>preloadAd( )</td><td>sequence load ads</td></tr>
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



###3.2.1、Banner Ad integration

	//Parameter：Context
	CMNativeBannerView bannerView = new CMNativeBannerView(this);
	//Set banner Size（requested）
    bannerView.setAdSize(CMBannerAdSize);
	//Set posid（requested）
	bannerView.setPosid(mAdPosid);
	//set callback listener
	bannerView.setAdListener(new CMBannerAdListener() {
        @Override
         public void onAdLoaded(CMAdView ad) {
         	  //after load success and need to display ad must invoke this method 
         	  cmNativeBannerView.prepare();
              mLinearLayout.removeAllViews();
              mLinearLayout.addView(ad);
         }

        @Override
        public void adFailedToLoad(CMAdView ad, int errorCode) {
				//load failed
        }

        @Override
        public void onAdClicked(CMAdView ad) {

        }
    });
    //load ad
    bannerView.loadAd();
    
when Activity destory，suggest destory bannerView

```
 @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cmNativeBannerView != null){
            cmNativeBannerView.onDestroy();
        }
    }
```

####3.2.2、The Main APIs for Native Banner
- com.cmcm.adsdk.banner.CMNativeBannerView

<table>
<table>
<tbody>
<tr><td>Methodology</td><td>method description</td></tr>
<tr><td>setPosid(String posid)</td><td>Set ad position id</td></tr>
<tr><td>setAdSize(CMBannerSize size)</td><td>Set banner size</td></tr>
<tr><td>setAdListener(CMBannerAdListener listener)</td><td>Set banner callback interface </td></tr>
<tr><td>loadAd()</td><td>downloading ad</td></tr>
<tr><td>prepare()</td><td>after the success of the load needs to display ads invoke this method</td></tr>
</tbody>
</table>

- com.cmcm.adsdk.banner.CMBannerAdSize

<table>
<tbody>
<tr><td>Parameter name</td><td>Parameters description</td></tr>
<tr><td>CMBannerAdSize.BANNER_320_50</td><td>320*50 size</td></tr>
<tr><td>CMBannerAdSize.BANNER_300_250</td><td>300*250 size</td></tr>
</tbody>
</table>


- com.cmcm.adsdk.banner.CMBannerAdListener

<table>
<tbody>
<tr><td>Methodology</td><td>method description</td></tr>
<tr><td>onAdLoaded(CMAdView view)</td><td>ad request success</td></tr>
<tr><td>adFailedToLoad(CMAdView view, int error)</td><td>ad request failure</td></tr>
<tr><td>onAdClicked(CMAdView view)</td><td>ad click</td></tr>
</tbody>
</table>

###3.3.1、InterstitialAd integration

		//init InterstitialAdManager
		//parameter：Context , posid
		InterstitialAdManager interstitialAdManager＝new InterstitialAdManager(Context, posid)
		//set this callback interface if you need callback。
		interstitialAdManager.setInterstitialCallBack(new InterstitialAdCallBack() {
					@Override
					public void onAdLoadFailed(int errorCode) {
						//request failed								
					}
					@Override
					public void onAdLoaded() {
						／／request success
					}

					@Override
					public void onAdClicked() {
						//ad clicked	
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
```
start load InterstitialAd 

```
interstitialAdManager.loadAd();
```
display InterstitialAd 

```
interstitialAdManager.showAd();
```
###3.3.2、The Main APIs for Native Interstitial

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
ote: There is no picture loading function of the SDK, need to add the picture loading function from outside. Please set the load function before sending request of interstitial Ads. Please add the following code at the init:

CMAdManagerFactory.setImageDownloaderDelegate(new MyImageLoadListener());

see example at Demo MyAppLication.
![image load failed](http://i.imgur.com/1fQTPtt.png)

##4、Proguard script
Add the following script in your proguard script:
  
###4.1.1、NativeAd Proguard script
	-dontwarn com.cmcm.adsdk.**
	-dontwarn com.cleanmaster.**	
	-keep class  com.cmcm.adsdk.** { *;}
	-keep class  com.cleanmaster.** { *;}
![image load failed](http://i.imgur.com/N8VUOdM.png)
###4.1.2、add facebook Proguard script
		
    -dontwarn com.facebook.ads.**
	-keep class com.facebook.ads.**{*;}
![image load  failed](http://i.imgur.com/y70XWL0.png)
    
###4.1.3、add Mopub Proguard script

	-keep class  com.mopub.nativeads.*{*;}
	-keep class  com.mopub.common.**{*;}
	-keep class  com.mopub.network.**{*;}
	-dontwarn com.mopub.**
	-keep class com.cmcm.adsdk.nativead.MopubNativeAdLoader{
    	    <fields>;
     	    <methods>;
	}
	-keep class com.mopub.mobileads.MoPubActivity{
      	    <fields>;
			<methods>;
	}
	-keep class com.mopub.mobileads.MraidActivity{
    	    <fields>;
			<methods>;
	}
	-keep class com.mopub.common.MoPubBrowser{
    	    <fields>;
			<methods>;
	}
	-keep class com.mopub.mobileads.MraidVideoPlayerActivity{
    	    <fields>;
			<methods>;
	}
	
![image load failed](http://i.imgur.com/0HAmapT.png)
###4.1.4、Admob Proguard script

	-keep class com.cmcm.adsdk.nativead.AdmobNativeLoader{
  	      <fields>;
  	      <methods>;
	}
	-keep public class com.google.android.gms.ads.**{
   		public *;
	}
	# For old ads classes
	-keep public class com.google.ads.**{
   		public *;
	}

	# For mediation
	-keepattributes *Annotation*
	# Other required classes for Google Play Services
	# Read more at http://developer.android.com/google/play-services/setup.html
	-keep class * extends java.util.ListResourceBundle {
   		protected Object[][] getContents();
	}
	-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   		public static final *** NULL;
	}
	-keepnames @com.google.android.gms.common.annotation.KeepName class *
	-keepclassmembernames class * {
   		@com.google.android.gms.common.annotation.KeepName *;
	}
	-keepnames class * implements android.os.Parcelable {
   		public static final ** CREATOR;
	}

![image load failed](http://i.imgur.com/hxnrtyE.png)
###4.1.5、Yahoo Proguard script
   
    -keep class com.flurry.** {*;}
    -dontwarn com.flurry.**
    -keepattributes *Annotation*,EnclosingMethod
    -keepclasseswithmembers class * {
      public <init>(android.content.Context, android.util.AttributeSet, int);
    }
    -keep class com.google.android.gms.ads.** {*;}
    -dontwarn com.google.android.gms.ads.**
    -keep class com.google.android.gms.** {*;}
    -dontwarn com.google.android.gms.**

![image load failed](http://i.imgur.com/qujAxBl.png)
	
##5、Error Code

<table>
<tbody>
<tr><td>ErrorCode</td><td>instruction</td></tr>
<tr><td>10001</td><td>lack the settings of the ad position，(perhaps lacks sdk initialization，or the cloud end closes the ad position）</td></tr>
<tr><td>10002</td><td>lacks ad data</td></tr>
<tr><td>10003</td><td>internal error</td></tr>
<tr><td>10004</td><td>request timeout</td></tr>
<tr><td>10005</td><td>There is no this type of advertising</td></tr>
</tbody>
</table>


