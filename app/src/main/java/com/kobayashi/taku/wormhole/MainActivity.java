package com.kobayashi.taku.wormhole;

import android.Manifest;
import android.app.Application;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.taptappun.taku.kobayashi.runtimepermissionchecker.RuntimePermissionChecker;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;
    private WebRTC webRTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RuntimePermissionChecker.requestAllPermissions(this, REQUEST_CODE_CAMERA_PERMISSION);
        if(!RuntimePermissionChecker.existConfirmPermissions(this)){
            startCapture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode != REQUEST_CODE_CAMERA_PERMISSION)
            return;
        if(!RuntimePermissionChecker.existConfirmPermissions(this)){
            startCapture();
        }
    }

    private void startCapture(){
        Log.d("wormhole", "startCapture");
        WebRTCSurfaceView surfaceView = (WebRTCSurfaceView) findViewById(R.id.local_render_view);
        webRTC = new WebRTC(this);
        webRTC.addLocalView(surfaceView);
        webRTC.startCapture();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(webRTC != null){
            webRTC.release();
        }
    }
}
