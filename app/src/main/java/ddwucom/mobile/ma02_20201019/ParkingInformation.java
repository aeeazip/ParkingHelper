package ddwucom.mobile.ma02_20201019;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/* 선택한 주차장 정보 + 블로그 검색 결과 보여주는 페이지 */
public class ParkingInformation extends AppCompatActivity {

    public static final String TAG = "ParkingInformation";

    private TextView infoName, howFar, infoAddress, infoRanking;
    private Result result;
    private ListView listView;
    private String query;
    private String apiAddress;

    private NaverBlogAdapter adapter;
    private List<NaverBlogDto> resultList;
    private NaverBlogXmlParser parser;

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
        if(result.getRating() == null)
            infoRanking.setText ("정보 없음");
        else
            infoRanking.setText (result.getRating ());

        listView = (ListView) findViewById (R.id.blog_listView);
        resultList = new ArrayList();

        adapter = new NaverBlogAdapter(this, resultList);
        listView.setAdapter(adapter);

        apiAddress = getResources ().getString(R.string.api_url);
        parser = new NaverBlogXmlParser ();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 블로그 link 가져와서 거기로 접속되도록 하기
                NaverBlogDto dto = resultList.get(i);
                String link = dto.getLink();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(link);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }
    
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.star: // 즐겨찾기에 등록
                // 이미 등록된 주차장인지 먼저 체크
                // 등록 안되있으면 즐겨찾기 추가 class로 이동
                Intent intent = new Intent(ParkingInformation.class, RegisterFavorite.class);
                intent.putExtra ("result", (Serializable) result);
                startActivity(intent);
                break;
            case R.id.blog: // 주차장 블로그 검색 결과 보기
                if (!isOnline ()) {
                    Toast.makeText (ParkingInformation.this, "네트워크를 사용 설정해주세요.", Toast.LENGTH_SHORT).show ();
                    return;
                }
                query = infoName.getText ().toString ();  // 검색어 = 주차장명
                new NaverAsyncTask ().execute (apiAddress, query);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    /* 블로그 검색 api는 비동기 방식으로 처리 */
    class NaverAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(ParkingInformation.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String query = strings[1];

            String apiURL = null;
            try {
                apiURL = address + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String result = downloadNaverContents(apiURL);
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            progressDlg.dismiss();

            ArrayList<NaverBlogDto> parserdList = parser.parse(result);     // 오픈 API 결과의 파싱 수행

            if (parserdList == null || parserdList.size() == 0) {
                Toast.makeText(ParkingInformation.this, "No data!", Toast.LENGTH_SHORT).show();
            } else {
                resultList.clear();
                resultList.addAll(parserdList);
                adapter.notifyDataSetChanged();
            }
        }
    }



    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadNaverContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
        String clientId = getResources().getString(R.string.client_id);
        String clientSecret = getResources().getString(R.string.client_secret);

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            /* 네이버 사용 시 설정 필요 */
            conn.setRequestProperty("X-Naver-Client-Id", clientId);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        // Log.d(TAG, "Result: " + result);
        return result;
    }


    /* 주소(address)에 접속하여 비트맵 데이터를 수신한 후 반환 */
    protected Bitmap downloadImage(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        Bitmap result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToBitmap(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }


    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException ("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }


    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
    protected Bitmap readStreamToBitmap(InputStream stream) {
        return BitmapFactory.decodeStream(stream);
    }
}
