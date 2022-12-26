package ddwucom.mobile.ma02_20201019;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/* 특정 즐겨찾기 상세 조회 페이지 + 수정 및 삭제도 가능 */
public class FavoriteDetail extends AppCompatActivity {

    private final static String TAG = "FavoriteDetail";

    private ParkingDto dto;
    private TextView detailParkName, detailParkAddress, detailParkRating;
    private ImageView imageView;
    private EditText editText;

    private String mCurrentPhotoPath;
    private ParkingDBHelper helper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.favorite_detail);

        helper = new ParkingDBHelper (this);
        dto = (ParkingDto) getIntent().getSerializableExtra ("result");

        detailParkName = findViewById (R.id.detailParkName);
        detailParkAddress = findViewById (R.id.detailParkAddress);
        detailParkRating = findViewById (R.id.detailParkRating);
        imageView = findViewById (R.id.detailImage);
        editText = findViewById (R.id.detailMemo);

        detailParkName.setText(dto.getName());
        detailParkAddress.setText(dto.getAddress ());
        editText.setText(dto.getMemo());
        if(dto.getRating() != null)
            detailParkRating.setText(dto.getRating ());
        else
            detailParkRating.setText("별점 정보 없음");

        mCurrentPhotoPath = dto.getImage();
    }

    // imageView.getWidth() or imageView.getHeight()는 onCreate or onResume에서 동작 안 함
    // onWindowFocusChanged 메소드를 통해 호출 O
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if(mCurrentPhotoPath != null)
                setPic();
        }
    }

    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        Log.d(TAG, "targetW : " + String.valueOf(targetW) + " targetH : " + String.valueOf(targetH));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Log.d(TAG, mCurrentPhotoPath);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d(TAG, "photoW : " + String.valueOf(photoW) + " photoH : " + String.valueOf(photoH));
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.goToAll:
                break;
            case R.id.update:
                // 메모 수정
                String memo = editText.getText().toString ();
                updateMemo(memo);
                break;
            case R.id.remove:
                // 즐겨찾기 목록에서 삭제
                deleteFavorite();
                break;
        }

        // 전체 즐겨찾기 목록으로 돌아가기
        Intent lookUpIntent = new Intent(this, LookUpFavorite.class);
        startActivity (lookUpIntent);
    }

    public void updateMemo(String memo){
        SQLiteDatabase db = helper.getWritableDatabase ();

        String whereClause = ParkingDBHelper.COL_ID + " = ?";
        String[] whereArgs = { String.valueOf(dto.getId()) };

        ContentValues row = new ContentValues ();
        row.put(ParkingDBHelper.COL_MEMO, memo);

        db.update(ParkingDBHelper.TABLE_NAME, row, whereClause, whereArgs);
    }

    public void deleteFavorite(){
        SQLiteDatabase db = helper.getWritableDatabase ();

        String whereClause = ParkingDBHelper.COL_ID + " = ?";
        String[] whereArgs = { String.valueOf(dto.getId()) };

        db.delete(ParkingDBHelper.TABLE_NAME, whereClause, whereArgs);
    }
}