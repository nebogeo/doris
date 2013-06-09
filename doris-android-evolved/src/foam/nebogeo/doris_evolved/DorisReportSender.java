package foam.nebogeo.doris_evolved;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.acra.*;
import org.acra.sender.*;
import org.acra.collector.*;

import android.content.Context;

import android.util.Log;

public class DorisReportSender implements ReportSender {

    private final Map<ReportField, String> mMapping = new HashMap<ReportField, String>() ;
    private FileOutputStream crashReport = null;

    public DorisReportSender(Context ctx) {
    }

	public static String getDateTime() {
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(new Date());
	}

    @Override
    public void send(CrashReportData report) throws ReportSenderException {
        DorisFileUtils.SaveData(DorisIDs.getUri("crash_"+getDateTime(),"crashes"),
                                report.toString().getBytes());
    }
}
