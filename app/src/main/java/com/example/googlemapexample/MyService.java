package com.example.googlemapexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "서비스의 onCreate");
        String PhoneNum = "010-9271-3205";//PhoneNumber;

        Response.Listener<String> responseListener = new Response.Listener<String>() { // php 접속 응답 확인
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if(success){
                        Log.d("TAG", "Success !");
                    }
                    else{
                        Log.d("TAG", "Fail..");
                    }
                }

                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청
        DestroyRequest destroyRequest = new DestroyRequest(PhoneNum, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyService.this);
        queue.add(destroyRequest);
    }
}
