package ddwucom.mobile.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class ParkingInformation extends AppCompatActivity {
    // 시간 계산하는 메소드 수행
    SimpleDateFormat hhmmssSSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Calendar start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.parking_information);
    }
    
    public void onClick(View v){
        switch(v.getId()){
            case R.id.star: // 즐겨찾기에 등록
                // 이미 등록된 주차장인지 먼저 체크
                // 등록 안되있으면 즐겨찾기 추가 class로 이동
                break;
            case R.id.start: // 주차 시간 계산
                start = Calendar.getInstance();
                break;
            case R.id.end: // 주차 시간 계산
                end = Calendar.getInstance();

                // startTime null 체크 어떻게 할거니???
                int diffMinute = end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE); // 분
                int diffSecond = end.get(Calendar.SECOND) - start.get(Calendar.SECOND);

                Toast.makeText(ParkingInformation.this, diffMinute, Toast.LENGTH_SHORT).show();
                break;
            case R.id.blog: // 주차장 블로그 검색 결과 보기
                break;
        }
    }
}
