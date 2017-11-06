package us.mindbuilders.petemit.timegoalie;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class StethoEnabler{
    public static void enable(Application application) {
        Stetho.initializeWithDefaults(application);
    }
}