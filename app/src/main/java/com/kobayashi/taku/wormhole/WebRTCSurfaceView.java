package com.kobayashi.taku.wormhole;

import android.content.Context;
import android.util.AttributeSet;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

public class WebRTCSurfaceView extends SurfaceViewRenderer {
    public WebRTCSurfaceView(Context context) {
        super(context);
    }

    public WebRTCSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void Setup(){
        // rendereContext
        EglBase eglBase = EglBase.create();
        renderEGLContext = eglBase.getEglBaseContext();
        this.init(renderEGLContext, null);
        this.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.setZOrderMediaOverlay(true);
        this.setEnableHardwareScaler(true);
    }
}
