package com.ct7liang.androidsystemapi.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ct7liang.androidsystemapi.R;
import com.ct7liang.androidsystemapi.util.NetUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 设备网络信息界面
 */
public class NetworkInfoActivity extends AppCompatActivity {

    private TextView tvNetState;
    private TextView tvNetType;
    private TextView tvNetInfo;
    private TextView tvNetInfo2;
    private View flowView;
    private TextView tvRxPackage;
    private TextView tvTxPackage;
    private TextView tvRxByte;
    private TextView tvTxByte;
    private TextView tvNetSpeed;
    private NetworkChangedReceiver networkChangedReceiver;
    private Disposable subscribe1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_net_info);

        tvNetState = findViewById(R.id.tv_net_state);
        tvNetType = findViewById(R.id.tv_net_type);
        tvNetInfo = findViewById(R.id.tv_net_info);
        tvNetInfo2 = findViewById(R.id.tv_net_info2);

        flowView = findViewById(R.id.view_net_speed);
        tvRxPackage = findViewById(R.id.tv_rx_package);
        tvTxPackage = findViewById(R.id.tv_tx_package);
        tvRxByte = findViewById(R.id.tv_rx_byte);
        tvTxByte = findViewById(R.id.tv_tx_byte);
        tvNetSpeed = findViewById(R.id.tv_net_speed);

        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, intentFilter);

        setNetDetail();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i("ct7liang123", "hasFocus: "+hasFocus);
        refreshView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangedReceiver);
        if (subscribe1!=null && !subscribe1.isDisposed()){
            subscribe1.dispose();
        }
    }

    private void refreshView(){
        tvNetState.setText(NetUtils.isNetworkEnable(this)?"可用":"不可用");
        int networkType = NetUtils.getNetworkType(this);
        flowView.setVisibility(View.VISIBLE);
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
            flowView.setVisibility(View.GONE);
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

    private void setNetDetail(){
        subscribe1 = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) {
                while (true){
                    long totalRxBytes = TrafficStats.getTotalRxBytes();
                    emitter.onNext(0L);
                    SystemClock.sleep(3000);
                    emitter.onNext(totalRxBytes);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long o) {
                if (o == 0){
                    long totalRxBytes = TrafficStats.getTotalRxBytes();
                    tvTxPackage.setText(NetUtils.addPoint(TrafficStats.getTotalTxPackets(), false));
                    tvRxPackage.setText(NetUtils.addPoint(totalRxBytes, false));
                    tvRxByte.setText(NetUtils.addPoint(TrafficStats.getTotalRxBytes(), true));
                    tvTxByte.setText(NetUtils.addPoint(TrafficStats.getTotalTxBytes(), true));
                }else{
                    long totalRxBytes = TrafficStats.getTotalRxBytes();
                    if (totalRxBytes != o){
                        long bps = (totalRxBytes - o) / 3;
                        if (bps>1000){
                            tvNetSpeed.setText(bps/1000 + "." + bps%1000 + "k/s");
                        }else{
                            tvNetSpeed.setText("0."+bps+"k/s");
                        }
                    }
                }

            }
        });
    }

}
