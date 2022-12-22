package ddwucom.mobile.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;

/* 주차장 찾기 (검색) 페이지 */
public class ParkingSearchActivity extends AppCompatActivity {
    final String TAG = "ParkingSearchActivity";
    final static int PERMISSION_REQ_CODE = 100;

    String kind1 = "PP"; // 주차장 구분
    String kind2 = "OFF"; // 주차장 유형
    String kind3 = "FREE"; // 요금 정보

    Double mLat = 360.0; // 위도 초기값
    Double mLng = 360.0; // 경도 초기값

    FusedLocationProviderClient flpClient;
    Location mLastLocation;
    Marker marker;
    Marker mCenterMarker;
    List<Marker> markerList;

    private GoogleMap mGoogleMap;       // 지도 객체
    private PlaceBasicManager placeBasicManager;
    private PlacesClient placesClient;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.parking_search);

        editText = findViewById (R.id.inputLocation);
        flpClient = LocationServices.getFusedLocationProviderClient (this);

        // 1. PlaceBasicManager 생성
        placeBasicManager = new PlaceBasicManager (getString (R.string.api_key));

        // 2. placeBasicManager.setOnPlaceBasicResult() 구현 -> 결과 받아오면 동작
        placeBasicManager.setOnPlaceBasicResult (new OnPlaceBasicResult () {
            @Override
            public void onPlaceBasicResult(List<PlaceBasic> list) {
                MarkerOptions options = new MarkerOptions ();
                markerList = new ArrayList<Marker> ();

                for (PlaceBasic place : list) {
                    LatLng latLng = new LatLng (place.getLatitude (), place.getLongitude ());

                    // place 객체 하나에 대한 MarkerOptions 준비 완료
                    options.title (place.getName ());
                    options.position (latLng);
                    options.icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_RED));
                    Marker marker = mGoogleMap.addMarker (options); // addMarker 반환 타입이 Marker임
                    marker.setTag (place.getPlaceId ());

                    markerList.add (marker);
                }
            }
        });

        Places.initialize (getApplicationContext (), getString (R.string.api_key));
        placesClient = Places.createClient (this);

        checkPermission ();
        mapLoad ();

        // 현재 위치 확인
        flpClient.requestLocationUpdates (
                getLocationRequest (),
                mLocCallback,
                Looper.getMainLooper ()
        );
    }

    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
        mapFragment.getMapAsync (mapReadyCallback); // map 정보 가져오기 (Callback 호출)
    }

    private void checkPermission() {
        if (checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission (Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 경우 수행할 동작
            Toast.makeText (ParkingSearchActivity.this, "Permissions Granted", Toast.LENGTH_SHORT).show ();
        } else {
            // 권한 요청
            requestPermissions (new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
        }
    }

    // getMapAsync의 매개변수로 전달
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback () {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            checkPermission();
            mGoogleMap.setMyLocationEnabled(true);

            // 지도 초기 위치 이동 (초기위치  : 동덕여자대학교)
            LatLng latLng = new LatLng (37.606320, 127.041808);
            // 지정한 위치로 애니메이션 이동
            mGoogleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (latLng, 17));


            // 지도 중심 마커 추가
            MarkerOptions markerOptions = new MarkerOptions ()
                    .position (latLng) // LatLng 객체
                    .title ("현재 위치") // infowindow : 터치 시 뜨는 정보
                    .snippet ("이동중") // infowindow : 터치 시 뜨는 정보
                    .icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_BLUE));

            // 지도에 마커 추가 후 추가한 마커 정보 기록
            mCenterMarker = mGoogleMap.addMarker (markerOptions); // addMarker : 구글맵 마커 만들어줘~ 요청
            mCenterMarker.showInfoWindow ();

            /*마커의 InfoWindow 클릭 시 marker에 Tag 로 보관한 placeID 로
             * Google PlacesAPI 를 이용하여 장소의 상세정보*/
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
                public void onInfoWindowClick(Marker marker){
                    String placeId = marker.getTag().toString();
                    getPlaceDetail(placeId);
                }
            });
        }
    };

    LocationCallback mLocCallback = new LocationCallback () {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            // 검색값이 있는 경우 현재 위치로 animateCamera 실행 X

            if(mLat == 360.0 && mLng == 360.0) {
                for (Location loc : locationResult.getLocations ()) {
                    double lat = loc.getLatitude ();
                    double lng = loc.getLongitude ();

                    // 지도 위치 이동 (가장 최근 위치 기록)
                    mLastLocation = loc;
                    LatLng currentLoc = new LatLng (lat, lng);
                    // 지정한 위치로 애니메이션 이동
                    mGoogleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (currentLoc, 17));
                    mCenterMarker.setPosition (currentLoc);

                    // Toast.makeText(ParkingSearchActivity.this, "위도 : " + lat + ", 경도 : " + lng, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest ();
        locationRequest.setInterval (5000);
        locationRequest.setFastestInterval (1000);
        locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /*입력된 유형의 주변 정보를 검색
     * PlaceBasicManager 를 사용해 type 의 정보로 PlaceBasic 을 사용하여 현재위치 주변의 관심장소 확인 */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic (lat, lng, radius, type);
    }

    /*Place ID 의 장소에 대한 세부정보 획득하여 반환
     * 마커의 InfoWindow 클릭 시 호출*/
    private Place getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG
        );

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request)
                .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>(){
                    public void onSuccess(FetchPlaceResponse response){
                        Place p = response.getPlace();
                        callDetailActivity(p);
                    }
                })
                .addOnFailureListener(new OnFailureListener (){
                    public void onFailure(Exception e){
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                    }
                });

        return null;
    }


    // Google PlacesAPI 의 place 객체를 전달 받음
    // 특정 장소 클릭 시 동작
    private void callDetailActivity(Place place) {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad ();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText (ParkingSearchActivity.this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show ();
                finish ();
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.button_search:
                String inputLocation = editText.getText ().toString ();

                mLat = 360.0;
                mLng = 360.0;

                if (inputLocation.getBytes ().length > 0) {
                    executeReverseGeocoding (inputLocation);
                } else {
                    Toast.makeText (ParkingSearchActivity.this, "위치를 입력하세요", Toast.LENGTH_SHORT).show ();
                }
                break;
            case R.id.allResult:
                Intent intent = new Intent (ParkingSearchActivity.this, SearchResultActivity.class);
                intent.putExtra("inputLocation", editText.getText().toString());

                ArrayList<Result> resultList = new ArrayList<Result>();
                for(int i=0; i<markerList.size(); i++){
                    Marker m = markerList.get(i);
                    String name = m.getTitle();
                    LatLng ll = m.getPosition();
                    resultList.add(new Result(name, ll.latitude, ll.longitude));
                }
                intent.putExtra ("resultList", (Serializable) resultList);
                startActivity (intent);
                break;
        }
    }

    private void executeReverseGeocoding(String str) {
        if (Geocoder.isPresent ()) {
            Toast.makeText (ParkingSearchActivity.this, "Run ReverseGeocoder", Toast.LENGTH_SHORT).show ();
            if (str != null)
                new ReverseGeoTask ().execute (str);
        } else {
            Toast.makeText (ParkingSearchActivity.this, "No ReverseGeocoder", Toast.LENGTH_SHORT).show ();
        }
    }

    // 현재 주소를 전달 받아 위도 경도를 확인하는 Geocoder 를 사용하는 AsyncTask 구현
    class ReverseGeoTask extends AsyncTask<String, Void, List<Address>> {
        Geocoder geocoder = new Geocoder (ParkingSearchActivity.this, Locale.getDefault ());

        @Override
        protected List<Address> doInBackground(String... str) {
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocationName (str[0], 1);
            } catch (IOException e) {
                e.printStackTrace ();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses != null) {
                Address address = addresses.get (0);
                mLat = address.getLatitude ();
                mLng = address.getLongitude ();
                Toast.makeText (ParkingSearchActivity.this, "317라인 = lat : " + mLat + ", log : " + mLng, Toast.LENGTH_SHORT).show ();

                if (mLat != 360.0 && mLng != 360.0) {
                    mGoogleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (new LatLng(mLat, mLng), 14));
                    searchStart (mLat, mLng, 1000, PlaceTypes.PARKING);
                }
                else
                    Toast.makeText (ParkingSearchActivity.this, "정확한 위치명을 입력하세요", Toast.LENGTH_SHORT).show ();
            }
        }
    }
}







