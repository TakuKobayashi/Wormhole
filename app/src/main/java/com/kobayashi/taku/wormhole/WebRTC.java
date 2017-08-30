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

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class WebRTC {
    private Activity activity;
    private PeerConnectionFactory factory;
    private VideoCapturer videoCapturer;

    private WebRTCCamera camera;
    private HashMap<String, VideoTrack> mResourceTrack = new HashMap<String, VideoTrack>();

    public WebRTC(Activity activity){
        this.activity = activity;

        // initialize Factory
        PeerConnectionFactory.initializeAndroidGlobals(activity.getApplicationContext(), true);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = new PeerConnectionFactory(options);

        camera = new WebRTCCamera(activity);
    }

    public String addLocalView(WebRTCSurfaceView view){
        factory.setVideoHwAccelerationOptions(view.getRenderEGLContext(), view.getRenderEGLContext());
        return setupStream(view);
    }

    public String addRemoteView(WebRTCSurfaceView view){
        factory.setVideoHwAccelerationOptions(view.getRenderEGLContext(), view.getRenderEGLContext());
        return setupStream(view);
    }

    public void removeLocalView(String trackId){
        releaseTrack(trackId);
    }

    public void removeRemoteView(String trackId){
        releaseTrack(trackId);
    }


    // implements -------------

    private String setupStream(WebRTCSurfaceView view) {
        String uuid = UUID.randomUUID().toString();
        MediaStream localStream = factory.createLocalMediaStream(uuid);
        videoCapturer = camera.createVideoCapture(WebRTCCamera.FRONT_CAMERA_ID, null);
        VideoSource localVideoSource = factory.createVideoSource(videoCapturer);

        VideoTrack localVideoTrack = factory.createVideoTrack(uuid, localVideoSource);
        localStream.addTrack(localVideoTrack);

        VideoRenderer videoRender = new VideoRenderer(view);
        localVideoTrack.addRenderer(videoRender);
        mResourceTrack.put(uuid, localVideoTrack);
        return uuid;
    }


    public void startCapture(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        int videoWidth = displayMetrics.widthPixels;
        int videoHeight = displayMetrics.heightPixels;

        videoCapturer.startCapture(videoWidth, videoHeight, 30);
    }

    private void releaseTrack(String trackId){
        VideoTrack track  = mResourceTrack.get(trackId);
        if(track != null){
            track.dispose();
        }
        mResourceTrack.remove(trackId);
    }

    public void release() {
        for(String trackId : mResourceTrack.keySet()){
            releaseTrack(trackId);
        }
        videoCapturer.dispose();
        factory.dispose();
    }
}
