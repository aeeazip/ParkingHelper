package ddwucom.mobile.test;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    String TAG = "ListViewAdapter";

    private LayoutInflater inflate;
    private ViewHolder viewHolder;
    private List<Result> list;

    public ListViewAdapter(Context context, List<Result> list){
        // MainActivity 에서 데이터 리스트를 넘겨 받는다.
        this.list = list;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){
            convertView = inflate.inflate(R.layout.result_listview,null);
            viewHolder = new ViewHolder();
            viewHolder.parkingName = (TextView) convertView.findViewById(R.id.parkingName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.address);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.parkingName.setText(list.get(position).getName());
        viewHolder.address.setText(list.get(position).getAddress ());

        return convertView;
    }

    class ViewHolder{
        public TextView parkingName;
        public TextView address;
    }
}
