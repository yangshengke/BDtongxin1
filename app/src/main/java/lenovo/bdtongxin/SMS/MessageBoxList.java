package lenovo.bdtongxin.SMS;

import android.Manifest;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lenovo.bdtongxin.R;

/**
 * 短信对话框，由HomeSMSActivity界面跳转过来
 */
public class MessageBoxList extends Activity {
    private ListView talkView;
    private List<MessageBean> list = null;
    private Button fasong;
    private Button btn_return;
    private Button btn_call;
    private EditText neirong;
    private SimpleDateFormat sdf;
    private AsyncQueryHandler asyncQuery;
    private String address;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_messageboxlist);

        btn_return = (Button) findViewById(R.id.btn_return);
        btn_call = (Button) findViewById(R.id.btn_call);
        fasong = (Button) findViewById(R.id.fasong);
        neirong = (EditText) findViewById(R.id.neirong);

        String thread = getIntent().getStringExtra("threadId");
        address = getIntent().getStringExtra("phoneNumber");
        TextView tv = (TextView) findViewById(R.id.topbar_title);
        tv.setText(getPersonName(address));//状态标题栏显示姓名

        sdf = new SimpleDateFormat("MM-dd HH:mm");

        init(thread);

        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MessageBoxList.this.setResult(RESULT_OK);
                MessageBoxList.this.finish();
            }
        });
        btn_call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + address);
                if (address.length() == 11) {//如果电话号码是11位，就拨打出去
                    Intent it = new Intent(Intent.ACTION_CALL, uri);
                    if (ContextCompat.checkSelfPermission(MessageBoxList.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                        startActivity(it);
                }
                else Toast.makeText(MessageBoxList.this,"不是标准的11位号码",Toast.LENGTH_SHORT).show();
            }
        });
        fasong.setOnClickListener(new View.OnClickListener() {//【需补充】
            public void onClick(View v) {
                String nei = neirong.getText().toString();
                ContentValues values = new ContentValues();
                values.put("address", address);
                values.put("body", nei);
                getContentResolver().insert(Uri.parse("content://sms/sent"), values);
                Toast.makeText(MessageBoxList.this, nei, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(String thread) {

        asyncQuery = new MyAsyncQueryHandler(getContentResolver());

        talkView = (ListView) findViewById(R.id.messageboxlist);
        list = new ArrayList<MessageBean>();

        Uri uri = Uri.parse("content://sms");
        String[] projection = new String[]{
                "date",
                "address",
                "person",
                "body",
                "type"
        };// 查询的列
        //按照日期升序排序，这样保证对话信息列表是从“最旧”的依次展示到“最新”的，这个函数查询完后将获得符合条件的cursor结果，并调用后面的onQueryComplete方法
        asyncQuery.startQuery(0, null, uri, projection, "thread_id = " + thread, null, "date asc");
    }

    /**
     * 数据库异步查询类AsyncQueryHandler
     *
     * @author administrator
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {  //Cursor 是每行的集合。
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String date = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
                    if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {//找到列名为type中值为1的cursor   从RexseeSMS类中看到，type=1即为收件箱．　
                        MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
                                date,
                                cursor.getString(cursor.getColumnIndex("body")),//短信内容
                                R.layout.list_say_he_item);//接收到的
                        list.add(d);
                    } else {
                        MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
                                date,
                                cursor.getString(cursor.getColumnIndex("body")),
                                R.layout.list_say_me_item);//发出去的
                        list.add(d);
                    }
                }
                if (list.size() > 0) {
                    talkView.setAdapter(new MessageBoxListAdapter(MessageBoxList.this, list));
                    talkView.setDivider(null);//各列表项的分割线
                    talkView.setSelection(list.size());//将列表移动到指定的Position处
                } else {
                    Toast.makeText(MessageBoxList.this, "没有短信进行操作", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getPersonName(String number) {
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,};
        Cursor cursor = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",
                null,
                null);
        if (cursor == null) {
            return number;//如果只有号码，则返回号码
        }
        String name = number;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();
        return name;//如果有名字，则返回名字
    }
}
