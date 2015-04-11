package dutta.sayon.com.irrigationscheduling;

import android.app.Application;
import android.content.Context;

/**
 * Created by rocky on 29/3/15.
 */
public class IrrigationScheduling extends Application {

    private static IrrigationScheduling sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static IrrigationScheduling getsInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
