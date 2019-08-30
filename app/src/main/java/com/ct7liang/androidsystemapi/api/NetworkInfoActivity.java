package com.ct7liang.androidsystemapi.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.ct7liang.androidsystemapi.R;
import com.ct7liang.androidsystemapi.util.NetUtils;

public class NetworkInfoActivity extends AppCompatActivity {

    private TextView tvNetState;
    private TextView tvNetType;
    private TextView tvNetInfo;
    private TextView tvNetInfo2;
    private NetworkChangedReceiver networkChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_net_info);

        tvNetState = findViewById(R.id.tv_net_state);
        tvNetType = findViewById(R.id.tv_net_type);
        tvNetInfo = findViewById(R.id.tv_net_info);
        tvNetInfo2 = findViewById(R.id.tv_net_info2);

        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, intentFilter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i("ct7liang123", "hasFocus: "+hasFocus);
        refreshView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangedReceiver);
    }

    private void refreshView(){
        tvNetState.setText(NetUtils.isNetworkEnable(this)?"可用":"不可用");
        int networkType = NetUtils.getNetworkType(this);
        if (networkType == 0){
            tvNetType.setText("移动数据");
            switch (NetUtils.getOperatorType(this)){
                case 0:
                    tvNetInfo.setText("中国移动");
                    break;
                case 1:
                    tvNetInfo.setText("中国联通");
                    break;
                case 2:
                    tvNetInfo.setText("中国电信");
                    break;
                default:
                    tvNetInfo.setText("未知运营商");
                    break;
            }
            switch (NetUtils.getNetWorkLevel(this)){
                case 2:
                    tvNetInfo2.setText("2G");
                    break;
                case 3:
                    tvNetInfo2.setText("3G");
                    break;
                case 4:
                    tvNetInfo2.setText("4G");
                    break;
                default:
                    tvNetInfo2.setText("未知");
                    break;
            }
        }else if (networkType == 1){
            tvNetType.setText("WiFi网络");
            tvNetInfo.setText(NetUtils.getWifiSSID(this));
            tvNetInfo2.setText(NetUtils.isWifi5G(this, tvNetInfo.getText().toString())?"(5G频段)":"(非5G频段)");
        }else{
            tvNetType.setText("当前无可用网络");
            tvNetInfo.setText("当前无可用网络");
            tvNetInfo2.setText("");
        }
    }

    /**
     * 监听网络状态发生变化的广播
     */
    public class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshView();
        }
    }
}
