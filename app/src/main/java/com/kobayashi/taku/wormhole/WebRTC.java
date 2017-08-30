package com.kobayashi.taku.wormhole;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpReceiver;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class WebRTC {
    private Activity activity;
    private PeerConnectionFactory factory;
    private VideoCapturer videoCapturer;

    private WebRTCCamera camera;
    private PeerConnection mConnection;
    private HashMap<String, VideoTrack> mResourceTrack = new HashMap<String, VideoTrack>();

    public WebRTC(Activity activity){
        this.activity = activity;

        // initialize Factory
        PeerConnectionFactory.initializeAndroidGlobals(activity.getApplicationContext(), true);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = new PeerConnectionFactory(options);

        camera = new WebRTCCamera(activity);
    }

    public void connect(){
        // initialize Factory
        PeerConnectionFactory.initializeAndroidGlobals(activity.getApplicationContext(), true);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = new PeerConnectionFactory(options);

        // create PeerConnection
        List<PeerConnection.IceServer> iceServers = Arrays.asList(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        // 何かセットする必要が多分ある
        MediaConstraints constraints = new MediaConstraints();
        mConnection = factory.createPeerConnection(iceServers, constraints, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(Config.TAG, "SignalingChange:" + signalingState);
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(Config.TAG, "IceConnectionChange:" + iceConnectionState);
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(Config.TAG, "receiveingChange:" + b);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(Config.TAG, "IceGatheringChange:" + iceGatheringState);
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(Config.TAG, "IceCandidate:" + iceCandidate);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(Config.TAG, "IceCandidatesRemoved:" + iceCandidates.length);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(Config.TAG, "addStream:" + mediaStream);
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(Config.TAG, "removeStream:" + mediaStream);
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(Config.TAG, "dataChannel:" + dataChannel);
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(Config.TAG, "RenegotiationNeeded");
            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                Log.d(Config.TAG, "addTrack:" + rtpReceiver + "\nstreams:" + mediaStreams.length);
            }
        });
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
