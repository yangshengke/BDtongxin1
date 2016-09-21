package lenovo.bdtongxin.SMS;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lenovo.bdtongxin.R;


public class HomeSMSActivity extends Activity {

    private ListView listView;
    private HomeSMSAdapter adapter;
    private RexseeSMS rsms;
    private Button newSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initCustomActionBar();
        init();
    }

    private void init() {
        setContentView(R.layout.home_sms_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//运行时设定屏幕方向:不随SENSOR改变

        listView = (ListView) findViewById(R.id.select_contacts_list);
        adapter = new HomeSMSAdapter(HomeSMSActivity.this);

        rsms = new RexseeSMS(HomeSMSActivity.this);
        //这其实就是把两个uri中拥有相同thread_id列的其他列（即 消息片段，thread_id，消息个数 + 电话号码，日期，已读状态）整合起来，
        // 对每一行所对应的这些列添加到SMSBean对象中，并把所有的这些每一行所组成的对象添加到list_mmt中．
        // 如果这两个uri中无数据，则只是new一个list_mmt，里面是没东西的
        List<SMSBean> list_mmt = rsms.getThreadsNum(rsms.getThreads(0));

        //把list_mmt中的数据传给HomeSMSAdapter，让其显示在listView中
        adapter.assignment(list_mmt);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {//点击信息列表中的某一项触发
                Map<String, String> map = new HashMap<String, String>();
                SMSBean sb = (SMSBean) adapter.getItem(position);
                map.put("phoneNumber", sb.getAddress());
                map.put("threadId", sb.getThread_id());
                BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, MessageBoxList.class, map);
            }
        });

        newSms = (Button) findViewById(R.id.newSms);//新建短信
        newSms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, NewSMSActivity.class, null);
            }
        });
    }

    private boolean initCustomActionBar() {//这个暂时没用
        ActionBar actionBar = this.getActionBar();
        if (actionBar == null) {
            return false;
        }

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = inflater.inflate(R.layout.action_bar_title_sms_main, null);

        actionBar.setCustomView(titleView, lp);
        actionBar.setDisplayShowHomeEnabled(false);//去掉导航
        actionBar.setDisplayShowTitleEnabled(false);//去掉标题
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        return true;
    }
}
