package lenovo.bdtongxin.SMS;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lenovo.bdtongxin.R;
import lenovo.bdtongxin.UI.QuickAlphabeticBar;

/**
 * 选择联系人，由NewSMSActivity跳转而来
 */
public class SelectContactsToSendActivity extends Activity {
    private SelectContactsToSendAdapter adapter;
    private ListView personList;

    private List<ContactBean> list;
    private AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar alpha;

    private List<ContactBean> selectContactList = new ArrayList<ContactBean>();
    private Button returnBtn;
    private Button doneBtn;
    private Map<String, String> selectMap = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contacts_to_send);

        if (null != getIntent().getStringExtra("data")) {
            String data = getIntent().getStringExtra("data");
            Gson gson = new Gson();
            Type listRet = new TypeToken<List<ContactBean>>() {
            }.getType();
            selectContactList = gson.fromJson(data, listRet);
            selectMap = new HashMap<String, String>();
            for (ContactBean cb : selectContactList) {
                selectMap.put(cb.getPhoneNum(), cb.getDisplayName());
            }
        }

        personList = (ListView) findViewById(R.id.select_contacts_list);
        alpha = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        init();

        returnBtn = (Button) findViewById(R.id.btn_return);//“返回”按钮
        doneBtn = (Button) findViewById(R.id.btn_done);//“确定”按钮

        returnBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SelectContactsToSendActivity.this.finish();
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Gson gson = new Gson();
                String data = gson.toJson(selectContactList);
                Map<String, String> map = new HashMap<String, String>();
                map.put("list", data);
                BaseIntentUtil.intentSysDefault(SelectContactsToSendActivity.this, NewSMSActivity.class, map);
            }
        });
    }

    private void init() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,//电话信息表的外键id
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,//头像的id
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY //查询键
        }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 从数据库中查询联系人的一些信息，按照sort_key升序查询。按本地语言进行排序，即按名字进行排序
        //注意：如果android操作系统版本4.4或4.4以上就要用phonebook_label而不是sort_key字段（在raw_contacts表中）
    }

    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * 查询（startQuery）结束的回调函数
         */
        @Override
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
                    //					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
                    //						cb.setPhoneNum(number.substring(3));
                    //					} else {
                    cb.setPhoneNum(number);
                    //					}
                    cb.setSortKey(sortKey);
                    cb.setContactId(contactId);
                    cb.setPhotoId(photoId);
                    cb.setLookUpKey(lookUpKey);

                    if (null == selectMap) {
                    } else {
                        if (selectMap.containsKey(number)) {
                            cb.setSelected(1);//被选中了
                        }
                    }
                    list.add(cb);
                }
                if (list.size() > 0) {
                    setAdapter(list);//把查询到的所有联系人信息展示出来
                }
            }
        }
    }

    private void setAdapter(List<ContactBean> list) {
        adapter = new SelectContactsToSendAdapter(this, list, alpha);
        personList.setAdapter(adapter);
        alpha.init(SelectContactsToSendActivity.this);
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                boolean check = SelectContactsToSendAdapter.isSelected.get(position);
                if (check) {
                    SelectContactsToSendAdapter.isSelected.put(position, false);
                    selectContactList.remove(cb);
                } else {
                    SelectContactsToSendAdapter.isSelected.put(position, true);
                    selectContactList.add(cb);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}
