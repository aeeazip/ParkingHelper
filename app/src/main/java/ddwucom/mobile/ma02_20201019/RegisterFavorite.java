package ddwucom.mobile.ma02_20201019;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* 즐겨찾기에 주차장 등록 */
public class RegisterFavorite extends AppCompatActivity {

    private static String TAG = "RegisterFavorite";
    private static final int REQUEST_TAKE_PHOTO = 100;

    private EditText memo;
    private TextView addParkName, addParkAddress, addParkRating;
    private ImageView imageView;
    private ParkingDBHelper helper;
    private Result result;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.favorite_add);

        result = (Result) getIntent().getSerializableExtra ("result");

        addParkName = findViewById (R.id.addParkName);
        addParkAddress = findViewById (R.id.addParkAddress);
        addParkRating = findViewById (R.id.addParkRating);
        imageView = findViewById (R.id.imageView);
        memo = findViewById (R.id.memo);

        addParkName.setText(result.getName());
        addParkAddress.setText (result.getAddress ());
        addParkRating.setText (result.getRating ());

        helper = new ParkingDBHelper (this);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.add:
                // DB 데이터 삽입 작업 수행
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(ParkingDBHelper.COL_NAME, result.getName());
                row.put(ParkingDBHelper.COL_ADDRESS, result.getAddress ());
                row.put(ParkingDBHelper.COL_RATING, result.getRating ());
                row.put(ParkingDBHelper.COL_IMAGE, mCurrentPhotoPath); // 등록한 이미지 값 넣기;
                row.put(ParkingDBHelper.COL_MEMO, memo.getText().toString());
                long result = db.insert(ParkingDBHelper.TABLE_NAME, null, row);
                helper.close();

                String msg = result > 0 ? "즐겨찾기 추가 성공!" : "즐겨찾기 추가 실패!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.addImage:
                dispatchTakePictureIntent ();
                break;
        }
    }

    /*원본 사진 파일 저장*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity (getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException ex){
                ex.printStackTrace();
            }

            // 파일을 정상 생성하였을 경우
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "ddwu.com.mobile.multimedia.photo.fileprovider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult (takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat ("yyyyMMdd_HHmmss").format(new Date ());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "Save image File : " + mCurrentPhotoPath);
            setPic();
        }
    }
}