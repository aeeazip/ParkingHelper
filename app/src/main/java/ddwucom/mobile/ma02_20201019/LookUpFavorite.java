package ddwucom.mobile.ma02_20201019;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LookUpFavorite extends AppCompatActivity {

    private static String TAG = "LookUpFavorite";

    private ListView listView;
    private FavCursorAdapter adapter;
    private List<ParkingDto> resultList;

    private ParkingDBHelper helper;
    private Cursor cursor;

    boolean isUpdated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.favorite_lookup);

        helper = new ParkingDBHelper (this);

        listView = (ListView) findViewById (R.id.lookup_listView);
        readAllContacts();

        adapter = new FavCursorAdapter (this, R.layout.lookup_listview, null);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUpdated) {
            readAllContacts();
        }
    }

    private void readAllContacts() {
        // DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + ParkingDBHelper.TABLE_NAME, null);

        Log.d(TAG, "ReadAllParkings");
        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}
