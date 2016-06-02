
package com.cmcm.ads.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Properties;

/**
 * 网络工具类
 */
public class NetUtil {

    private static final String TAG = "NetUtil";

    /**
     * 需要设置代理的接入点
     */
    private final static String CMWAP = "cmwap";
    private final static String THREEGWap = "3gwap";
    private final static String UNIWAP = "uniwap";
    private final static String CTWAP = "ctwap";
    private static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");


    private static void clearProxy() {
        Properties prop = System.getProperties();
        prop.remove("proxySet");
        prop.remove("http.proxyHost");
        prop.remove("http.proxyPort");
    }

    /**
     * 网络类型
     * <p>
     * 通知各页面网络情况
     *
     * @author Owen Xie
     * @version [shoujikong_market, 2013-2-21]
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_2G = 1;
    public static final int NETWORK_TYPE_3G = 2;
    public static final int NETWORK_TYPE_4G = 5;
    public static final int NETWORK_TYPE_WIFI = 3;
    public static final int NETWORK_TYPE_NONE = 4;

    /**
     * @author lin
     * @param context
     * @return 返回5种网络类型，0-->未知网络，1-->2G网络，2-->3G网络，3-->wifi网络，4-->无网络
     *         注意：只有wifi类型的判断是准确的，其他类型相对都有适配问题
     */
    public static int getNetworkState(Context context) {
        if (context == null)
            return NETWORK_TYPE_UNKNOWN;

        int networkType = NETWORK_TYPE_NONE;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                int type = info.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    if (isWiFiActive(context)) {
                        networkType = NETWORK_TYPE_WIFI;
                    } else {
                        networkType = NETWORK_TYPE_NONE;
                    }
                } else {
                    int subType = info.getSubtype();
                    if (isMobile2G(subType)) {
                        networkType = NETWORK_TYPE_2G;
                    } else if (isMobile3G(subType)) {
                        networkType = NETWORK_TYPE_3G;
                    } else if (isMobile4G(subType)) {
                        networkType = NETWORK_TYPE_4G;
                    }
                }
            } else {
                networkType = NETWORK_TYPE_NONE;
            }
        } catch (Exception ex) {
            networkType = NETWORK_TYPE_UNKNOWN;
        }

        return networkType;
    }

    /**
     * 判断Wifi是否可用
     * 
     * @deprecated 由于有USB连接导致判断不准 最好不用这个方法
     * @author yang (10.17修改)
     * @since 10.17
     * @return true:可用 false:不可用
     */
    public static boolean isWiFiActive(Context context) {
        boolean bReturn = false;
        try {
            if (context == null) {
                return false;
            }
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected())
            {
                int type = info.getType();
                if (type == ConnectivityManager.TYPE_WIFI)
                {
                    bReturn = true;
                }
            }
        } catch (Exception e) {
        }
        return bReturn;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
                if (connectivityManager != null) {
                    NetworkInfo activeNetInfo = connectivityManager
                            .getActiveNetworkInfo();// 获取网络的连接情况
                    if (activeNetInfo != null && activeNetInfo.isAvailable()
                            && activeNetInfo.isConnected()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是否2G网络
     * 
     * @param type
     * @return
     */
    private static boolean isMobile2G(int type) {
        boolean is2G = false;
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                is2G = true;
                break;
            default:
                is2G = false;
                break;
        }
        return is2G;
    }

    /**
     * 判断是否是3G网络
     * 
     * @param type
     * @return
     */
    private static boolean isMobile3G(int type) {
        boolean is3G = false;
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                is3G = true;
                break;
            default:
                is3G = false;
                break;
        }
        return is3G;
    }

    /**
     * 判断是否是4G网络
     * 
     * @param type
     * @return
     */
    private static boolean isMobile4G(int type) {
        boolean is4G = false;
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                is4G = true;
                break;
            default:
                is4G = false;
                break;
        }
        return is4G;
    }

    private static synchronized ContentValues getContentValue(Context context) {
        ContentValues values = new ContentValues();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    PREFERRED_APN_URI, null, null, null, null);
            if (c != null) {
                int colCount = c.getColumnCount();
                boolean isSuccess = c.moveToFirst();
                if (isSuccess) {
                    for (int j = 0; j < colCount; j++) {
                        values.put(c.getColumnName(j), c.getString(j));
                    }
                }
            }
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return values;
    }

    /**
     * 获取Sdk的版本号
     */
    public static int getPhoneSDKByInt() {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception e) {
            // it's weird adding a try...catch here ...
            return 0;
        }
    }

}
