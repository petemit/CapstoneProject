package us.mindbuilders.petemit.timegoalie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Peter on 9/21/2017.
 */

public class TimeGoalieDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static String DB_NAME = "timeGoalie.db";

    public TimeGoalieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create goals table
        final String createGoalsSQL = "CREATE TABLE IF NOT EXISTS " +
                TimeGoalieContract.Goals.GOALS_TABLE_NAME +
                "(" + TimeGoalieContract.Goals._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_NAME + " TEXT NOT NULL, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS + " INTEGER, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES + " INTEGER, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID + " INTEGER, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY + " BOOLEAN, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY + " BOOLEAN, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISTODAYONLY + " TIMESTAMP, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISDISABLED + " BOOLEAN);";
        db.execSQL(createGoalsSQL);

        //create goalentries table
        final String createGoalEntriesTable = "CREATE TABLE " +
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME +
                "(" + TimeGoalieContract.GoalEntries._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME + " TIMESTAMP NOT NULL, " +
        TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED + " INTEGER);";
        db.execSQL(createGoalEntriesTable);

        //create goaltypes table
        final String createGoalTypesSQL = "CREATE TABLE " +
                TimeGoalieContract.GoalTypes.GOALTYPES_TABLE_NAME +
                "(" + TimeGoalieContract.GoalTypes._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.GoalTypes.GOALTYPES_COLUMN_NAME + " TEXT NOT NULL);";
        db.execSQL(createGoalTypesSQL);

        //create Days table
        final String createDaysSQL = "CREATE TABLE " +
                TimeGoalieContract.Days.DAYS_TABLE_NAME +
                "(" + TimeGoalieContract.Days._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.Days.DAYS_COLUMN_NAME + " TEXT NOT NULL, " +
                TimeGoalieContract.Days.DAYS_COLUMN_SEQUENCE + " INTEGER NOT NULL);";
        db.execSQL(createDaysSQL);


        //create GoalsDays table
        final String createGoalsDaysSQL = "CREATE TABLE " +
                TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME +
                "(" + TimeGoalieContract.GoalsDays._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.GoalsDays
                        .GOALS_DAYS_COLUMN_GOAL_ID + " INTEGER NOT NULL, " +
                TimeGoalieContract.GoalsDays
                        .GOALS_DAYS_COLUMN_DAY_ID + " INTEGER NOT NULL);";
        db.execSQL(createGoalsDaysSQL);

        //create GoalsDatesAccomplished table
        final String createGoalsDatesAccomplishedSQL = "CREATE TABLE " +
                TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME +
                "(" + TimeGoalieContract.GoalsDatesAccomplished._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.GoalsDatesAccomplished
                        .GOALS_DATES_ACCOMPLISHED_COLUMN_GOAL_ID + " INTEGER NOT NULL, " +
                TimeGoalieContract.GoalsDatesAccomplished
                        .GOALS_DATES_ACCOMPLISHED_COLUMN_DATE + " TIMESTAMP NOT NULL);";
        db.execSQL(createGoalsDatesAccomplishedSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        //todo implement upgrade logic
        db.execSQL("DROP TABLE IF EXISTS " + TimeGoalieContract.Goals.GOALS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TimeGoalieContract.Days.DAYS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TimeGoalieContract.GoalTypes.GOALTYPES_TABLE_NAME );
        db.execSQL("DROP TABLE IF EXISTS " + TimeGoalieContract.GoalsDays.GOALS_DAYS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TimeGoalieContract.GoalsDatesAccomplished.GOALS_DATES_ACCOMPLISHED_TABLE_NAME);
        onCreate(db);

    }
}
