#天马SDK V3.3海外base版接入说明文档
##1、base版本说明
   base只包含猎户NativeAd接入

##2.接入工作
	 studio开发工具下
	 解压压缩包，将aar文件复制到工程的libs文件夹下。在Gradle脚本dependencies节点下添加以下脚本即可。
	 compile(name: cmadsdk_base_world_V3.3.0', ext: 'aar')
     
###2.2、修改AndroidManifest.xml文件

添加权限说明：
	
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.INTERNET" />
    
###2.3、 初始化
	
	请在Application的OnCreate()方法中初始化SDK，示例代码：	
	@Override
	public void onCreate() {
		super.onCreate();	
        //初始化聚合sdk
        //第一个参数：Context
		//第二个参数：MId（Posid的前四位数字）
        //第三个参数：产品渠道号Id，没有可填""
        CMAdManager.applicationInit(this, "Your Mid", "You channel ID");
	}

##3. 接入代码

####3.1、 Native广告接入

####3.1.1、请求广告和展示广告	
	 //第一个参数：Context
	 //第二个参数：Posid
 	 NativeAdManager nativeAdManager = new NativeAdManager(this, "Your posid");
     nativeAdManager.setNativeAdListener(new INativeAdLoader.INativeAdLoaderListener() {
        	@Override
         	public void adLoaded() {
				INativeAd ad = nativeAdManager.getAd();
                View mAdView = View.inflate(MainActivity.this,
                        "Your Ad layout", null);
                //通过广告对象获取广告数据渲染AdView
                //将广告View和广告对象绑定，绑定后将自动给广告View添加点击事件，监控广告的点击
                mNativeAd.registerViewForInteraction(mAdView);
				//注意：此操作必须，如果不进行绑定将广告展示将作废，也不会自动添加点击事件。
				//unregisterView请在此广告不需要展示时调用，比如界面退出时调用。
				//展示广告View
				 nativeAdContainer.addView(mAdView);
            }
            @Override
            public void adFailedToLoad(int errorCode) {
				//请求广告失败回调
            }
            @Override
            public void adClicked(INativeAd ad) {
				//广告被点击回调
            }
        });     
		nativeAdManager.loadAd();

####3.1.2、Native广告主要API
- com.cmcm.adsdk.NativeAdManager
<table>
<tbody>
<tr><td>方法名</td><td>方法说明</td></tr>
<tr><td>NativeAdManager(Context context, String posid)</td><td>NativaAdManager构造函数</td></tr>
<tr><td>setNativeAdListener(INativeAdLoaderListener listener)</td><td>设置回调接口</td></tr>
<tr><td>loadAd( )</td><td>加载广告</td></tr>
<tr><td>getAd( )</td><td>获取广告</td></tr>
</tbody>
</table>

- com.cmcm.baseapi.ads.INativeAd
<table>
<tbody>
<tr><td>方法名</td><td>方法说明</td></tr>
<tr><td>getAdtitle()</td><td>广告标题</td></tr>
<tr><td>getAdBody()</td><td>广告描述</td></tr>
<tr><td>getAdIconUrl()</td><td>广告Icon的Url</td></tr>
<tr><td>getAdCoverImageUrl()</td><td>广告背景图片Url</td></tr>
<tr><td>getAdCallToAction()</td><td>广告按钮的文案</td></tr>
<tr><td>getAdStarRating()</td><td>广告的评分条（可能为空）</td></tr>
<tr><td>getAdSocialContext()</td><td>广告的下载量或网址（可能为空）</td></tr>
<tr><td>hasExpired()</td><td>广告是否过期（true为过期）</td></tr>
<tr><td>isDownLoadApp()</td><td>获取是否下载类型app true 是；false 否；null 未知</td></tr>
<tr><td>setImpressionListener(ImpressionListener listener)</td><td>设置SDK广告上报展示后回调</td></tr>
<tr><td>registerViewForInteraction(View view)</td><td>将广告View和广告进行绑定（必须）</td></tr>
<tr><td>unregisterView()</td><td>将广告和其绑定的广告View进行解绑（必须）</td></tr>
</tbody>
</table>


##4、混淆脚本

在你的混淆脚本中添加以下脚本：
    -dontwarn com.cmcm.adsdk.*
    -keep class com.cmcm.adsdk.** { *;}

##5、错误码

<table>
<tbody>
<tr><td>Error Code</td><td>说明</td></tr>
<tr><td>10001</td><td>没有此广告位的配置（请检查是否初始化了sdk，posid是否和mid对应）</td></tr>
<tr><td>10002</td><td>没有广告数据（请确认网络环境是否海外网络，如果不是请用VPN尝试）</td></tr>
<tr><td>10003</td><td>内部错误</td></tr>
<tr><td>10004</td><td>请求超时</td></tr>
</tbody>
</table>



