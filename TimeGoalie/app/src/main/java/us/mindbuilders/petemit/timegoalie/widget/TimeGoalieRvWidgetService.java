package us.mindbuilders.petemit.timegoalie.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieRvWidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e("findme","I got here!!!");
        return new TimeGoalieWidgetListRemoteViewsFactory(this.getApplicationContext());
    }
}
