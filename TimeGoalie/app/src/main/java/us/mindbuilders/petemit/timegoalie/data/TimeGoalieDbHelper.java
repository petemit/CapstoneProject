package us.mindbuilders.petemit.timegoalie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import us.mindbuilders.petemit.timegoalie.R;

/**
 * Created by Peter on 9/21/2017.
 */

public class TimeGoalieDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static String DB_NAME = "timeGoalie.db";
    private Context context;

    public TimeGoalieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context=context;
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
                TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID + " INTEGER NOT NULL, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY + " BOOLEAN, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY + " BOOLEAN, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_CREATIONDATE + " TEXT NOT NULL, " +
                TimeGoalieContract.Goals.GOALS_COLUMN_ISDISABLED + " BOOLEAN);";
        db.execSQL(createGoalsSQL);

        //create goalentries table
        final String createGoalEntriesTable = "CREATE TABLE " +
                TimeGoalieContract.GoalEntries.GOALENTRIES_TABLE_NAME +
                "(" + TimeGoalieContract.GoalEntries._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED + " INTEGER, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT + " INTEGER, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED + " INTEGER, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISRUNNING + " INTEGER, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISFINISHED + " INTEGER, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_TARGETTIME + " INTEGER, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID + " INTEGER NOT NULL, "+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME + " TEXT NOT NULL, "+
        " UNIQUE("+
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID + "," +
                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME +
                ") "+
                " ON CONFLICT REPLACE )";
        db.execSQL(createGoalEntriesTable);

        //create goaltypes table
        final String createGoalTypesSQL = "CREATE TABLE " +
                TimeGoalieContract.GoalTypes.GOALTYPES_TABLE_NAME +
                "(" + TimeGoalieContract.GoalTypes._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.GoalTypes.GOALTYPES_COLUMN_NAME + " TEXT NOT NULL);";
        db.execSQL(createGoalTypesSQL);


        String[] goalTypes = context.getResources()
                .getStringArray(R.array.goal_type_array);
        String[] goalTypeValues = context.getResources()
                .getStringArray(R.array.goal_type_array_values);
        //populate goalTypes data
        String populateGoalTypesSql = "insert into " +
                TimeGoalieContract.GoalTypes.GOALTYPES_TABLE_NAME +
                "(" + TimeGoalieContract.GoalTypes.GOALTYPES_COLUMN_NAME +
                ", " + TimeGoalieContract.GoalTypes._ID +
                ") " + "VALUES ";
        for (int i = 0; i < goalTypes.length; i++) {
            populateGoalTypesSql= populateGoalTypesSql.concat(" (");
            populateGoalTypesSql =  populateGoalTypesSql.concat(buildInsertSqlfromStringArray
                    (new String[]{'"'+goalTypes[i]+'"', goalTypeValues[i]}));
            if (i!=goalTypes.length-1) {
                populateGoalTypesSql = populateGoalTypesSql.concat("), ");
            }
            else{
                populateGoalTypesSql = populateGoalTypesSql.concat(")");
            }
        }
        db.execSQL(populateGoalTypesSql);

        //create Days table
        final String createDaysSQL = "CREATE TABLE " +
                TimeGoalieContract.Days.DAYS_TABLE_NAME +
                "(" + TimeGoalieContract.Days._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeGoalieContract.Days.DAYS_COLUMN_NAME + " TEXT NOT NULL, " +
                TimeGoalieContract.Days.DAYS_COLUMN_SEQUENCE + " INTEGER NOT NULL);";
        db.execSQL(createDaysSQL);


        String[] dayNames = context.getResources()
                .getStringArray(R.array.days_of_the_week);
        String[] daySeqValues = context.getResources()
                .getStringArray(R.array.days_of_the_week_values);

        //populate Day data
        String populateDaysSql = "insert into " +
                TimeGoalieContract.Days.DAYS_TABLE_NAME +
                "(" + TimeGoalieContract.Days.DAYS_COLUMN_NAME +
                ", " + TimeGoalieContract.Days.DAYS_COLUMN_SEQUENCE +
                ") " + "VALUES ";
        for (int i = 0; i < dayNames.length; i++) {
           populateDaysSql = populateDaysSql.concat(" (");
          populateDaysSql =  populateDaysSql.concat(buildInsertSqlfromStringArray
                    (new String[]{'"'+dayNames[i]+'"', daySeqValues[i]}));
            if (i!=dayNames.length-1) {
                populateDaysSql = populateDaysSql.concat("), ");
            }
            else{
                populateDaysSql = populateDaysSql.concat(")");
            }
        }
        db.execSQL(populateDaysSql);

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
                        .GOALS_DATES_ACCOMPLISHED_COLUMN_DATE + " TEXT NOT NULL);";
        db.execSQL(createGoalsDatesAccomplishedSQL);



        //DUMMY DATA
        // TODO: 10/6/2017 remove when done
        db.execSQL("insert into goalentries (goal_id,timestamp,seconds_elapsed, succeeded) VALUES (1, '2017-10-05',3000,0)");

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

    public String buildInsertSqlfromStringArray(String[] array){
        String returnString="";
        for (int i = 0; i < array.length; i++) {
            if (i!=array.length-1){
              returnString = returnString.concat(array[i])
                        .concat(",");
            }
            else{
                returnString = returnString.concat(array[i]);
            }
        }
        return returnString;
    }
}
