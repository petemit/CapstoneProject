package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * ContentProvider for TimeGoalieDb
 */

public class TimeGoalieContentProvider extends ContentProvider {
    private static final int GOAL = 100;
    private static final int GOAL_BY_ID = 101;
    private static final int GOALS_BY_TODAYS_DATE = 102;
    private static final int GOALTYPES = 200;
    private static final int GOALTYPE_BY_ID = 201;
    private static final int DAYS = 300;
    private static final int DAY_BY_ID = 301;
    private static final int DAY_BY_DATE_SEQUENCE = 302;
    private static final int GOALS_ACCOMPLISHED_BY_GOAL_ID_BY_DATE = 405;
    private static final int GOALS_DAYS = 500;
    private static final int GOAL_DAYS_BY_GOAL_ID = 505;
    private static final int GOALS_DAYS_BY_GOAL_ID_QUERY =508 ;
    private static final int GOALS_BY_DAY_ID = 501;
    private static final int GOAL_ENTRIES = 600;
    private static final int GOAL_ENTRIES_BY_GOAL_ID = 601;
    private static final int GOAL_ENTRIES_BY_DATE = 602;
    private static final int SUCCESSFUL_GOAL_ENTRIES_BY_DATE = 603;
    private static final int GOAL_ENTRY_BY_GOAL_ENTRY_ID = 604;
    private static final int GOALS_ENTRIES_ACCOMPLISHED_BY_WEEK = 605;
    private static final int GOALS_ENTRIES_ACCOMPLISHED_BY_MONTH = 606;
    private static final int GOALS_ENTRIES_ACCOMPLISHED_BY_WEEK_BY_GOAL_ID = 607;
    private static final int GOALS_ENTRIES_ACCOMPLISHED_BY_MONTH_BY_GOAL_ID = 608;
    private static final int GOAL_ENTRIES_RUNNING = 609;
    private static final int GOAL_ENTRIES_ALL_BY_GOAL_ID = 610;
    private static final UriMatcher uriMatcher = makeUriMatcher();
    private static final String PARAMETER = "=? ";

    private TimeGoalieDbHelper dbHelper;

