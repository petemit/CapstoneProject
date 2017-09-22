package us.mindbuilders.petemit.timegoalie;

import android.app.Application;

/**
 * Created by Peter on 9/22/2017.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        getDatabasePath("timeGoalie.db").delete();
    }
}
