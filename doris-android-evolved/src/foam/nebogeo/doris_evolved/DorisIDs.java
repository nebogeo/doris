package foam.nebogeo.doris_evolved;

import java.io.File;
import android.os.Environment;
import android.util.Log;
import java.net.URI;
import android.net.Uri;

class DorisIDs {

    static private String PackageName="";

    static public void SetPackageName(String v) {
        PackageName=v;
    }

    static public void checkStorage() {
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

	static public Uri getUri(String filename, String dir) {
		File path = new File(Environment.getExternalStorageDirectory(),
				PackageName + "/" + dir);

        Log.i("DORIS",PackageName + "/" + dir);

		if (!path.exists() && path.mkdirs()) {
			return Uri.fromFile(new File(path, filename));
		}

		return Uri.fromFile(new File(path, filename));
	}

    static public String GetIDString() {
        Uri TripUri = getUri("Trip.txt","IDS");
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        Uri StringUri = getUri("String.txt","IDS");

        return GetID(TripUri)+"-"+GetID(StringUri)+"-"+GetID(LobsterUri);
    }

    static public void ResetID() {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        Uri StringUri = getUri("String.txt","IDS");
        SetID(LobsterUri,0);
        SetID(StringUri,0);
    }

    static public void IncLobster() {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        IncID(LobsterUri);
    }

    static public void IncString() {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        Uri StringUri = getUri("String.txt","IDS");
        IncID(StringUri);
        SetID(LobsterUri,1);
    }

    static public void SetTrip(String trip) {
        Uri TripUri = getUri("Trip.txt","IDS");
        SetText(TripUri,trip);
    }

    static public void SetLobster(int v) {
        Uri LobsterUri = getUri("Lobster.txt","IDS");
        SetID(LobsterUri,v);
    }

    static public void SetString(int v) {
        Uri StringUri = getUri("String.txt","IDS");
        SetID(StringUri,v);
    }

    static private int GetID(Uri uri) {
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

    static private void IncID(Uri uri) {
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

    static private void SetText(Uri uri, String v) {
        Log.i("DORIS","setting id");
        String t=v+"\0";
        DorisFileUtils.SaveData(uri,t.getBytes());
    }

    static private void SetID(Uri uri, int v) {
        Log.i("DORIS","setting id");
        String t=""+v+"\0";
        DorisFileUtils.SaveData(uri,t.getBytes());
    }
}
