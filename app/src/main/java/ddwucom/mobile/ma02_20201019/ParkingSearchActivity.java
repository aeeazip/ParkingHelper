package ddwucom.mobile.ma02_20201019;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

/* ????????? ?????? (??????) ????????? */
public class ParkingSearchActivity extends AppCompatActivity {

    final String TAG = "ParkingSearchActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private GoogleMap mGoogleMap;       // ?????? ??????
    private PlaceBasicManager placeBasicManager;
    private PlacesClient placesClient;
    private EditText editText;

    Double mLat = 360.0; // ?????? ?????????
    Double mLng = 360.0; // ?????? ?????????
    String rating;
    int count;

    FusedLocationProviderClient flpClient;
    Location mLastLocation;
    Marker mCenterMarker;
    List<Marker> markerList;
    ArrayList<Result> resultList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.parking_search);

        editText = findViewById (R.id.inputLocation);
        flpClient = LocationServices.getFusedLocationProviderClient (this);

        // 1. PlaceBasicManager ??????
        placeBasicManager = new PlaceBasicManager (getString (R.string.api_key));

        // 2. placeBasicManager.setOnPlaceBasicResult() ?????? -> ?????? ???????????? ??????
        placeBasicManager.setOnPlaceBasicResult (new OnPlaceBasicResult () {
            @Override
            public void onPlaceBasicResult(List<PlaceBasic> list) {
                MarkerOptions options = new MarkerOptions ();
                markerList = new ArrayList<Marker> ();

                for (PlaceBasic place : list) {
                    LatLng latLng = new LatLng (place.getLatitude (), place.getLongitude ());

                    // place ?????? ????????? ?????? MarkerOptions ?????? ??????
                    options.title (place.getName ());
                    options.position (latLng);
                    options.icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_RED));
                    Marker marker = mGoogleMap.addMarker (options); // addMarker ?????? ????????? Marker???
                    marker.setTag (place.getPlaceId ());
                    markerList.add (marker);
                }
            }
        });

        Places.initialize (getApplicationContext (), getString (R.string.api_key));
        placesClient = Places.createClient (this);

        checkPermission ();
        mapLoad ();

        // ?????? ?????? ??????
        flpClient.requestLocationUpdates (
                getLocationRequest (),
                mLocCallback,
                Looper.getMainLooper ()
        );
    }

    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
        mapFragment.getMapAsync (mapReadyCallback); // map ?????? ???????????? (Callback ??????)
    }

    private void checkPermission() {
        if (checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission (Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // ????????? ?????? ?????? ????????? ??????
            // Toast.makeText (ParkingSearchActivity.this, "Permissions Granted", Toast.LENGTH_SHORT).show ();
        } else {
            // ?????? ??????
            requestPermissions (new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
        }
    }

    // getMapAsync??? ??????????????? ??????
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback () {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            checkPermission();
            mGoogleMap.setMyLocationEnabled(true);

            // ?????? ?????? ?????? ?????? (????????????  : ?????????????????????)
            LatLng latLng = new LatLng (37.606320, 127.041808);
            // ????????? ????????? ??????????????? ??????
            mGoogleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (latLng, 17));


            // ?????? ?????? ?????? ??????
            MarkerOptions markerOptions = new MarkerOptions ()
                    .position (latLng) // LatLng ??????
                    .title ("?????? ??????") // infowindow : ?????? ??? ?????? ??????
                    .snippet ("?????????") // infowindow : ?????? ??? ?????? ??????
                    .icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_BLUE));

            // ????????? ?????? ?????? ??? ????????? ?????? ?????? ??????
            mCenterMarker = mGoogleMap.addMarker (markerOptions); // addMarker : ????????? ?????? ????????????~ ??????
            mCenterMarker.showInfoWindow ();

            /*????????? InfoWindow ?????? ??? marker??? Tag ??? ????????? placeID ???
             * Google PlacesAPI ??? ???????????? ????????? ????????????*/
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
            // ???????????? ?????? ?????? ?????? ????????? animateCamera ?????? X

            if(mLat == 360.0 && mLng == 360.0) {
                for (Location loc : locationResult.getLocations ()) {
                    double lat = loc.getLatitude ();
                    double lng = loc.getLongitude ();

                    // ?????? ?????? ?????? (?????? ?????? ?????? ??????)
                    mLastLocation = loc;
                    LatLng currentLoc = new LatLng (lat, lng);
                    // ????????? ????????? ??????????????? ??????
                    mGoogleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (currentLoc, 17));
                    mCenterMarker.setPosition (currentLoc);
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

    /*????????? ????????? ?????? ????????? ??????
     * PlaceBasicManager ??? ????????? type ??? ????????? PlaceBasic ??? ???????????? ???????????? ????????? ???????????? ?????? */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic (lat, lng, radius, type);
    }

    private Place getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.PHONE_NUMBER,
                Place.Field.RATING
        );

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request)
                .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>(){
                    public void onSuccess(FetchPlaceResponse response){
                        Place p = response.getPlace();
                        rating = String.valueOf(p.getRating());
                        // callDetailActivity(p);
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


    // Google PlacesAPI ??? place ????????? ?????? ??????
    // ?????? ?????? ?????? ??? ??????
    private void callDetailActivity(Place place) {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // ???????????? ??????????????? ?????? ??? ?????? ??????
                mapLoad ();
            } else {
                // ????????? ????????? ??? ???????????? ??????
                // Toast.makeText (ParkingSearchActivity.this, "??? ????????? ?????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show ();
                finish ();
            }
        }
    }

    public void onClick(View v) throws InterruptedException {
        switch (v.getId ()) {
            case R.id.button_search:
                String inputLocation = editText.getText ().toString ();

                mLat = 360.0;
                mLng = 360.0;

                if (inputLocation.getBytes ().length > 0) {
                    executeReverseGeocoding (inputLocation);
                } else {
                    Toast.makeText (ParkingSearchActivity.this, "????????? ???????????????", Toast.LENGTH_SHORT).show ();
                }
                break;
            case R.id.allResult:
                if(markerList != null) {
                    intent = new Intent (ParkingSearchActivity.this, SearchResultActivity.class);
                    intent.putExtra ("inputLocation", editText.getText ().toString ());

                    resultList = new ArrayList<Result> ();
                    count = 0;

                    for (int i = 0; i < markerList.size (); i++) {
                        count++;
                        Marker m = markerList.get (i);
                        String name = m.getTitle ();
                        LatLng ll = m.getPosition ();

                        Location mLoc = new Location("mLoc");
                        mLoc.setLatitude (mLat); // ?????? ?????? ??????
                        mLoc.setLongitude (mLng); // ?????? ?????? ??????

                        Location search = new Location("search");
                        search.setLatitude (ll.latitude);
                        search.setLongitude (ll.longitude);

                        float distance = Math.round(mLoc.distanceTo (search));
                        String placeId = m.getTag().toString();
                        getPlaceDetail(placeId);
                        Result result = new Result(name, ll.latitude, ll.longitude, distance, rating);
                        executeGeocoding(result);
                    }
                } else
                    Toast.makeText (ParkingSearchActivity.this, "????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show ();
                break;
        }
    }

    private void executeGeocoding(Result result){
        if(Geocoder.isPresent()){
            if(result != null)
                new GeoTask ().execute(result);
        }
    }

    class GeoTask extends AsyncTask<Result, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(ParkingSearchActivity.this, Locale.getDefault());
        Result result;

        public List<Address> doInBackground(Result...locations){
            List<Address> address = null;
            result = locations[0];

            try{
                address = geocoder.getFromLocation(locations[0].lat, locations[0].lng, 1);
            } catch(IOException e){
                e.printStackTrace();
            }
            return address;
        }

        public void onPostExecute(List<Address> addresses){
            if (addresses != null) {
                Address address = addresses.get(0);
                String markerAddress = address.getAddressLine (0);
                resultList.add (new Result (result.name, result.lat, result.lng, markerAddress, result.far, result.rating));
            }

            if(count == markerList.size()){
                intent.putExtra ("resultList", (Serializable) resultList);
                startActivity (intent);
            }
        }
    }

    private void executeReverseGeocoding(String str) {
        if (Geocoder.isPresent ()) {
            if (str != null)
                new ReverseGeoTask ().execute (str);
        }
    }

    // ?????? ????????? ?????? ?????? ?????? ????????? ???????????? Geocoder ??? ???????????? AsyncTask ??????
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
                if (addresses.size () == 0) { // ????????? ???????????? ???????????? ?????? ??????
                    Toast.makeText (ParkingSearchActivity.this, "????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show ();
                } else {

                    Address address = addresses.get (0);
                    mLat = address.getLatitude ();
                    mLng = address.getLongitude ();

                    if (mLat != 360.0 && mLng != 360.0) {
                        mGoogleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (new LatLng (mLat, mLng), 14));
                        searchStart (mLat, mLng, 1000, PlaceTypes.PARKING);
                    } else
                        Toast.makeText (ParkingSearchActivity.this, "????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show ();
                }
            }
        }
    }
}