package ddwucom.mobile.ma02_20201019;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

public class FavCursorAdapter extends CursorAdapter {
    final static String TAG = "MyCursorAdapter";

    LayoutInflater inflater;
    int layout;

    public FavCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @SuppressLint("Range")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

//        Tag에 저장한 ViewHolder 추출 후 view 를 생성하였는지 확인
        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder.favoriteName == null) {
            holder.favoriteName = view.findViewById(R.id.favoriteName);
            holder.favoriteMemo = view.findViewById(R.id.favoriteMemo);
            Log.d(TAG, "The holder is filled");
        }

        holder.favoriteName.setText(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_NAME)));
        holder.favoriteMemo.setText(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_MEMO)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemLayout = inflater.inflate(layout, parent, false);

//        ViewHolder 사용을 위해 ViewHolder 를 생성한 View 의 Tag 에 추가
        ViewHolder holder = new ViewHolder();
        listItemLayout.setTag(holder);

        return listItemLayout;
    }


    static class ViewHolder {
        TextView favoriteName;
        TextView favoriteMemo;
    }
}