package us.mindbuilders.petemit.timegoalie.utils;

import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by Peter on 10/12/2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    RemoteViews views;
    int id;
    public CustomTextView(Context context, RemoteViews views, int id) {
        super(context);
        this.views=views;
        this.id=id;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (text!= null && views != null) {
            views.setTextViewText(id, text);
        }
    }
}
