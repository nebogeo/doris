package foam.nebogeo.doris_evolved;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import java.util.List;


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
            Parameters parameters = mCam.getParameters();
            parameters.set("jpeg-quality", 90);

            parameters.setPictureSize(800, 600);
            // change resise in application/libraries/Imap.php

/*            List<Size> sl = parameters.getSupportedPictureSizes();

            int w=0,h=0;
            for(Size s : sl){
                if (w<s.width) w = s.width;
                if (h<s.height) h = s.height;
            }
            parameters.setPictureSize(w, h);

            Log.i("DORIS","setting picture size to "+w+" "+h);
*/
            mCam.setParameters(parameters);
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
