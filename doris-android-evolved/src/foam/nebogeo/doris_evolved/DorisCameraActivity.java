/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foam.nebogeo.doris_evolved;

import android.app.Activity;
import android.util.Log;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;

// things for camera
import android.view.KeyEvent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import android.net.Uri;
import android.widget.FrameLayout;
import android.content.pm.ActivityInfo;
import android.widget.Button;

import foam.nebogeo.doris_evolved.PictureTaker;
import foam.nebogeo.doris_evolved.DorisLocationListener;
import foam.nebogeo.doris_evolved.DorisFileUtils;

import android.location.LocationManager;

// should be in utils or somefink
import java.io.File;
import android.os.Environment;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DorisCameraActivity extends Activity {

    private CameraPreview mCameraPreview;
    private PictureTaker mPictureTaker;
    private long mStartTime=0;
    private Boolean mKeyPressed=false;

    static AssetManager assetManager;
	protected LocationManager locationManager;
    protected DorisLocationListener locationListener;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.camera);

        assetManager = getAssets();

		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

        locationListener = new DorisLocationListener(locationManager);

        mPictureTaker = new PictureTaker();
        mCameraPreview = new CameraPreview(this, mPictureTaker);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        Log.i("DORIS",""+mCameraPreview);
        Log.i("DORIS",""+preview);

        preview.addView(mCameraPreview);

    }

    /** Called when the activity is about to be destroyed. */
    @Override
    protected void onPause()
    {
/*        // turn off all audio
        selectClip(CLIP_NONE, 0);
        isPlayingAsset = false;
        setPlayingAssetAudioPlayer(false);
        isPlayingUri = false;
        setPlayingUriAudioPlayer(false);*/
        super.onPause();
    }

    /** Called when the activity is about to be destroyed. */
    @Override
    protected void onDestroy()
    {
        mPictureTaker.Shutdown();
        super.onDestroy();
    }

    // disable volume graphic
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) return true;

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (!mKeyPressed) {
                Log.i("DORIS","keydown");
                mKeyPressed = true;
                mStartTime = System.currentTimeMillis();
            }
            else
            {
                long elapsed = System.currentTimeMillis() - mStartTime;
                if (elapsed>1000) {
                    // increment string id
                    DorisIDs.IncString();
                    Log.i("DORIS","TAKING PICTURE -------->");
                    mPictureTaker.TakePicture(mCameraPreview,mPicture);
                    Log.i("DORIS","TAKEN PICTURE <--------");
                    mKeyPressed=false;
                }
            }

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) return true;

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (!mPictureTaker.mTakingPicture && mKeyPressed) {

                mKeyPressed=false;
                long elapsed = System.currentTimeMillis() - mStartTime;

                Log.i("DORIS","elapsed:"+elapsed);

                if (elapsed>10) {

                    if (elapsed>1000) {
                        return true;
                    }
                    else
                    {
                        // increment the lobster id
                        DorisIDs.IncLobster();
                    }

                    Log.i("DORIS","TAKING PICTURE -------->");
                    mPictureTaker.TakePicture(mCameraPreview,mPicture);
                    Log.i("DORIS","TAKEN PICTURE <--------");
                }
            }
            return true;
        }
        return false;
    }


    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
			// get a file name for the photo to be uploaded

            Log.i("DORIS","ON PICTURE TAKEN");

            String datetime = getDateTime();
			String photoName = datetime + ".jpg";
			String dataName = datetime + ".txt";

            Log.i("DORIS","ON PICTURE TAKEN2");

            String bakdata=
                DorisIDs.GetIDString()+"\n"+
                locationListener.mLatitude+"\n"+
                locationListener.mLongitude+"\n"+
                datetime;

            Log.i("DORIS","ON PICTURE TAKEN3 "+DorisIDs.GetIDString());

            Uri uri = DorisIDs.getUri(photoName,"backup");
            Uri data_uri  = DorisIDs.getUri(dataName,"backup");

            DorisFileUtils.SaveData(uri,data);
            DorisFileUtils.SaveData(data_uri,bakdata.getBytes());

            Log.i("DORIS","ON PICTURE TAKEN4");

            setResult(RESULT_OK);
            finish();
        }
    };


	public static String getDateTime() {
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(new Date());
	}



}
