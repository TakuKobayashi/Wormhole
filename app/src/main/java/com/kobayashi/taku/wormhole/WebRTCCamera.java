package com.kobayashi.taku.wormhole;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class WebRTCCamera {
    public final static int BACK_CAMERA_ID = 0;
    public final static int FRONT_CAMERA_ID = 1;

    private Camera2Enumerator camera2Enumerator;

    public WebRTCCamera(Context context){
        camera2Enumerator = new Camera2Enumerator(context);
    }

    public VideoCapturer createVideoCapture(int cameraId, CameraVideoCapturer.CameraEventsHandler eventsHandler){
        String cameraDeviceName = getCameraDeviceName(cameraId);
        if(cameraDeviceName != null){
            VideoCapturer videoCapturer = camera2Enumerator.createCapturer(cameraDeviceName, eventsHandler);

            if (videoCapturer != null) {
                return videoCapturer;
            }
        }

        return null;
    }

    private String getCameraDeviceName(int cameraId){
        String[] deviceNames = camera2Enumerator.getDeviceNames();
        for (int i = 0;i < deviceNames.length;++i) {
            if(cameraId == BACK_CAMERA_ID && camera2Enumerator.isBackFacing(deviceNames[i])){
                return deviceNames[i];
            }else if(cameraId == FRONT_CAMERA_ID && camera2Enumerator.isFrontFacing(deviceNames[i])) {
                return deviceNames[i];
            }
        }
        return null;
    }
}
