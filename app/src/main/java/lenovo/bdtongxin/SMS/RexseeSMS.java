package lenovo.bdtongxin.SMS;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/9/5.
 */
public class RexseeSMS {
    public static final String CONTENT_URI_SMS = "content://sms";// 短信
    public static final String CONTENT_URI_SMS_INBOX = "content://sms/inbox";// 收件箱
    public static final String CONTENT_URI_SMS_SENT = "content://sms/sent";//已发送
    public static final String CONTENT_URI_SMS_CONVERSATIONS = "content://sms/conversations";// 获取短彩信会话列表
    private Context mContext;

    public RexseeSMS(Context mContext) {
        this.mContext = mContext;
        // TODO Auto-generated constructor stub
    }

    public static String[] SMS_COLUMNS = new String[]{
            "_id", //0－为列索引值
            "thread_id", //1
            "address", //2　电话号码
            "person", //3
            "date", //4
            "body", //5
            "read", //6; 0:not read 1:read; default is 0
            "type", //7;  短信类型：1是接收到的，2是已发出   0:all 1:inBox-收件箱 2:sent 3:draft-草稿 4:outBox-发送箱  5:failed-发送失败 6:queued-正在发送？
            "service_center" //8
    };
    public static String[] THREAD_COLUMNS = new String[]{
            "thread_id",//线程id
            "msg_count",//消息个数
            "snippet"//// 消息片段
    };

    public String getContentUris() {
        String rtn = "{";
        rtn += "\"sms\":\"" + CONTENT_URI_SMS + "\"";
        rtn += ",\"inbox\":\"" + CONTENT_URI_SMS_INBOX + "\"";
        rtn += ",\"sent\":\"" + CONTENT_URI_SMS_SENT + "\"";
        rtn += ",\"conversations\":\"" + CONTENT_URI_SMS_CONVERSATIONS + "\"";
        rtn += "}";
        return rtn;
    }

    public String get(int number) {
        return getData(null, number);
    }

    public String getUnread(int number) {
        return getData("type=1 AND read=0", number);
    }

    public String getRead(int number) {
        return getData("type=1 AND read=1", number);
    }

    public String getInbox(int number) {
        return getData("type=1", number);
    }

    public String getSent(int number) {
        return getData("type=2", number);
    }

    public String getByThread(int thread) {
        return getData("thread_id=" + thread, 0);
    }

    /**
     * @param selection 过滤条件，用于要返回的行
     * @param number    按照日期降序，并取符合条件的结果的前面number条
     * @return 符合条件的所有信息组成的字符串
     */
    public String getData(String selection, int number) {
        Cursor cursor = null;
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            if (number > 0) {
                //SMS_COLUMNS:要返回的列　　selection：过滤条件，用于要返回的行  最后一个：怎么对行进行排序-按日期降序排列，并取前面的number条
                //返回的cursor位于第一个位置
                cursor = contentResolver.query(Uri.parse(CONTENT_URI_SMS), SMS_COLUMNS, selection, null, "date desc limit " + number);
            } else {
                cursor = contentResolver.query(Uri.parse(CONTENT_URI_SMS), SMS_COLUMNS, selection, null, "date desc");
            }
            if (cursor == null || cursor.getCount() == 0) return "[]";
            String rtn = "";
            for (int i = 0; i < cursor.getCount(); i++) {//cursor.getCount()返回cursor的行数
                cursor.moveToPosition(i);
                if (i > 0) rtn += ",";
                rtn += "{";
                rtn += "\"_id\":" + cursor.getString(0);//0为列数
                rtn += ",\"thread_id\":" + cursor.getString(1);
                rtn += ",\"address\":\"" + cursor.getString(2) + "\"";
                rtn += ",\"person\":\"" + ((cursor.getString(3) == null) ? "" : cursor.getString(3)) + "\"";
                rtn += ",\"date\":" + cursor.getString(4);
                rtn += ",\"body\":\"" + cursor.getString(5) + "\"";
                rtn += ",\"read\":" + ((cursor.getInt(6) == 1) ? "true" : "false");
                rtn += ",\"type\":" + cursor.getString(7);
                rtn += ",\"service_center\":" + cursor.getString(8);
                rtn += "}";
            }
            cursor.close();
            return "[" + rtn + "]";
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * 注意这里查询的uri为：CONTENT_URI_SMS_CONVERSATIONS
     *
     * @param number 按照thread_id降序，取前number条数据
     * @return 对符合条件的cursor的每行数据添加到对应的每个SMSBean对象中（一行对应一个对象），并把这些对象添加到list中
     */
    public List<SMSBean> getThreads(int number) {
        Cursor cursor = null;
        ContentResolver contentResolver = mContext.getContentResolver();
        List<SMSBean> list = new ArrayList<>();
        try {
            if (number > 0) {
                cursor = contentResolver.query(Uri.parse(CONTENT_URI_SMS_CONVERSATIONS), THREAD_COLUMNS, null, null, "thread_id desc limit " + number);
            } else {
                cursor = contentResolver.query(Uri.parse(CONTENT_URI_SMS_CONVERSATIONS), THREAD_COLUMNS, null, null, "date desc");
            }
            if (cursor == null || cursor.getCount() == 0) return list;
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                //对符合条件的cursor的每一行*3列（消息片段,线程id,消息个数）添加到每一个SMSBean对象中
                SMSBean mmt = new SMSBean(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                list.add(mmt);
            }
            cursor.close();
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    /**
     * 注意这里查询的uri为：CONTENT_URI_SMS
     * @return 通过上面getThreads方法所得到的每个对象的Thread_id，再从另一张表上查询SMS_COLUMNS中所包含的那些列（包括电话号码、日期和已读状态），
     * 获得第一个cursor所指向的电话号码、日期和已读状态，最终把这三个信息又重新添加到ll里各SMSBean对象对应的成员中
     */
    public List<SMSBean> getThreadsNum(List<SMSBean> ll) {

        Cursor cursor = null;
        ContentResolver contentResolver = mContext.getContentResolver();
        List<SMSBean> list = new ArrayList<SMSBean>();
//		try {
        for (SMSBean mmt : ll) {
            cursor = contentResolver.query(Uri.parse(CONTENT_URI_SMS), SMS_COLUMNS, "thread_id = " + mmt.getThread_id(), null, null);
            if (cursor == null || cursor.getCount() == 0) return list;
            cursor.moveToFirst();
            mmt.setAddress(cursor.getString(2));
            mmt.setDate(cursor.getLong(4));
            mmt.setRead(cursor.getString(6));
            list.add(mmt);
            cursor.close();
        }

        return list;
//		} catch (Exception e) {
//			Log.e("getThreadsNum", "getThreadsNum-------------");
//			return list;
//		}
    }
}