/* 메뉴 정보*/
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mif = getMenuInflater();
        mif.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 버튼 클릭했을 때 동작하는 함수 작성
        switch (item.getItemId()) {
            case R.id.PP: // 주차장 구분 : 공영
                kind1 = "PP";
                break;
            case R.id.PM: // 주차장 구분 : 민영
                kind1 = "PM";
                break;
            case R.id.OFF: // 주차장 유형 : 노외
                kind2 = "OFF";
                break;
            case R.id.ON: // 주차장 유형 : 노상
                kind2 = "ON";
                break;
            case R.id.ELSE: // 주차상 유형 : 부설
                kind2 = "ELSE";
                break;
            case R.id.FREE: // 요금 정보 : 무료
                kind3 = "FREE";
                break;
            case R.id.PAY: // 요금 정보 : 유료
                kind3 = "PAY";
                break;
            case R.id.MIX: //요금 정보 : 혼합
                kind3 = "MIX";
                break;
            case R.id.allResult: // 리스트 형식의 모든 결과 보기
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("kind1",kind1);
                intent.putExtra("kind2", kind2);
                intent.putExtra("kind3", kind3);
                intent.putExtra("input", editText.getText().toString());
                startActivity(intent);
                break;
        }

        // 선택에 따라 값이 변하는지 테스트 해볼 것
        Toast.makeText(ParkingSearchActivity.this, kind1 + kind2 + kind3, Toast.LENGTH_LONG).show();
        return false;
    }
    */