    private static UriMatcher makeUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //goals
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.Goals.GOALS_TABLE_NAME, GOAL);
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
                TimeGoalieContract.Days.DAYS_TABLE_NAME + "/date/#", DAY_BY_DATE_SEQUENCE);
        //goaldatesaccomplished

        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                        "/goal/*", GOALS_ACCOMPLISHED_BY_GOAL_ID_BY_DATE);
        //goaldays
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME + "/#", GOALS_BY_DAY_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME, GOALS_DAYS);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME + "/goal/#", GOAL_DAYS_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME + "/getGoalDays/#", GOALS_DAYS_BY_GOAL_ID_QUERY);


        //goalentries
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME, GOAL_ENTRIES);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME + "/goal/#", GOAL_ENTRIES_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME + "/goal/all/#", GOAL_ENTRIES_ALL_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME + "/date", GOAL_ENTRIES_BY_DATE);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME + "/successfulGoals",
                SUCCESSFUL_GOAL_ENTRIES_BY_DATE);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME + "/#", GOAL_ENTRY_BY_GOAL_ENTRY_ID);


        //goalentries report queries
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME +
                        "/weeks", GOALS_ENTRIES_ACCOMPLISHED_BY_WEEK);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME +
                        "/months", GOALS_ENTRIES_ACCOMPLISHED_BY_MONTH);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME +
                        "/weeks/goal/#", GOALS_ENTRIES_ACCOMPLISHED_BY_WEEK_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME +
                        "/months/goal/#", GOALS_ENTRIES_ACCOMPLISHED_BY_MONTH_BY_GOAL_ID);
        matcher.addURI(TimeGoalieContract.AUTHORITY,
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME + "/running/date",
                GOAL_ENTRIES_RUNNING);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        dbHelper = new TimeGoalieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String suppliedDate, @Nullable String[] suppliedSelectionArgs, @Nullable String s1) {
        String date = TimeGoalieDateUtils.getSqlDateString();
        if (suppliedDate != null) {
            date = suppliedDate;
        }
        Cursor cursor = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "";
        String[] selectionArgs = null;
        Calendar today = null;
        String dateString = "";
        switch (uriMatcher.match(uri)) {

            //goals

            case GOAL:
                cursor = db.query(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case GOAL_BY_ID:
                selection = TimeGoalieContract.Goals._ID.concat(PARAMETER);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            case GOALS_BY_TODAYS_DATE:
                selection = TimeGoalieContract.Goals.GOALS_COLUMN_CREATIONDATE.concat(PARAMETER);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;

                //goaldays
            case GOALS_DAYS_BY_GOAL_ID_QUERY:
                selection = TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID.concat(PARAMETER);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            //days
            case DAYS:
                cursor = db.query(TimeGoalieContract.Days.DAYS_TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case DAY_BY_ID:
                selection = TimeGoalieContract.Days._ID.concat(PARAMETER);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TimeGoalieContract.Days.DAYS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            case DAY_BY_DATE_SEQUENCE:
                selection = TimeGoalieContract.Days.DAYS_COLUMN_SEQUENCE.concat(PARAMETER);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TimeGoalieContract.Days.DAYS_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            //Goal Entries
            case GOAL_ENTRIES_BY_DATE:
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME
                        .concat(PARAMETER);

                String gEtablesJointatement = TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME
                        .concat(" LEFT JOIN ")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(" on ")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.Goals._ID)
                        .concat("=")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID);


                cursor = db.query(gEtablesJointatement,
                        null,
                        selection,
                        suppliedSelectionArgs,
                        null,
                        null,
                        TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID);
                break;

            case GOAL_ENTRIES_BY_GOAL_ID:
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID
                        .concat(PARAMETER)
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat(" ='")
                        .concat(suppliedSelectionArgs[0])
                        .concat("'");
                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID);
                break;

            case GOAL_ENTRIES_ALL_BY_GOAL_ID:
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID
                        .concat(PARAMETER);
                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;


            case GOAL_ENTRY_BY_GOAL_ENTRY_ID:
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                selection = TimeGoalieContract.GoalEntries._ID
                        .concat(PARAMETER);
                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            case SUCCESSFUL_GOAL_ENTRIES_BY_DATE:
                selectionArgs = new String[]{String.valueOf(uri.getQueryParameter("date")), "1"};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME
                        .concat(PARAMETER)
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED)
                        .concat(PARAMETER);
                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;

            case GOALS_ENTRIES_ACCOMPLISHED_BY_WEEK:
                today = Calendar.getInstance();
                today.add(Calendar.WEEK_OF_YEAR,
                        -Integer.parseInt(uri.getQueryParameter("numOfWeeks")));

                dateString = TimeGoalieDateUtils.getSqlDateString(today);
                selectionArgs = new String[]{dateString};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED
                        .concat("=")
                        .concat("'1'")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat(">=?");

                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;

            case GOALS_ENTRIES_ACCOMPLISHED_BY_WEEK_BY_GOAL_ID:
                today = Calendar.getInstance();
                today.add(Calendar.WEEK_OF_YEAR,
                        -Integer.parseInt(uri.getQueryParameter("numOfWeeks")));
                dateString = TimeGoalieDateUtils.getSqlDateString(today);
                //make the date and the goalid the arguments;
                selectionArgs = new String[]{dateString, String.valueOf(ContentUris.parseId(uri))};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED
                        .concat("=")
                        .concat("'1'")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat(">=?")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID)
                        .concat(PARAMETER);

                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;

            case GOALS_ENTRIES_ACCOMPLISHED_BY_MONTH:
                today = Calendar.getInstance();
                today.add(Calendar.MONTH,
                        -Integer.parseInt(uri.getQueryParameter("numOfMonths")));
                dateString = TimeGoalieDateUtils.getSqlDateString(today);
                selectionArgs = new String[]{dateString};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED
                        .concat("=")
                        .concat("'1'")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat(">=?");

                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;

            case GOALS_ENTRIES_ACCOMPLISHED_BY_MONTH_BY_GOAL_ID:
                today = Calendar.getInstance();
                today.add(Calendar.MONTH,
                        -Integer.parseInt(uri.getQueryParameter("numOfMonths")));
                dateString = TimeGoalieDateUtils.getSqlDateString(today);
                selectionArgs = new String[]{dateString, String.valueOf(ContentUris.parseId(uri))};
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED
                        .concat("=")
                        .concat("'1'")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat(">=?")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID)
                        .concat(PARAMETER);

                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;


            case GOAL_ENTRIES_RUNNING:
                selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME
                        .concat(PARAMETER)
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISRUNNING)
                        .concat(" = 1 ");

                cursor = db.query(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        selection,
                        suppliedSelectionArgs,
                        null,
                        null,
                        TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID);
                break;

            //goaldays
            case GOALS_BY_DAY_ID:


                selection = TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_DAY_ID
                        .concat(PARAMETER)
                        .concat(" UNION ALL ")
                        .concat(" SELECT * FROM ")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(" left join ")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME)
                        .concat(" on ")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.Goals._ID)
                        .concat("=")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID)
                        .concat(" LEFT JOIN ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME)
                        .concat(" on (")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.Goals._ID)
                        .concat("=")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID)
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat("='")
                        .concat(date)
                        .concat("')")
                        .concat(" WHERE ")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID)
                        .concat(" IS NULL ")
                        .concat(" AND ")
                        .concat(TimeGoalieContract.Goals.GOALS_COLUMN_CREATIONDATE)
                        .concat("='")
                        .concat(date)
                        .concat("'")
                        .concat(" OR ")
                        .concat(TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY)
                        .concat("=1 ");

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                String tablesJoinStatement = TimeGoalieContract.Goals.GOALS_TABLE_NAME
                        .concat(" LEFT JOIN ")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME)
                        .concat(" on ")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.Goals._ID)
                        .concat("=")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID)
                        .concat(" LEFT JOIN ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME)
                        .concat(" on (")
                        .concat(TimeGoalieContract.Goals.GOALS_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.Goals._ID)
                        .concat("=")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME)
                        .concat(".")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID)
                        .concat(" AND ")
                        .concat(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)
                        .concat("='")
                        .concat(date)
                        .concat("')");


                cursor = db.query(tablesJoinStatement,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        TimeGoalieContract.Goals._ID);
                break;

        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case GOAL:
                long goal_id = db.insert(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        null,
                        contentValues);

                if (goal_id > -1) {
                    Uri returnUri = TimeGoalieContract.buildGetaGoalByIdUri(goal_id);
                    getContext().getContentResolver().notifyChange(returnUri, null);
                    return returnUri;
                } else {
                    throw new SQLException("Insert failed!");
                }
            case GOALS_DAYS:
                long goal_day_id = db.insert(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME,
                        null,
                        contentValues);
                if (goal_day_id > -1) {
                    Uri returnUri = TimeGoalieContract.buildGetaGoalByIdUri(goal_day_id);
                    getContext().getContentResolver().notifyChange(returnUri, null);
                    return returnUri;
                } else {

                    throw new SQLException("Insert failed!");
                }

            case GOAL_ENTRIES:
                String selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID +
                        PARAMETER;
                String[] selectionArgs = new String[]{contentValues.getAsLong(
                        TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID) + ""};
