package us.mindbuilders.petemit.timegoalie;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.facebook.stetho.Stetho;

/**
 * Created by Peter on 9/22/2017.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        getDatabasePath("timeGoalie.db").delete();
        Stetho.initializeWithDefaults(this);
    }

}
