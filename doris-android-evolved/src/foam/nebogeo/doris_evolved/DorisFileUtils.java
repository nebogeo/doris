package foam.nebogeo.doris_evolved;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class DorisFileUtils {

	public static String formatDate(String dateFormat, String date,
			String toFormat, Locale fromLocale, Locale toLocale) {

		String formatted = "";

		DateFormat formatter = fromLocale == null?
				new SimpleDateFormat(dateFormat):
				new SimpleDateFormat(dateFormat, fromLocale);
		try {
			Date dateStr = formatter.parse(date);
			formatted = formatter.format(dateStr);
			Date formatDate = formatter.parse(formatted);
			formatter = toLocale == null ?
					new SimpleDateFormat(toFormat):
					new SimpleDateFormat(toFormat, toLocale);
			formatted = formatter.format(formatDate);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return formatted;
	}


    static public void SaveData(Uri uri, byte[] data) {
        try {
            File file = new File(new URI(uri.toString()));

            if (file == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        } catch (Exception e) {
        }
    }

    static public String LoadData(Uri uri) {
        return LoadDataFromString(uri.toString());
    }

    static public String LoadDataFromString(String fname) {
        try {
            return LoadDataFromFile(new File(new URI(fname)));
        } catch (Exception e) {
        }
        return "";
    }

    static public String LoadDataFromFile(File file) {
        try {
            if (file == null) {
                return "";
            }
            try {
                FileInputStream fis = new FileInputStream(file);
                String ret="";
                StringBuffer fileContent = new StringBuffer("");
                byte[] buffer = new byte[1024];
                int length;
                if ((length = fis.read(buffer)) != -1) {
                    String t=new String(buffer).split("\0")[0]; // hacky null terminator
                    //Log.i("DORIS",t);
                    ret=t;
                }
                fis.close();
                return ret;

            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        } catch (Exception e) {
        }

        return "";
    }

}
