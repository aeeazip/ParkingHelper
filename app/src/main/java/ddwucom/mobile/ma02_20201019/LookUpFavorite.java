package ddwucom.mobile.ma02_20201019;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/* 전체 즐겨찾기 목록 조회 페이지 */
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

        adapter = new FavCursorAdapter (this, R.layout.lookup_listview, null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = helper.getReadableDatabase();
                String selection = ParkingDBHelper.COL_ID + " = ?";
                String[] selectionArgs = { String.valueOf(id) };
                cursor = db.query(ParkingDBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);

                ParkingDto dto = new ParkingDto();
                while(cursor.moveToNext()){
                     dto.setId(cursor.getLong(cursor.getColumnIndex(ParkingDBHelper.COL_ID)));
                     dto.setName(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_NAME)));
                     dto.setAddress(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_ADDRESS)));
                     dto.setImage(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_IMAGE)));
                     dto.setRating(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_RATING)));
                     dto.setMemo(cursor.getString(cursor.getColumnIndex(ParkingDBHelper.COL_MEMO)));
                }

                Intent favDetailIntent = new Intent(LookUpFavorite.this, FavoriteDetail.class);
                favDetailIntent.putExtra("result", dto);
                startActivity(favDetailIntent);
            }
        });
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
        cursor= db.rawQuery("select * from " + ParkingDBHelper.TABLE_NAME, null);

        if (cursor != null && cursor.getCount() != 0) {
            adapter.changeCursor (cursor);
            helper.close ();
        } else{
            Toast.makeText(this, "즐겨찾기에 등록된 것이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}
