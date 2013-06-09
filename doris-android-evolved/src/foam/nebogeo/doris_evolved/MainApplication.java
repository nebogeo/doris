package foam.nebogeo.doris_evolved;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes( formKey = "" )
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        ErrorReporter.getInstance().setReportSender(new DorisReportSender(this));
    }
}
