package com.kobayashi.taku.wormhole;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.HashSet;
import java.util.UUID;

public class WebRTC {
    private Activity activity;
    private PeerConnectionFactory factory;
    private VideoCapturer videoCapturer;

    private WebRTCCamera camera;
    private HashSet<String> mResourceIds = new HashSet<String>();

    public WebRTC(Activity activity){
        this.activity = activity;

        // initialize Factory
        PeerConnectionFactory.initializeAndroidGlobals(activity.getApplicationContext(), true);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = new PeerConnectionFactory(options);

        camera = new WebRTCCamera(activity);
    }

    public void addLocalView(WebRTCSurfaceView view){
        factory.setVideoHwAccelerationOptions(view.getRenderEGLContext(), view.getRenderEGLContext());
        setupStream(view);
    }

    public void addRemoteView(WebRTCSurfaceView view){
        factory.setVideoHwAccelerationOptions(view.getRenderEGLContext(), view.getRenderEGLContext());
        setupStream(view);
    }

    public void removeLocalView(WebRTCSurfaceView view){
    }

    public void removeRemoteView(WebRTCSurfaceView view){

    }


    // implements -------------

    private void setupStream(WebRTCSurfaceView view) {
        String uuid = UUID.randomUUID().toString();
        mResourceIds.add(uuid);
        MediaStream localStream = factory.createLocalMediaStream(uuid);
        videoCapturer = camera.createVideoCapture(WebRTCCamera.FRONT_CAMERA_ID, null);
        VideoSource localVideoSource = factory.createVideoSource(videoCapturer);

        VideoTrack localVideoTrack = factory.createVideoTrack(uuid, localVideoSource);
        localStream.addTrack(localVideoTrack);

        VideoRenderer videoRender = new VideoRenderer(view);
        localVideoTrack.addRenderer(videoRender);
    }


    public void startCapture(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        int videoWidth = displayMetrics.widthPixels;
        int videoHeight = displayMetrics.heightPixels;

        videoCapturer.startCapture(videoWidth, videoHeight, 30);
    }

    public void release() {
        videoCapturer.dispose();
        factory.dispose();
    }
}
