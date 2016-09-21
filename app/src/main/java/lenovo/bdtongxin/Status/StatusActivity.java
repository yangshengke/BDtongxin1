package lenovo.bdtongxin.Status;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedHashMap;

import lenovo.bdtongxin.R;

public class StatusActivity extends Activity {

    private static final String ACTION_MSG_INFO_RECEIVED =
            "android.intent.action.beidou.msg.info.received";
    private static final String ACTION_MSG_BD_INFO_RECEIVED =
            "android.intent.action.beidou.msg.bd.info.received";
    private static final String TAG = "HELLO";
    TextView cardNumber;
    TextView tvTime;
    LocationManager mLocationManager;
    HistogramView histogramForBD1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        initCustomActionBar();
        cardNumber = (TextView) findViewById(R.id.CardNumber);
        tvTime = (TextView) findViewById(R.id.TvTime);
        histogramForBD1 = (HistogramView) findViewById(R.id.HistogramViewForBD1);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        thread.start();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationManager.addGpsStatusListener(listener);

//        //获取模块信息
//        ModuleInfoReceiver mModuleInfoReceiver = new ModuleInfoReceiver();
//        IntentFilter moduleFilter = new IntentFilter(ACTION_MSG_INFO_RECEIVED);
//        registerReceiver(mModuleInfoReceiver, moduleFilter);
//        mLocationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,
//                "request_module_info", null);


        //获取北斗模块信息
        BeidouModuleInfoReceiver mBeidouModuleInfoReceiver = new BeidouModuleInfoReceiver();
        IntentFilter BDmoduleFilter = new IntentFilter(ACTION_MSG_BD_INFO_RECEIVED);
        registerReceiver(mBeidouModuleInfoReceiver, BDmoduleFilter);
        mLocationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,
                "request_bd_info", null);
    }

    private boolean initCustomActionBar() {
        ActionBar actionBar = this.getActionBar();
        if (actionBar == null) {
            return false;
        }

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = inflater.inflate(R.layout.action_bar_title_status, null);

        actionBar.setCustomView(titleView, lp);
        actionBar.setDisplayShowHomeEnabled(false);//去掉导航
        actionBar.setDisplayShowTitleEnabled(false);//去掉标题
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        return true;
    }

    /**
     * 获取模块信息
     */
//    private class ModuleInfoReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(ACTION_MSG_INFO_RECEIVED)) {
//                Bundle bundle = intent.getExtras();
//                int module_state = bundle.getInt("module_state");
//                Log.d(TAG, "module_state = " + module_state);
//            }
//        }
//    }

    /**
     * 获取北斗模块信息
     */
    private class BeidouModuleInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_MSG_BD_INFO_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                int service_frequency = bundle.getInt("service_frequency");
                int communication_level = bundle.getInt("communication_level");
                String number = bundle.getString("number");//获取北斗卡号
//                cardNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
                cardNumber.setText(number);
                String version = bundle.getString("version");
                Log.d(TAG, "service_frequency = " + service_frequency
                        + ", communication_level = " + communication_level
                        + ", 北斗卡号 = " + number + ", version" + version);
            }
        }
    }

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == 123) {
                long sysTime = System.currentTimeMillis();
                CharSequence sysTimeStr = DateFormat
                        .format("hh:mm:ss", sysTime);
                tvTime.setText(sysTimeStr);
            }
        }
    };

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(123);
            }
        }
    };

    GpsStatus.Listener listener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                updateHistogram();
            }
        }
    };

    /**
     * 更新信号柱状图
     */
    private void updateHistogram() {
        //获取当前状态
        GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
        //获取卫星颗数的默认最大值
        int maxSatellites = gpsStatus.getMaxSatellites();
        //创建一个迭代器保存所有卫星
        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
        int count = 0;
        LinkedHashMap<Integer, Integer> BD1data = new LinkedHashMap<Integer, Integer>();//BD的<卫星编号,信号强度>
        while (iters.hasNext() && count <= maxSatellites) {
            GpsSatellite s = iters.next();
            int number = s.getPrn(); //卫星的伪随机噪声码，整形数据，即卫星编号
            int str = (int) s.getSnr(); //卫星的信噪比，浮点型数据，即信号强度
            Log.w("signal", "第" + number + "卫星的信噪比为" + str);
            if (number >= 41 && number <= 46) {
                number -= 40;
                BD1data.put(number, str);
            }
            count++;
        }
        histogramForBD1.upDataTextForXY(BD1data);
    }

}