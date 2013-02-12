package com.ushahidi.android.app.ui.phone;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.Log;

class PictureTaker 
{
    private Camera mCam;
    public Boolean mTakingPicture;

    public PictureTaker() {
        mTakingPicture=false;
    }

    public void Startup(SurfaceView view) {
        mTakingPicture=false;
        OpenCamera(view);
    }

    public void Shutdown() {
        CloseCamera();
    }

    private void OpenCamera(SurfaceView view) {
        try {
            mCam = Camera.open();
            if (mCam == null) {
                Log.i("DORIS","Camera is null!");
                return;
            }
            mCam.setPreviewDisplay(view.getHolder());
            mCam.startPreview();
        }
        catch (Exception e) {
            Log.i("DORIS","Problem opening camera! " + e);
            return;
        }
    }

    private void CloseCamera() {
        if (mCam!=null) {
            mCam.stopPreview();
            mCam.release();
            mCam = null;
        }
    }

    public void TakePicture(SurfaceView view, PictureCallback picture)
    {
        if (!mTakingPicture) {
            mTakingPicture=true;
            CloseCamera();
            OpenCamera(view);
            
            try {
                mCam.takePicture(null, null, picture);
            }
            catch (Exception e) {
                Log.i("DORIS","Problem taking picture: " + e);
            }
        }
        else {
            Log.i("DORIS","Picture already being taken");
        }   
    }
}
