package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * ContentProvider for TimeGoalieDb
 */

public class TimeGoalieContentProvider extends ContentProvider {
    private TimeGoalieDbHelper dbHelper;
    private static final UriMatcher uriMatcher = makeUriMatcher();

    private static final int GOAL_BY_ID = 101;
    private static final int GOALS_BY_TODAYS_DATE = 102;

    private static final int GOALTYPES = 200;
    private static final int GOALTYPE_BY_ID = 201;

    private static final int DAYS = 300;
    private static final int DAY_BY_ID = 301;
    private static final int DAY_BY_DATE = 302;

    private static final int GOALS_ACCOMPLISHED_BY_WEEK = 401;
    private static final int GOALS_ACCOMPLISHED_BY_MONTH = 402;
    private static final int GOALS_ACCOMPLISHED_BY_WEEK_BY_GOAL_ID = 403;
    private static final int GOALS_ACCOMPLISHED_BY_MONTH_BY_GOAL_ID = 404;
    private static final int GOALS_ACCOMPLISHED_BY_GOAL_ID_BY_DATE = 405;

    private static final int GOALS_BY_DAY_ID = 500;

    private static final String PARAMETER = "=? ";

    private static UriMatcher makeUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //goals
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.Goals.GOALS_TABLE_NAME + "/#", GOAL_BY_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.Goals.GOALS_TABLE_NAME + "/date/#", GOALS_BY_TODAYS_DATE);
        //goaltypes
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalTypes.GOALTYPES_TABLE_NAME, GOALTYPES);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalTypes.GOALTYPES_TABLE_NAME + "/#", GOALTYPE_BY_ID);
        //days
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.Days.DAYS_TABLE_NAME, DAYS);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.Days.DAYS_TABLE_NAME + "/#", DAY_BY_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.Days.DAYS_TABLE_NAME + "/date/#", DAY_BY_DATE);
        //goaldatesaccomplished
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                        "/week/#", GOALS_ACCOMPLISHED_BY_WEEK);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                        "/month/#", GOALS_ACCOMPLISHED_BY_MONTH);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                        "/goal/#/week/#", GOALS_ACCOMPLISHED_BY_WEEK_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                        "/goal/#/month/#", GOALS_ACCOMPLISHED_BY_MONTH_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                        "/goal/#/date/#", GOALS_ACCOMPLISHED_BY_GOAL_ID_BY_DATE);
        //goaldays
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME + "/#", GOALS_BY_DAY_ID);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        dbHelper = new TimeGoalieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case GOAL_BY_ID:
                String selection = TimeGoalieContract.Goals._ID.concat(PARAMETER);
                String[] selectionArgs = {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
