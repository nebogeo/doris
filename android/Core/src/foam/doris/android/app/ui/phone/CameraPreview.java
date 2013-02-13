package foam.doris.android.app.ui.phone;

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
import foam.doris.android.app.ui.phone.PictureTaker;

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    PictureTaker mPictureTaker;

    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, PictureTaker picturetaker) {
        super(context);
        mPictureTaker=picturetaker;
        Log.i("DORIS","CameraPreview ctr");
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("DORIS","CameraPreview surface created");
        mSurfaceHolder=surfaceHolder;
        mPictureTaker.Startup(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("DORIS","preview surfaceDestroyed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
            int width, int height) {
        Log.i("DORIS","preview surfacechanged");
        mSurfaceHolder=surfaceHolder;
    }
}
