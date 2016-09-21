package lenovo.bdtongxin.SMS;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lenovo.bdtongxin.R;

/**
 * 新建短信，由HomeSMSActivity跳转而来
 */
public class NewSMSActivity extends Activity {
    private Button btn_return;
    private Button add_btn;
    private Button fasong;
    private List<ContactBean> selectContactList = null;

    private MyViewGroup mvg;
    private LinearLayout ll;
    private EditText etMess;
    private int extiTextId = 100001;
    private String[] chars = new String[]{" ", ","};

    private ListView queryListView;
    private NewSmsAdapter adapter;

    private AsyncQueryHandler asyncQuery;
    private List<ContactBean> list;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sms);

        queryListView = (ListView) findViewById(R.id.select_contacts_list);
        btn_return = (Button) findViewById(R.id.new_sms_btn_return); //返回
        btn_return.setOnClickListener(new View.OnClickListener() {//返回
            public void onClick(View v) {
                NewSMSActivity.this.finish();
            }
        });

        add_btn = (Button) findViewById(R.id.add_btn); //从联系人列表中添加联系人
        add_btn.setOnClickListener(new View.OnClickListener() {//新建联系人
            public void onClick(View v) {
                if (null == etMess || "".equals(etMess.getText().toString())) {
                } else {
                    String phoneNum = etMess.getText().toString();
                    if (isNum(etMess.getText().toString().trim())) {
                        createView1(phoneNum, phoneNum);
                        etMess.setText("");
                    } else {
                        etMess.setText("");
                    }
                }

                if (null == selectContactList || selectContactList.size() < 1) {//如果还没有选择联系人，则打开联系人列表的activity进行选择
                    BaseIntentUtil.intentSysDefault(NewSMSActivity.this, SelectContactsToSendActivity.class, null);
                } else {
                    Gson gson = new Gson();
                    String data = gson.toJson(selectContactList);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("data", data);
                    BaseIntentUtil.intentSysDefault(NewSMSActivity.this, SelectContactsToSendActivity.class, map);
                }
            }
        });

        fasong = (Button) findViewById(R.id.new_sms_fasong);
        fasong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == etMess || "".equals(etMess.getText().toString())) {
                } else {
                    String phoneNum = etMess.getText().toString();
                    if (isNum(etMess.getText().toString().trim())) {
                        createView1(phoneNum, phoneNum);
                        etMess.setText("");
                    } else {
                        etMess.setText("");
                    }
                }

                if (null == selectContactList || selectContactList.size() < 1) {
                    Toast.makeText(NewSMSActivity.this, "请输入发送目标", Toast.LENGTH_SHORT).show();
                } else {

                    for (ContactBean cb : selectContactList) {

                        System.out.println(cb.getDisplayName());
                        System.out.println(cb.getPhoneNum());
                        System.out.println("------");
                    }
                }
            }
        });


        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        query();
        initMyGroupView();

        if (null != getIntent().getStringExtra("list")) {
            String data = getIntent().getStringExtra("list");
            Gson gson = new Gson();
            Type listRet = new TypeToken<List<ContactBean>>() {
            }.getType();
            selectContactList = gson.fromJson(data, listRet);
            for (ContactBean cb : selectContactList) {
                createView2(cb.getDisplayName().trim());
                final View child = mvg.getChildAt(mvg.getChildCount() - 1);
                autoHeight(child);
            }
        }

    }


    private void query() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }


    private void initMyGroupView() {
        ll = (LinearLayout) findViewById(R.id.l1);
        // 调用getWindowManager()之后，会取得现有Activity的Handle，此时，getDefaultDisplay()方法将取得的宽高维度存放于DisplayMetrics对象中，
        // 而取得的宽高维度是以像素为单位(Pixel)，“像素”所指的是“绝对像素”而非“相对像素”
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;  // 宽度（像素）
//        int height = metric.heightPixels;  // 高度（像素）
        /**********************************************************************************************/
        // 把etMess（通过java自定义的 EditText控件）添加到mvg（通过java自定义的 ViewGroup控件）中，再把mvg添加到l1（xml中的 LinearLayout控件）中
        mvg = new MyViewGroup(NewSMSActivity.this);
        mvg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 70));
        //		mvg.setBackgroundColor(Color.GREEN);
        etMess = new EditText(NewSMSActivity.this);
        etMess.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});//限制输入最大不超过15位
        etMess.setSelection(etMess.getText().length());//使光标随着输入内容移动
        etMess.setGravity(Gravity.CENTER_VERTICAL);//设置控件显示的位置：默认top，这里居中显示
        etMess.setMinWidth(100);//设置控件最小的宽度
        etMess.setHeight(60);
        etMess.setTag("edit");//setTag函数做个标记，再通过getTag获取标记来判断。比如：new了一堆TextView，然后把他们都同时放在一个ViewGroup里边。这时候你需要怎么区分这一堆TextView呢？无非是用Id或者Tag。
        etMess.getBackground().setAlpha(0);//设置透明度值，0为全透明
        etMess.setId(extiTextId);//给这个EditText设置一个唯一的ID，在布局文件里对应的就是android:id这个属性
        etMess.addTextChangedListener(new TextWatcher() {
            //在Text改变过程中触发调用，它的意思就是说在原有的文本s中，从start开始的count个字符替换长度为before的旧文本，注意这里没有将要之类的字眼，也就是说一句执行了替换动作。
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNum(s.toString())) {
                    if (s.length() >= 1) {
                        boolean bool = false;
                        //length() == 15直接生成按钮
                        if (s.length() == 15) {
                            bool = true;
                        }
                        //字数没有满足15个验证是否有空格
                        if (!bool) {
                            String c = s.toString().substring(start, start + count);
                            for (int i = 0; i < chars.length; i++) {
                                if (chars[i].equals(c)) {
                                    bool = true;
                                    break;
                                }
                            }
                        }
                        //bool == true 生成Button
                        if (bool) {
                            createView1(s.toString(), s.toString());
                            etMess.setText("");
                        }
                        //检测输入框数据是否已经换行
                        final View child = mvg.getChildAt(mvg.getChildCount() - 1);
                        autoHeight(child);
                    }
                } else {
                    adapter.getFilter().filter(s);
                    queryListView.setVisibility(View.VISIBLE);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mvg.addView(etMess);
        ll.addView(mvg);
        etMess.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isNum(etMess.getText().toString().trim())) {
                        createView1(etMess.getText().toString().trim(), etMess.getText().toString().trim());
                        etMess.setText("");
                    } else {
                        etMess.setText("");
                        queryListView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 为MyViewGroup自动计算高度
     *
     * @param child
     */
    private void autoHeight(final View child) {
        if (child != null) {
            new Handler() {
            }.postDelayed(new Runnable() {
                public void run() {
                    if (child.getBottom() > mvg.getBottom() || mvg.getBottom() - child.getBottom() >= child.getHeight()) {
                        LayoutParams l = mvg.getLayoutParams();
                        l.height = child.getBottom();
                        mvg.setLayoutParams(l);
                    }
                }
            }, 500);
        }
    }

    /**
     * 生成MyViewGroup的子元素
     * @param text 姓名
     * @param number 对应的电话号码
     */
    private void createView1(String text, String number) {

        if (etMess.getText().toString().equals(" ") || etMess.getText().toString().equals("")) {
        } else {
            TextView t = new TextView(this);
            t.setText(text);
            t.setTextColor(Color.BLACK);
            t.setGravity(Gravity.CENTER);
            t.setBackgroundResource(R.drawable.bg_sms_contact_btn);
            t.setHeight(60);
            t.setPadding(2, 0, 2, 0);
            t.setOnClickListener(new MyListener());
            t.setTag(number);
            mvg.addView(t, mvg.getChildCount() - 1);

            ContactBean cb = new ContactBean();
            cb.setDisplayName(text);
            cb.setPhoneNum(number);
            if (null == selectContactList) {
                selectContactList = new ArrayList<ContactBean>();
            }
            selectContactList.add(cb);
            queryListView.setVisibility(View.INVISIBLE);//设置联系人列表不可见,但是仍占用空间。如果要设置为不占用空间，请设置为View.GONE
        }
    }

    private void createView2(String text) {

        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.BLACK);
        t.setGravity(Gravity.CENTER);
        t.setHeight(60);
        t.setPadding(2, 0, 2, 0);
        t.setBackgroundResource(R.drawable.bg_sms_contact_btn);
        t.setOnClickListener(new MyListener());
        t.setTag(text);
        mvg.addView(t, mvg.getChildCount() - 1);
    }

    /**
     * MyViewGroup子元素的事件
     *
     * @author LDM
     */
    private class MyListener implements OnClickListener {
        public void onClick(View v) {
            mvg.removeView(v);
            String number = (String) v.getTag();
            for (ContactBean cb : selectContactList) {
                if (cb.getPhoneNum().equals(number)) {
                    selectContactList.remove(cb);
                    break;
                }
            }
            autoHeight(mvg.getChildAt(mvg.getChildCount() - 1));
        }
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

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {

                list = new ArrayList<ContactBean>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    ContactBean cb = new ContactBean();
                    cb.setDisplayName(name);
                    cb.setPhoneNum(number);
                    cb.setSortKey(sortKey);
                    cb.setContactId(contactId);
                    cb.setPhotoId(photoId);
                    cb.setLookUpKey(lookUpKey);

                    list.add(cb);
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }
        }
    }

    private void setAdapter(List<ContactBean> list) {
        adapter = new NewSmsAdapter(this);
        adapter.assignment(list);
        queryListView.setAdapter(adapter);
        queryListView.setTextFilterEnabled(true);
        queryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                boolean b = true;
                if (null == selectContactList || selectContactList.size() < 1) {
                } else {
                    for (ContactBean cbean : selectContactList) {
                        if (cbean.getPhoneNum().equals(cb.getPhoneNum())) {
                            b = false;
                            break;
                        }
                    }
                }
                if (b) {
                    etMess.setText(cb.getDisplayName());
                    createView1(etMess.getText().toString().trim(), cb.getPhoneNum());
                    etMess.setText("");
                } else {
                    queryListView.setVisibility(View.INVISIBLE);
                    etMess.setText("");
                }
            }
        });
        queryListView.setOnScrollListener(new OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(NewSMSActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");//正则表达式
    }


}
