package com.example.googlemapexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mMap;
    private Marker[] currentMarker = new Marker[100];

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    //gps가 켜져 있는 동안에 실시간으로 바뀌는 위치정보를 얻기 위함
    private static final int UPDATE_INTERVAL_MS = 3000;// 3초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 2000; // 2초
    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;
    private int wait = 0;
    private final static int myLatLng = 99;
    private ToggleButton tb;
    private int emg_button=1;

    private  String PhoneNumber = "";

    String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_NUMBERS
    };  // 외부 저장소

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;

    private View mLayout2;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.layout_main);
        mLayout2=findViewById(R.id.layout_main);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS) //위치가 update 되는 주기(3000ms=3초)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); //위치 획득 후 update 되는 주기 (2000ms=2초)

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        tb=(ToggleButton)this.findViewById(R.id.togglebutton);
        tb.setText("버튼을 클릭하실 수 없습니다.");
    }

    private void buttonactivate(boolean emergency) {
        Log.d(TAG,"Emergency button: "+ emg_button);
        if(emergency){
            Toast.makeText(getApplicationContext(),"긴급 자동차" , Toast.LENGTH_SHORT).show();
            Log.d(TAG,"버튼: 사용 가능");

            tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                    if(on){
                        tb.setText("일반자동차");
                        emg_button=0;
                        Log.d(TAG,"Emergency button: "+ emg_button);
                    }
                    else{
                        tb.setText("긴급자동차");
                        emg_button=1;
                        Log.d(TAG,"Emergency button: "+emg_button);
                    }
                }
            });
        }
        else{
            emg_button=0;
            Toast.makeText(getApplicationContext(), "일반 자동차",Toast.LENGTH_SHORT).show();
            tb.setText("일반자동차");
            tb.setClickable(false);
            Log.d(TAG,"버튼: 사용 불가");
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        setDefaultLocation();
        getPermission();

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                wait=1;
                Log.d(TAG, "wait1 = "+wait);
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {

                    Location location = locationList.get(locationList.size() - 1);
                    //location = locationList.get(0);

                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + " 경도:" + String.valueOf(location.getLongitude());


                    /* 현재 위치정보 DB저장 */
                    String Latitude = String.valueOf(location.getLatitude()); // 위치정보 받아서 string 변수에 넣기
                    String Longitude = String.valueOf(location.getLongitude());
                    String PhoneNum = PhoneNumber;
                    //String PhoneNum = "010-9271-3205";
                    Log.d(TAG, "PhoneNum : "+PhoneNum);

                Response.Listener<String> responseListener = new Response.Listener<String>() { // php 접속 응답 확인
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean emergency = jsonObject.getBoolean("emergency");
                                Log.d(TAG, "emergency : "+emergency);

                                int number = jsonObject.getInt("number");
                                Log.d(TAG,"number : " + number);
                                String[] lat = new String[number+2];
                                String[] lng = new String[number+2];
                                Log.d(TAG, "String success !!");

                                while(number>=0) {
                                    lat[number] = jsonObject.getString("Latitude"+number);
                                    lng[number] = jsonObject.getString("Longitude"+number);
                                    Log.d(TAG, "Latitude : "+lat[number]);
                                    Log.d(TAG, "Longitude : "+lng[number]);
                                    number--;
                                }
                                setCurrentLocation(lat, lng); //현재 위치에 마커 생성
                                buttonactivate(emergency);
                            }

                            catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    };

                // 서버로 Volley를 이용해서 요청
                AddressRequest addressRequest = new AddressRequest(Latitude, Longitude, PhoneNum, emg_button, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(addressRequest);

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                if(wait!=1) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentPosition);
                    mMap.moveCamera(cameraUpdate);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                }
                wait=0;

                mCurrentLocatiion = location;

            }
        }


    };




    @SuppressLint({"MissionPermission", "HardwareIds"})
     private void getPermission(){
        Log.d(TAG, "getPermission()");
        int chkper_phonestate = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int chkper_phonenum = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        String PhoneNumber_Temp = "";
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (chkper_phonenum == PackageManager.PERMISSION_GRANTED && chkper_phonestate == PackageManager.PERMISSION_GRANTED &&hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            try {
                if (telephony.getLine1Number() != null) {
                    PhoneNumber_Temp = telephony.getLine1Number();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (PhoneNumber_Temp.startsWith("+82")) {
                PhoneNumber_Temp = PhoneNumber_Temp.replace("+82", "0");
                PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber_Temp);
                startLocationUpdates(); // 3. 위치 업데이트 시작
            }
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout2, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setCurrentLocation(String[] lat, String[] lng) {

        int i=0;

        while(currentMarker[i]!=null){
            currentMarker[i].remove();
            i++;
        }

        i=0;
        while(lat[i]!=null)
        {
            double latitude = Double.parseDouble(lat[i]);
            double longitude = Double.parseDouble(lng[i]);

            LatLng currentLatLng = new LatLng(latitude, longitude); // maker 위치 ( 0.001 = 약 100m )
            Log.d(TAG, "currentLatLng : "+latitude + ", "+longitude);

            if(getDistance(currentPosition,currentLatLng)<200){ // 반경 내 마커가 있을 때,
                Log.d(TAG, "WARRING !! :"+getDistance(currentPosition,currentLatLng));
                double distance=getDistance(currentPosition,currentLatLng);
                warning(distance);
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.draggable(true);
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.redcircle); // maker icon 변경
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70,50, false); // maker 크기
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            currentMarker[i] = mMap.addMarker(markerOptions);
            i++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void warning(double distance){
        if(emg_button==0){
            soundwarning(distance);
            viewwarning();
        }
    }

    public void soundwarning(double distance){
        Vibrator vibrator; //진동 알람 객체
        MediaPlayer player; //소리 알람 객체
        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long []{500,1000,500,1000},-1);
        player=MediaPlayer.create(this,R.raw.little_urgent);
        player.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void viewwarning(){
        Animation mAnimation = new AlphaAnimation(1.0f, 0.3f);
        mAnimation.setDuration(1000);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(3);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mLayout.startAnimation(mAnimation);
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);

        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);

        distance = locationA.distanceTo(locationB);
        return distance; // m 단위
    }

    private void startLocationUpdates() //위치를 이동하면서 계속 업데이트하는 과정
    {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


            if (checkPermission())
                mMap.setMyLocationEnabled(true); // 현재위치 파란색 동그라미로 표시
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setDefaultLocation()
    {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(35.832303, 128.757473);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        int i=0;

        if (currentMarker[i] != null) currentMarker[i].remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker[i] = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   )
        {
            return true;
        }

        return false;
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults)
            {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result )
            {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1]))
                {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();

                }else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }

        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        String PhoneNum = "010-9271-3205";//PhoneNumber;

        Response.Listener<String> responseListener = new Response.Listener<String>() { // php 접속 응답 확인
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if(success){
                        Log.d(TAG, "Success !");
                    }
                    else{
                        Log.d(TAG, "Fail..");
                    }
                }

                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청
        DestroyRequest destroyRequest = new DestroyRequest(PhoneNum, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(destroyRequest);

        Log.d(TAG, "Destroy !!");
    }
}
