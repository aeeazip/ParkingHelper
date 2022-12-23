package ddwucom.mobile.ma02_20201019;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ParkingInformation extends AppCompatActivity {
    TextView infoName, howFar, infoAddress, infoRanking;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.parking_information);

        result = (Result) getIntent().getSerializableExtra ("result");
        infoName = findViewById (R.id.infoName);
        howFar = findViewById (R.id.howFar);
        infoAddress = findViewById (R.id.infoAddress);
        infoRanking = findViewById (R.id.infoRanking);

        infoName.setText(result.getName());
        howFar.setText(result.getFar () + "m");
        infoAddress.setText (result.getAddress());
        infoRanking.setText (result.getRating ());

    }
    
    public void onClick(View v){
        switch(v.getId()){
            case R.id.star: // 즐겨찾기에 등록
                // 이미 등록된 주차장인지 먼저 체크
                // 등록 안되있으면 즐겨찾기 추가 class로 이동
                break;
            case R.id.blog: // 주차장 블로그 검색 결과 보기
                break;
        }
    }
}
