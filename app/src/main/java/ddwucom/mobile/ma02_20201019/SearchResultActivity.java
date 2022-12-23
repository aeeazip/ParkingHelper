package ddwucom.mobile.ma02_20201019;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/* 전체 검색 결과 페이지 */
public class SearchResultActivity extends AppCompatActivity {

    String TAG = "SearchResultActivity";

    private TextView input;
    private ListView listView;
    private ListViewAdapter adapter;
    private int page = 0;
    private final int OFFSET = 20;
    private List<Result> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.search_result);

        listView = (ListView) findViewById (R.id.listView);
        resultList = (List<Result>) getIntent().getSerializableExtra ("resultList");
        adapter = new ListViewAdapter (this, resultList);
        listView.setAdapter(adapter);

        String inputLocation = getIntent().getStringExtra("inputLocation");
        input = (TextView) findViewById (R.id.input);
        input.setText(inputLocation);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchResultActivity.this, ParkingInformation.class);
                Result result = resultList.get(i);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    // 버튼 안먹는다
    public void onClick(View v){
        switch(v.getId()){
            case R.id.back:
                // 이전 Activity에서 비동기적으로 intent를 호출하면서 finish() 해줘도 결과값이 하나 줄어드는 화면으로 돌아간다
                // 따라서 Manifest에 android:noHistory="true" 등록해주면 기록이 지워지면서 검색 화면으로 돌아가게 해준다
                finish(); 
                break;
        }
    }
}
