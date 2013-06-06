/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package foam.nebogeo.doris_evolved;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.os.Environment;
import android.content.Context;
import android.text.TextUtils;
import com.google.gson.JsonSyntaxException;
import android.util.Log;

import foam.nebogeo.doris_evolved.GsonHelper;
import foam.nebogeo.doris_evolved.UshahidiApiResponse;

/**
 * @author eyedol
 */
public class DorisHttpClient extends BaseHttpClient {

	private static MultipartEntity entity;

    public int totalReports = 20;

	/**
	 * @param context
	 */
	private Context context;

	public DorisHttpClient(Context context, String domain) {
		super(context,domain);
		this.context = context;
	}


	/**
	 * Upload files to server 0 - success, 1 - missing parameter, 2 - invalid
	 * parameter, 3 - post failed, 5 - access denied, 6 - access limited, 7 - no
	 * data, 8 - api disabled, 9 - no task found, 10 - json is wrong
	 */
	public boolean PostFileUpload(String URL, HashMap<String, String> params)
        throws IOException {
		Log.i("DORIS","PostFileUpload(): upload file to server.");

		entity = new MultipartEntity();
		// Dipo Fix
		try {
			// wrap try around because this constructor can throw Error
			final HttpPost httpost = new HttpPost(URL);

			if (params != null) {

				for(Entry<String, String> en: params.entrySet()){
					String key = en.getKey();
					if ( key == null || "".equals(key)){
						continue;
					}
					String val = en.getValue();
					if( !"filename".equals(key)){
						entity.addPart(
								key,
								new StringBody(val, Charset.forName("UTF-8"))
						);
						continue;
					}

					if (!TextUtils.isEmpty(val)) {
						String filenames[] = val.split(",");
						log("filenames "+filenames[0]);
						for (int i = 0; i < filenames.length; i++) {
                            File file = new File(Environment.getExternalStorageDirectory(),
                                                 "foam.nebogeo.doris_evolved/backup/"+filenames[i]);
                            Log.i("DORIS",file.getPath());
                            if (file.exists()) {
                                Log.i("DORIS","adding file...");
                                entity.addPart("incident_photo[]",
                                               new FileBody(file));
                            }
                            else
                            {
                                Log.i("DORIS","couldn't find file");
                            }
						}
					}
				}

                Log.i("DORIS","1");

				// NEED THIS NOW TO FIX ERROR 417
				httpost.getParams().setBooleanParameter(
						"http.protocol.expect-continue", false);

                Log.i("DORIS","2");

				httpost.setEntity(entity);

                Log.i("DORIS","SENDING...");
				HttpResponse response = httpClient.execute(httpost);
                Log.i("DORIS","4");

				httpRunning = false;
                HttpEntity respEntity = response.getEntity();
                Log.i("DORIS","5");

                String res = EntityUtils.toString(respEntity);
                Log.i("DORIS",res);

				if (respEntity != null) {

                    try{
                        UshahidiApiResponse resp = GsonHelper.fromString(res, UshahidiApiResponse.class);
                        return resp.getErrorCode() == 0;

                    } catch (JsonSyntaxException e) {
                        log("JsonSyntaxException", e);
                        return false;
                    }

				}
			}

		} catch (MalformedURLException ex) {
			log("PostFileUpload(): MalformedURLException", ex);

			return false;
			// fall through and return false
		} catch (IllegalArgumentException ex) {
			log("IllegalArgumentException", ex);
			// invalid URI
			return false;
		} catch (ConnectTimeoutException ex) {
			//connection timeout
			log("ConnectionTimeoutException");
			return false;
		}catch(SocketTimeoutException ex) {
			log("SocketTimeoutException");
		} catch (IOException e) {
			log("IOException", e);
			// timeout
			return false;
        }

        Log.i("DORIS","DONE SENDING ALL GOOD");
		return false;
	}
}
