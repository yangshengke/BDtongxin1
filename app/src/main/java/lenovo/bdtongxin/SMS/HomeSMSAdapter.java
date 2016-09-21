package lenovo.bdtongxin.SMS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lenovo.bdtongxin.R;

/**
 * Created by lenovo on 2016/9/5.
 */
public class HomeSMSAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SMSBean> list;
    private Context context;
    private Date d;
    private SimpleDateFormat sdf;

    public HomeSMSAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.list = new ArrayList<SMSBean>();
        this.context = context;
        this.d = new Date();
        this.sdf = new SimpleDateFormat("MM/dd HH:mm");
    }

    public void assignment(List<SMSBean> list) {
        this.list = list;
    }

    public void add(SMSBean bean) {
        list.add(bean);
    }

    public void remove(int position) {
        list.remove(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_sms_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.content = (TextView) convertView.findViewById(R.id.content);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getAddress());//  姓名/号码
        holder.count.setText("(" + list.get(position).getMsg_count() + ")");

        this.d.setTime(list.get(position).getDate());
        holder.date.setText(this.sdf.format(d));

        holder.content.setText(list.get(position).getMsg_snippet());

//        View中的setTag(Onbect)表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来。
//        可以用在多个Button添加一个监听器，每个Button都设置不同的setTag。这个监听器就通过getTag来分辨是哪个Button 被按下
//        这个东西在一些需要用到Adapter自定控件显示方式的时候非常有用。 Adapter 有个getView方法，可以使用setTag把查找的view缓存起来方便多次重用
        convertView.setTag(holder);
        return convertView;
    }

    public final class ViewHolder {
        public TextView name;
        public TextView count;
        public TextView date;
        public TextView content;
    }
}
