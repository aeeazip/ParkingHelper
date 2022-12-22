package ddwucom.mobile.test;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    TextView input;
    String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.search_result);

        String inputLocation = getIntent().getStringExtra("inputLocation");
        input = (TextView) findViewById (R.id.input);
        input.setText(inputLocation);

        List<Result> resultList = (List<Result>) getIntent().getSerializableExtra ("resultList");

        // markerList에 담아논 위치 위도 경도 잘 출력되는 것 확인
        for(int i=0; i<resultList.size(); i++){
            Result result = resultList.get(i);
            Log.d(TAG, String.valueOf(result.lat));
            Log.d(TAG, String.valueOf(result.lng));
        }
    }

    @Override
    public void onResume(){
        super.onResume();


    }
}
