package com.ct7liang.androidsystemapi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.List;

public class NetUtils {

    /**
     * 获取网络流量数据
     */
    public static void getNetFlow(){
//        static long  getMobileRxBytes()  //获取通过Mobile连接收到的字节总数，不包含WiFi
//        static long  getMobileRxPackets()  //获取Mobile连接收到的数据包总数
//        static long  getMobileTxBytes()  //Mobile发送的总字节数
//        static long  getMobileTxPackets()  //Mobile发送的总数据包数
//        static long  getTotalRxBytes()  //获取总的接受字节数，包含Mobile和WiFi等
//        static long  getTotalRxPackets()  //总的接受数据包数，包含Mobile和WiFi等
//        static long  getTotalTxBytes()  //总的发送字节数，包含Mobile和WiFi等
//        static long  getTotalTxPackets()  //发送的总数据包数，包含Mobile和WiFi等
//        static long  getUidRxBytes(int uid)  //获取某个网络UID的接受字节数
//        static long  getUidRxPackets(int uid)  //获取某个网络UID的接受数据包总数
//        static long  getUidTxBytes(int uid) //获取某个网络UID的发送字节数
//        static long  getUidTxPackets(int uid) //获取某个网络UID的发送数据包总数
    }

    private static String formatSize(long size){
        if (size < 1024){
            return size+"B";
        }else if (size < 1024*1024){
            return size/1024+"."+size%1024+"K";
        }else{
            return size/(1024*1024)+"."+size%(1024*1024)+"M";
        }
    }
    public static String addPoint(long size, boolean isAppend){
        StringBuffer sb = new StringBuffer();
        String s1 = String.valueOf(size);
        int j=0;
        for (int i = s1.length()-1; i > -1 ; i--) {
            j++;
            sb.append(s1.charAt(i));
            if (j%3==0 && i!=0){
                j = 0;
                sb.append(",");
            }
        }
        if (isAppend){
            String s = formatSize(size);
            return sb.reverse().toString()+"K (" + s + ")";
        }else{
            return sb.reverse().toString();
        }
    }


    /**
     * 判断当前网络是否可用
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * @return true:可用 false:不可用
     */
    public static boolean isNetworkEnable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * @return  -1:无网络 0:移动数据 1:wifi网络
     */
    public static int getNetworkType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }


    /**
     * 获取运营商类型
     * @param context context
     * @return -1:unknown,  0:移动,  1:联通,  2:电信
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static int getOperatorType(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            return -1;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simOperator = telephonyManager.getSimOperator();
        if (simOperator == null){
            return -1;
        }
        switch (telephonyManager.getSimOperator()){
            case "46000":
            case "46002":
            case "46007":
            case "46008":
                return 0;
            case "46001":
            case "46006":
            case "46009":
                return 1;
            case "46003":
            case "46005":
            case "46011":
                return 2;
            default:
                return -1;
        }
    }

    /**
     * 获取运营商网络等级
     * @param context
     * @return 0:unknown 2:2G 3:3G 4:4G
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static int getNetWorkLevel(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            /*
             GPRS : 2G(2.5) General Packet Radia Service 114kbps
             EDGE : 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
             UMTS : 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
             CDMA : 2G 电信 Code Division Multiple Access 码分多址
             EVDO_0 : 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
             EVDO_A : 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
             1xRTT : 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
             HSDPA : 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
             HSUPA : 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
             HSPA : 3G (分HSDPA,HSUPA) High Speed Packet Access
             IDEN : 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
             EVDO_B : 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
             LTE : 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
             EHRPD : 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
             HSPAP : 3G HSPAP 比 HSDPA 快些
             */
            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 2;
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return 3;
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            default:
                return 0;
        }
    }

    /**
     * 获取wifi的ssid
     * @param context
     * @return ssid
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> 危险权限
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> 危险权限
     */
    public static String getWifiSSID(Context context) {
        String ssid="unknown ssid";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT>=Build.VERSION_CODES.P) {
            WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = mWifiManager.getConnectionInfo();
            ssid = info.getSSID();
            return ssid.replace("\"", "");
        }else{
//            Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1
            ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            ssid = networkInfo.getExtraInfo();
            return ssid.replace("\"", "");
        }
    }

    /**
     * 判断 wifi 是否是 5G 频段.
     * 需要权限:
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    public static boolean isWifi5G(Context context, String ssid) {
        int freq = 0;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult scanResult : scanResults) {
                if (scanResult.SSID.equals(ssid)) {
                    freq = scanResult.frequency;
                    break;
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }
}
