package lenovo.bdtongxin.SMS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lenovo.bdtongxin.R;

/**
 * Created by lenovo on 2016/9/8.
 */
public class NewSmsAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater;
    private List<ContactBean> list;
    private List<ContactBean> allContactList;
    private Context context;

    public NewSmsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.list = new ArrayList<ContactBean>();
        this.context = context;
    }

    public void assignment(List<ContactBean> list) {
        this.allContactList = list;
        this.list = this.allContactList;
    }

    public void add(ContactBean bean) {
        list.add(bean);
    }

    public void remove(int position) {
        list.remove(position);
    }

    public int getCount() {
        return list.size();
    }

    public ContactBean getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.newsms_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getDisplayName());
        holder.number.setText(list.get(position).getPhoneNum());

        convertView.setTag(holder);
        return convertView;
    }

    public final class ViewHolder {
        public TextView name;
        public TextView number;
    }

    public Filter getFilter() {
        Filter filter = new Filter() {
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<ContactBean>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            protected FilterResults performFiltering(CharSequence s) {
                String str = s.toString();
                FilterResults results = new FilterResults();
                ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
                if (allContactList != null && allContactList.size() != 0) {
                    for (ContactBean cb : allContactList) {
                        if (cb.getSortKey().indexOf(str) > -1) {
                            contactList.add(cb);
                        }
                    }
                }
                results.values = contactList;
                results.count = contactList.size();
                return results;
            }
        };
        return filter;
    }
}
