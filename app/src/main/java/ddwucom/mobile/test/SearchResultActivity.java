package ddwucom.mobile.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchResultActivity extends AppCompatActivity {
    TextView input;
    TextView k1, k2, k3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.search_result);
        
        // parsing한 검색 결과 listView에 넣기 
        // listView 항목 하나 클릭하면 세부 사항 보여주기
    }

    @Override
    public void onResume(){
        super.onResume();

        input = findViewById (R.id.input);
        k1 = findViewById (R.id.k1);
        k2 = findViewById (R.id.k2);
        k3 = findViewById (R.id.k3);

        Intent intent = getIntent ();

        String kind1 = intent.getStringExtra ("kind1");
        String kind2 = intent.getStringExtra ("kind2");
        String kind3 = intent.getStringExtra ("kind3");
        String location = intent.getStringExtra("input");

        if (kind1.equals ("PP"))
            kind1 = "공영";
        else if (kind1.equals ("PM"))
            kind1 = "민영";

        if (kind2.equals ("OFF"))
            kind2 = "노외";
        else if (kind2.equals ("ON"))
            kind2 = "노상";
        else if (kind2.equals ("ELSE"))
            kind2 = "부설";

        if (kind3.equals ("FREE"))
            kind3 = "무료";
        else if (kind3.equals ("PAY"))
            kind3 = "유료";
        else if (kind3.equals ("MIX"))
            kind3 = "혼합";

        input.setText(location + "의 검색 결과입니다!");
        k1.setText(kind1);
        k2.setText(kind2);
        k3.setText(kind3);
    }
}
