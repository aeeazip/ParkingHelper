package ddwucom.mobile.ma02_20201019;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NaverBlogAdapter extends BaseAdapter {
    String TAG = "NaverBlogAdapter";

    private LayoutInflater inflate;
    private NaverBlogAdapter.ViewHolder viewHolder;
    private List<NaverBlogDto> list;

    public NaverBlogAdapter(Context context, List<NaverBlogDto> list){
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
            convertView = inflate.inflate(R.layout.blog_listview,null);

            viewHolder = new NaverBlogAdapter.ViewHolder ();
            viewHolder.blogTitle = (TextView) convertView.findViewById(R.id.blogTitle);
            viewHolder.bloggerName = (TextView) convertView.findViewById(R.id.bloggerName);
            viewHolder.postDate = (TextView) convertView.findViewById(R.id.postDate);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (NaverBlogAdapter.ViewHolder)convertView.getTag();
        }

        viewHolder.blogTitle.setText(list.get(position).getBlogTitle());
        viewHolder.bloggerName.setText(list.get(position).getBloggername ());
        viewHolder.postDate.setText(String.valueOf(list.get(position).getPostdate ()));
        return convertView;
    }

    class ViewHolder{
        public TextView blogTitle;
        public TextView bloggerName;
        public TextView postDate;
    }
}
