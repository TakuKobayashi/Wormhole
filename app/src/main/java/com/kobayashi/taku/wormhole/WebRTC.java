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

public class WebRTC {
    private Activity activity;
    private PeerConnectionFactory factory;
    private VideoCapturer videoCapturer;

    private WebRTCCamera camera;

    public WebRTC(Activity activity){
        this.activity = activity;

        // rendereContext
        EglBase eglBase = EglBase.create();
        renderEGLContext = eglBase.getEglBaseContext();

        // initialize Factory
        PeerConnectionFactory.initializeAndroidGlobals(activity.getApplicationContext(), true);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = new PeerConnectionFactory(options);
        factory.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext);

        camera = new WebRTCCamera(activity);

        // setupLocalStream
        setupLocalStream();
    }

    public void addLocalView(WebRTCSurfaceView view){

    }

    public void addRemoteView(WebRTCSurfaceView view){

    }

    public void removeLocalView(WebRTCSurfaceView view){

    }

    public void removeRemoteView(WebRTCSurfaceView view){

    }

    // implements -------------

    private void setupLocalStream() {

        SurfaceViewRenderer localRenderer = setupRenderer();

        MediaStream localStream = factory.createLocalMediaStream("android_local_stream");
        videoCapturer = camera.createVideoCapture(WebRTCCamera.FRONT_CAMERA_ID, null);
        VideoSource localVideoSource = factory.createVideoSource(videoCapturer);

        VideoTrack localVideoTrack = factory.createVideoTrack("android_local_videotrack", localVideoSource);
        localStream.addTrack(localVideoTrack);

        VideoRenderer videoRender = new VideoRenderer(localRenderer);
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

    private SurfaceViewRenderer setupRenderer(){
        SurfaceViewRenderer localRenderer = (SurfaceViewRenderer) activity.findViewById(R.id.local_render_view);
        localRenderer.init(renderEGLContext, null);
        localRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        localRenderer.setZOrderMediaOverlay(true);
        localRenderer.setEnableHardwareScaler(true);

        return localRenderer;
    }

    public void release() {
        videoCapturer.dispose();
        factory.dispose();
    }
}
