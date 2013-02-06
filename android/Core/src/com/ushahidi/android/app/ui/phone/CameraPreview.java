package com.ushahidi.android.app.ui.phone;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.IOException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        Log.i("DORIS","CameraPreview ctr");
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void startup(SurfaceHolder surfaceHolder) {
        if (mCamera!=null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                // left blank for now
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("DORIS","CameraPreview surface created");
        mSurfaceHolder=surfaceHolder;
        startup(surfaceHolder);
    }

    public void attachCamera(Camera camera) {
        Log.i("DORIS","preview attachCamera");
        this.mCamera = camera;
        startup(mSurfaceHolder);
    }
    
    public void detachCamera() {
        Log.i("DORIS","preview detachCamera");
        if (mCamera!=null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("DORIS","preview surfaceDestroyed");
        detachCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
            int width, int height) {
        Log.i("DORIS","preview surfacechanged");
        mSurfaceHolder=surfaceHolder;
        startup(surfaceHolder);
    }
}