package ddwucom.mobile.ma02_20201019;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/* 메인 페이지 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.search: // 전국 주차장 검색
                Intent intent = new Intent(getApplicationContext(), ParkingSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.favorite: // 즐겨찾기

        }
    }
}