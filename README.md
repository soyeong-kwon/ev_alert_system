# Emergency Vehicle Notification Service : web
[2020] Emergency Vehicle Notification Service : app code
_Update: 2022-04-18_  
## **Index**
+ [About this project](#about-this-project)
+ [Overview](#overview)
  + [Goal](#goal)
  + [Flow](#flow)
+ [Detail Function](#detail-function)    
  + [LocationCallback](#1-locationcallback--서버에서-긴급자동차의-위치를-받음)
  + [setCurrentLocation](#2-setcurrentlocation--수신받은-위치-정보를-화면에-표시함)
  + [warning](#3-warning-긴급자동차와-운전자가-일정-범위-내-근접하면-경고음-발생)
    
+ [Environment](#environment)

## **About this project**
<img src = "https://user-images.githubusercontent.com/68631435/163201142-024fc295-7b25-4a72-b2cc-52f14154a010.png" width="60%" height="40%">   

+ 프로젝트 이름: 긴급자동차 위치 알림 서비스
+ 프로젝트 진행 목적: ICT 멘토링 및 공모전 출전    
▻ (이브와 ICT 멘토링) 참고 url: https://www.hanium.or.kr/portal/kibwa/businessOverview.do  
+ 프로젝트 진행 기간: 2020년 5월 ~ 2020년 11월  
+ 프로젝트 참여 인원: 5명  
## **Overview** 
> ### **Goal**
+ (목적) 운전자에게 긴급자동차의 접근을 알림으로서 긴급자동차 경로 확보를 도우고 2차 사고 발생을 예방하기 위함.  
+ (필요성) 운전자의 시야 확보가 불가능한 상황에서 긴급자동차의 위치를 실시간으로 알려 신속하고 원활한 이동을 도움.   
> ### **Flow**
<img src = "https://user-images.githubusercontent.com/68631435/163202194-e6f579cf-888a-45f1-9d44-0b9036d7d5e8.png" width="45%" height="45%">   
<img src = "https://user-images.githubusercontent.com/68631435/163204774-367f1ab7-9e89-4b02-bd14-79ec5ee89c84.png" width="50%" height="height 20%">    

## **Detail Function**
> **App**   
> 본 프로젝트의핵심 기능을 구현한 코드를 설명하였음.    
#### **(1) LocationCallback : 서버에서 긴급자동차의 위치를 받음.**   
```java
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {

                Location location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerSnippet = "위도:" + location.getLatitude() + " 경도:" + location.getLongitude();

                /* 현재 위치정보 DB저장 */
                String Latitude = String.valueOf(location.getLatitude()); // 위치정보 받아서 string 변수에 넣기
                String Longitude = String.valueOf(location.getLongitude());
                Log.d(TAG, "PhoneNum : "+PhoneNum);

                Response.Listener<String> responseListener = new Response.Listener<String>() { // php 접속 응답 확인
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int number = jsonObject.getInt("number"); // 전체 긴급자동차의 개수 

                            // 긴급자동차의 위치정보(위도, 경도값)
                            String[] lat = new String[number+2];
                            String[] lng = new String[number+2];

                            while(number>=0) {
                                lat[number] = jsonObject.getString("Latitude"+number);
                                lng[number] = jsonObject.getString("Longitude"+number);
                                Log.d(TAG, "Latitude : "+lat[number]);
                                Log.d(TAG, "Longitude : "+lng[number]);
                                number--;
                            }
                            setCurrentLocation(lat, lng); //현재 위치에 마커 생성 함수 
                            buttonactivate();
                        }

                        catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청
                AddressRequest addressRequest = new AddressRequest(Latitude, Longitude, PhoneNum, emg_button,ID,responseListener);
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
``` 
+ volley로 긴급자동차의 위치를 계속해서 요청
+ 요청 시 운전자의 위치정보도 함께 송신하여 범위 내에 위치하는 긴급자동차 위치정보 수신함. 
+ 수신받은 긴급자동차 위치정보를 _setCurrentLocation()_ 함수에 넣어 화면 상에 표시함. 

#### **(2) setCurrentLocation() : 수신받은 위치 정보를 화면에 표시함.**   
```java
public void setCurrentLocation(String[] lat, String[] lng) {
        int i=0;
        double distance;
        int bool_warning = 0;

        //현재 마커를 모두 삭제
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

            if (getDistance(currentPosition, currentLatLng) < 500 && getDistance(currentPosition, currentLatLng)!=0) { // 반경 내 마커가 있을 때,
                Log.d(TAG, "WARRING !! :" + getDistance(currentPosition, currentLatLng));
                distance = getDistance(currentPosition, currentLatLng); // 긴급자동차 위치와 운전자의 위치 간 거리 구함. 
                if (distance<min){
                    min = distance;
                }
                bool_warning = 1; //경고를 알려야함 
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.draggable(true);
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.redcircle); // maker icon 변경
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70, 50, false); // maker 크기
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            currentMarker[i] = mMap.addMarker(markerOptions); //지도에 Marker 띄우기
            i++;

        }

        if (bool_warning == 1) { // 반경 내 마커가 있을 때,
            warning(min); //반경 내 마커가 있을때 warning 함수 호출 -> 소리 및 화면 상 경고
            min=1000;
        }
    }
```
+ 수신받은 긴급자동차 위치정보를 기반으로 marker로 화면에 표시 
+ 실제 긴급자동차와 운전자의 거리를 계산하고 일정 반경 내에 위치하면 warning (진동, 소리)

#### **(3) warning: 긴급자동차와 운전자가 일정 범위 내 근접하면 경고음 발생**
```java
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void warning(double distance){
        soundwarning(distance);
        viewwarning();
    }

    public void soundwarning(double distance){
        Vibrator vibrator; //진동 알람 객체
        MediaPlayer player; //소리 알람 객체
        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long []{500,1000,500,1000},-1);

        if(distance<=500 && distance>=200)
        {
            player=MediaPlayer.create(this,R.raw.emergency_500m);
            player.start();
        }
        else if(distance<200&&distance>=100)
        {
            player=MediaPlayer.create(this,R.raw.emergency_200m);
            Log.d(TAG, "distance100이상: "+distance);
            player.start();
        }
        else if(distance<100&&distance>=50)
        {
            player=MediaPlayer.create(this,R.raw.emergency_nearby);
            Log.d(TAG, "distance50이상: "+distance);
            player.start();
        }
        else if(distance<50&&distance>=10)
        {
            player=MediaPlayer.create(this,R.raw.little_urgent);
            Log.d(TAG, "distance10이상: "+distance);
            player.start();
        }
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
```
+ 운전 중인 운전자에게 신속하게 알리기 위해 긴급자동차와의 거리에 맞춤으로 경고음 및 진동 발생
+ 화면이 빨갛게 변해 깜빡이는 애니메이션 효과 적용

## **Environment** 
+ 안드로이드 OS(9) : 스마트폰 운영체제
+ Android Studio (4.0.1) : Android application 프로그램 개발
+ Google API : 위치정보를 탐지하고 처리하기 위해 사용
