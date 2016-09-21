package lenovo.bdtongxin.SMS;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import lenovo.bdtongxin.R;

public class SmsActivity extends Activity {

    private EditText mobileText;
    private EditText contentText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sms);
        initCustomActionBar();
        //获取电话号文本框
        mobileText = (EditText) this.findViewById(R.id.l1);
        //获取短信内容文本框
        contentText = (EditText) this.findViewById(R.id.content);

        //获取按钮
        Button button = (Button) this.findViewById(R.id.new_sms_fasong);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //获取电话号码
                String moblie = mobileText.getText().toString();
                //短信内容
                String content = contentText.getText().toString();
                if (moblie.trim().equals("")) {
                    Toast.makeText(SmsActivity.this, "请填写收信人号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content.trim().equals("")) {
                    Toast.makeText(SmsActivity.this, "请编辑短信内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (moblie.length() != 6 && moblie.length() != 7 && moblie.length() != 11) {
                    Toast.makeText(SmsActivity.this, "收信人号码应该为6或者7位北斗号码，或者11位手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }


                //获取短信管理器
                SmsManager smsManager = SmsManager.getDefault();
                //如果汉字大于70个
                if (content.length() > 70) {
                    //返回多条短信
                    List<String> contents = smsManager.divideMessage(content);
                    for (String sms : contents) {
                        //1.目标地址：电话号码 2.原地址：短信中心服号码3.短信内容4.意图
                        smsManager.sendTextMessage(moblie, null, sms, null, null);
                    }
                } else {
                    smsManager.sendTextMessage(moblie, null, content, null, null);
                }
                //吐西1.应用上下文2.提示内容3.时间
                Toast.makeText(SmsActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
            }
        });


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
        View titleView = inflater.inflate(R.layout.action_bar_title_sms, null);

        actionBar.setCustomView(titleView, lp);
        actionBar.setDisplayShowHomeEnabled(false);//去掉导航
        actionBar.setDisplayShowTitleEnabled(false);//去掉标题
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        return true;
    }
}
