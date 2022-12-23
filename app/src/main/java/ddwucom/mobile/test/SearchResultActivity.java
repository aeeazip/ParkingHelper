package ddwucom.mobile.test;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
