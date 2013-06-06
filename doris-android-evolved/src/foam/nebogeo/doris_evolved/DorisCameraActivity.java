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
                    IncString();
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
                        IncLobster();
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
                GetIDString()+"\n"+
                locationListener.mLatitude+"\n"+
                locationListener.mLongitude+"\n"+
                datetime;

            Log.i("DORIS","ON PICTURE TAKEN3 "+GetIDString());

            Uri uri = getUri(photoName,"backup");
            Uri data_uri  = getUri(dataName,"backup");

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

    public void checkStorage() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        Log.i("DORIS", "storage check: availible:"+mExternalStorageAvailable+
              " writable:"+mExternalStorageWriteable);
    }

	public Uri getUri(String filename, String dir) {
		File path = new File(Environment.getExternalStorageDirectory(),
				getPackageName() + "/" + dir);
        Log.i("DORIS","might be making dir...");

        Log.i("DORIS",getPackageName() + "/" + dir);

		if (!path.exists() && path.mkdirs()) {
            Log.i("DORIS","path not there and made dir");
			return Uri.fromFile(new File(path, filename));
		}
        Log.i("DORIS",""+path.mkdir()+" "+path.exists());

        Log.i("DORIS","path there or not made dir");

		return Uri.fromFile(new File(path, filename));
	}

    public String GetIDString() {
        Uri TripUri = getUri("Trip.txt","IDS");
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        Uri StringUri = getUri("String.txt","IDS");

        return GetID(TripUri)+"-"+GetID(StringUri)+"-"+GetID(LobsterUri);
    }

    public void ResetID() {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        Uri StringUri = getUri("String.txt","IDS");
        SetID(LobsterUri,0);
        SetID(StringUri,0);
    }

    private void IncLobster() {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        IncID(LobsterUri);
    }

    private void IncString() {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        Uri StringUri = getUri("String.txt","IDS");
        IncID(StringUri);
        SetID(LobsterUri,1);
    }

    public void SetTrip(String trip) {
        Uri TripUri = getUri("Trip.txt","IDS");
        SetText(TripUri,trip);
    }

    public void SetLobster(int v) {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        SetID(LobsterUri,v);
    }

    public void SetString(int v) {
        Uri StringUri = getUri("String.txt","IDS");
        SetID(StringUri,v);
    }

    private int GetID(Uri uri) {
        String sid=DorisFileUtils.LoadData(uri);
        if (sid=="") {
            Log.i("DORIS","get id firsttime");
            DorisFileUtils.SaveData(uri,"1\0".getBytes());
            return 1;
        }
        else
        {
            Log.i("DORIS","get id found");
            int id=Integer.parseInt(sid);
            return id;
        }
    }

    private void IncID(Uri uri) {
        String sid=DorisFileUtils.LoadData(uri);
        if (sid=="") {
            Log.i("DORIS","get id firsttime");
            DorisFileUtils.SaveData(uri,"1\0".getBytes());
        }
        else
        {
            Log.i("DORIS","get id found");
            int id=Integer.parseInt(sid);
            id++;
            String temp=""+id+"\0";
            DorisFileUtils.SaveData(uri,temp.getBytes());
        }
    }

    private void SetText(Uri uri, String v) {
        Log.i("DORIS","setting id");
        String t=v+"\0";
        DorisFileUtils.SaveData(uri,t.getBytes());
    }

    private void SetID(Uri uri, int v) {
        Log.i("DORIS","setting id");
        String t=""+v+"\0";
        DorisFileUtils.SaveData(uri,t.getBytes());
    }


}
