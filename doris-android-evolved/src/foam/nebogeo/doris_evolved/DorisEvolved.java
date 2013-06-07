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
import android.content.Context;
import android.util.Log;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;
import android.view.KeyEvent;

import java.io.File;
import java.io.IOException;
import android.os.Environment;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Locale;

public class DorisEvolved extends Activity {

    static final int LOBSTER_DATA_ID = 0;
    static final int LOBSTER_DATA_LAT = 1;
    static final int LOBSTER_DATA_LON = 2;
    static final int LOBSTER_DATA_TIME = 3;

    static AssetManager assetManager;

    private CameraPreview mCameraPreview;
    private PictureTaker mPictureTaker;
    private long mStartTime=0;
    private Boolean mKeyPressed=false;

    private DorisHttpClient httpClient;

    private ListView lobsterList;
    private TextView mID;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.main);

        DorisIDs.SetPackageName(getPackageName());

        httpClient = new DorisHttpClient(this,"http://dorismap.exeter.ac.uk/");
        assetManager = getAssets();

        final Button button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("DORIS","sending...");
                Upload();
            }
        });

        lobsterList = (ListView) findViewById(R.id.listview);
        UpdateLobsterList();

        mID = (TextView) findViewById(R.id.incident_next_id);
        mID.setText(DorisIDs.GetIDString());

    }

    private void UpdateLobsterList() {
        File dir = new File(Environment.getExternalStorageDirectory(),
				getPackageName()+"/backup");
        final ArrayList<HashMap> list = new ArrayList<HashMap>();
        for (File child : dir.listFiles()) {
            String fname = child.getName();
            if (fname.endsWith(".txt")) {
                String data[]=DorisFileUtils.LoadDataFromFile(child).split("\n");
                HashMap hm = new HashMap();
                hm.put("title",data[LOBSTER_DATA_ID]);
                hm.put("date",data[LOBSTER_DATA_TIME]);
                list.add(hm);
            }
        }
        final DorisLobsterAdapter adapter = new DorisLobsterAdapter(this,android.R.layout.simple_list_item_1, list);
        lobsterList.setAdapter(adapter);
    }


    private void Upload() {
        File dir = new File(Environment.getExternalStorageDirectory(),
                            getPackageName()+"/backup");
        for (File child : dir.listFiles()) {
            String fname = child.getName();
            if (fname.endsWith(".txt")) {
                Log.i("DORIS",fname);
                String data[]=DorisFileUtils.LoadDataFromFile(child).split("\n");
                // todo check some uploaded list first
                UploadLobster(data,data[LOBSTER_DATA_TIME]+".jpg");
            }
        }
    }

    private void UploadLobster(String data[], String photo) {
        Log.i("DORIS","uploading a lobster");

		boolean retVal = true;
		String time[];
		StringBuilder urlBuilder = new StringBuilder(httpClient.domain);
		urlBuilder.append("/api");

        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("task", "report");
        mParams.put("incident_title", data[LOBSTER_DATA_ID]);
        mParams.put("incident_description", "No description");

        Log.i("DORIS",data[LOBSTER_DATA_TIME]);

        // dates
//        String dates[] = DorisFileUtils
//            .formatDate("MMMM dd, yyyy 'at' hh:mm:ss aaa",
//                        data[LOBSTER_DATA_TIME], "MM/dd/yyyy hh:mm a", null,
//                        Locale.US).split(" ");

        String dates[] = DorisFileUtils
            .formatDate("yyyy_MM_dd_hh_mm_ss",
                        data[LOBSTER_DATA_TIME], "MM/dd/yyyy hh:mm a", null,
                        Locale.US).split(" ");


        for (String c : dates) {
            Log.i("DORIS", c);
        }

        time = dates[1].split(":");
        mParams.put("incident_date", dates[0]);
        mParams.put("incident_hour", time[0]);
        mParams.put("incident_minute", time[1]);
        mParams.put("incident_ampm", dates[2].toLowerCase());

// problem...
        Log.i("DORIS","categories hack");
//				mParams.put("incident_category", report.getCategories());
        mParams.put("incident_category", "1");

        mParams.put("latitude", data[LOBSTER_DATA_LAT]);
        mParams.put("longitude", data[LOBSTER_DATA_LON]);
        mParams.put("location_name", "No location");
        mParams.put("person_first", "");
        mParams.put("person_last", "");
        mParams.put("person_email", "");

        // load filenames
        mParams.put("filename", photo);

        // upload
        try {
            if (httpClient.PostFileUpload(urlBuilder.toString(), mParams)) {
                Log.i("DORIS","upload success!");

                Toast.makeText(this, "Uploaded "+data[LOBSTER_DATA_ID],
                               Toast.LENGTH_LONG).show();

            } else {
                retVal = false;
            }
        } catch (IOException e) {
            retVal = false;
        }
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
//        shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        mID.setText(DorisIDs.GetIDString());
        UpdateLobsterList();
        super.onDestroy();
    }

    // disable volume graphic
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) return true;

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mKeyPressed==false) {
                mKeyPressed=true;
                mStartTime = System.currentTimeMillis();
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
            if (mKeyPressed==true) {
                long elapsed = System.currentTimeMillis() - mStartTime;
                mKeyPressed=false;

                Log.i("DORIS",""+elapsed);

                if (elapsed>1000) {
                    Intent intent = new Intent(this,DorisCameraActivity.class);
                    startActivityForResult(intent, 2);
                }
            }
            return true;
        }
        else {
            return super.onKeyUp(keyCode, event);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UpdateLobsterList();
	}

}
