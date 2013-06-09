/*
 	android.os.AsyncTask * Copyright (C) 2010 The Android Open Source Project
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
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Intent;

import android.widget.EditText;
import android.widget.Button;

public class DorisPref extends Activity {

    private EditText tripname;
    private Button ok;
    private Button cancel;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.pref);

        tripname = (EditText) findViewById(R.id.trip);
        tripname.setText(DorisIDs.GetTrip());

        ok = (Button) findViewById(R.id.ok_button);
        cancel = (Button) findViewById(R.id.cancel_button);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DorisIDs.SetTrip(tripname.getText().toString());
                DorisIDs.ResetID();
                setResult(RESULT_OK);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }
}