//
//                db.update(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,contentValues,
//                        selection,selectionArgs);

                long goal_entry_id = db.insertWithOnConflict(
                        TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (goal_entry_id > -1) {
                    Uri returnUri = TimeGoalieContract.buildGetAGoalEntryByGoalId(goal_entry_id);
                    getContext().getContentResolver().notifyChange(returnUri, null);
                    return returnUri;
                }
            default:
                throw new UnsupportedOperationException("That insert query didn't work, dude");
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case GOAL_ENTRIES_ALL_BY_GOAL_ID:

                String[] goal_id = {String.valueOf(ContentUris.parseId(uri))};
                if (goal_id.length == 0) {
                    return 0;
                }

                String selection = TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID + "=?";

                int rowsdeletedByGoalEntries = db.delete(TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME,
                        selection,
                        goal_id);
                if (goal_id.length > 0) {

                    return rowsdeletedByGoalEntries;

                } else {
                    throw new SQLException("Delete failed!");
                }

            case GOAL_BY_ID:
                String[] _id = {String.valueOf(ContentUris.parseId(uri))};
                if (_id.length == 0) {
                    return 0;
                }

                String mSelection = TimeGoalieContract.Goals._ID + "=?";

                int rowsdeleted = db.delete(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        mSelection,
                        _id);
                if (_id.length > 0) {

                    return rowsdeleted;

                } else {
                    throw new SQLException("Delete failed!");
                }

            case GOAL_DAYS_BY_GOAL_ID:
                String[] anotherGoal_id = {String.valueOf(ContentUris.parseId(uri))};
                if (anotherGoal_id.length == 0) {
                    return 0;
                }

                String goalDaySelection = TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID + "=?";

                int goalDayRowsdeleted = db.delete(TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME,
                        goalDaySelection,
                        anotherGoal_id);
                if (anotherGoal_id.length > 0) {

                    return goalDayRowsdeleted;

                } else {
                    throw new SQLException("Delete failed!");
                }


            default:
                throw new UnsupportedOperationException("that delete query is not supported, man.");
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String whereClause, @Nullable String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case GOAL_BY_ID:
                String[] _id = {String.valueOf(ContentUris.parseId(uri))};
                if (_id.length == 0) {
                    return 0;
                }

                int rowsUpdated = db.update(TimeGoalieContract.Goals.GOALS_TABLE_NAME,
                        contentValues, whereClause, strings);
                if (_id.length > 0) {

                    return rowsUpdated;

                } else {
                    throw new SQLException("Update failed!");
                }
            default:
                throw new UnsupportedOperationException("that Update query is not supported, man.");
        }

    }
}
