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

    public void Setup(EglBase.Context rendererContext){
        this.init(rendererContext, new RendererCommon.RendererEvents() {
            @Override
            public void onFirstFrameRendered() {

            }

            @Override
            public void onFrameResolutionChanged(int i, int i1, int i2) {

            }
        });
        this.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.setZOrderMediaOverlay(true);
        this.setEnableHardwareScaler(true);
    }
}
